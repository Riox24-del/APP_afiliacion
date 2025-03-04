package com.example.plesapp

import android.R.attr.apiKey
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.*


@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class Product(
    val id: Int,
    val name: String,
    val listPrice: Double,
    val qtyAvailable: Int
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ApiResponseProducts(
    val products: List<Product>
)


class ApiService(private val context: Context) {
    private val clientAPI = OkHttpClient()
    private val apiKey = "d603725815bef5701e3769f95e2402d2d7412715"
    private val baseUrl = "https://plesmx.com"
    private val authUrl = "$baseUrl/web/session/authenticate"
    private val registerUrl = "$baseUrl/api/create_contact"
    private val productsUrl = "$baseUrl/api/products"
    private val database = "ples"
    private val email = "administrador@plesmx.com"
    private val password = "admin"

    private var sessionId: String? = null

    // Método para autenticar al usuario y obtener el session_id
    suspend fun authenticate(): String {
        if (sessionId != null) return sessionId!!

        val json = """
        {
            "jsonrpc": "2.0",
            "method": "call",
            "params": {
                "db": "$database",
                "login": "$email",
                "password": "$password"
            }
        }
        """.trimIndent()

        val authRequest = Request.Builder()
            .url(authUrl)
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = clientAPI.newCall(authRequest).execute()
                val responseBody = response.body?.string() ?: ""
                Log.d("ApiService", "Respuesta de autenticación: $responseBody")

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBody)
                    if (jsonResponse.has("result") && jsonResponse.getJSONObject("result").has("session_id")) {
                        sessionId = jsonResponse.getJSONObject("result").getString("session_id")
                        sessionId!!
                    } else {
                        throw Exception("Error: no se encontró session_id en la respuesta.")
                    }
                } else {
                    throw Exception("Error de autenticación: ${response.code}")
                }
            } catch (e: IOException) {
                throw Exception("Error de red: ${e.message}")
            }
        }
    }

    suspend fun createPortalUser(
        name: String,
        email: String,
        phone: String,
        sexo: String,
        curp: String,
        fechaNacimiento: String,
        tieneTarjetaFisica: Boolean,
        esUsuarioApp: Boolean
    ): Boolean {
        val sessionId = "d73861a448249be383579ee02ae6ad87602f54ed"

        if (sessionId.isNullOrBlank()) {
            throw Exception("Error: la sesión no esta disponible.")
        }
        val jsonBody = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("phone", phone)
            put("sexo", sexo)
            put("curp", curp)
            put("fechaNacimiento", fechaNacimiento)
            put("tieneTarjetaFisica", tieneTarjetaFisica)
            put("esUsuarioApp", esUsuarioApp)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(registerUrl)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("session_id", sessionId)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = clientAPI.newCall(request).execute()
                response.isSuccessful
            } catch (e: IOException) {
                println("Error al crear usuario: ${e.message}")
                false
            }
        }
    }


    suspend fun getAllProducts(): List<Product>? {
        val sessionId = "d73861a448249be383579ee02ae6ad87602f54ed"
        val apiUrl = productsUrl

        val request = Request.Builder()
            .url(apiUrl) 
            .get()
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("session_id", sessionId)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = clientAPI.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: return@withContext null
                    println("Respuesta de la API de productos: $responseBody")

                    
                    val responseJson = JSONObject(responseBody)
                    val result = responseJson.optJSONObject("result") 
                    val productsJsonArray = result?.optJSONArray("products") 

                   
                    if (productsJsonArray == null || productsJsonArray.length() == 0) {
                        println("No se encontraron productos.")
                        return@withContext null
                    }

                    val productList = mutableListOf<Product>()
                    for (i in 0 until productsJsonArray.length()) {
                        val productJson = productsJsonArray.getJSONObject(i)
                        val product = Product(
                            id = productJson.getInt("id"),
                            name = productJson.getString("name"),
                            listPrice = productJson.getDouble("price"),
                            qtyAvailable = productJson.getDouble("stock").toInt()
                        )
                        productList.add(product)
                    }

                    productList 
                } else {
                    println("Error: ${response.code} - ${response.message}")
                    null
                }
            } catch (e: IOException) {
                println("Error al obtener productos: ${e.message}")
                null
            } catch (e: Exception) {
                println("Error inesperado: ${e.message}")
                null
            }
        }
    }



}





