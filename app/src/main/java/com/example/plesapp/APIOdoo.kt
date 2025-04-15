package com.example.plesapp

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import org.json.JSONException


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

//aqui vam tus rutas generadas desde tu modulo api(Odoo)
class ApiService(private val context: Context) {
    private val clientAPI = OkHttpClient()
    private val apiKey = "API"
    private val baseUrl = "Sitio web"
    private val authUrl = "$baseUrl/web/session/authenticate"
    private val registerUrl = "$baseUrl/api/create_contact"
    private val productsUrl = "$baseUrl/api/products"
    private val database = "db"
    private val email = "usuario"
    private val password = "pass"

    private var sessionId: String? = null

    suspend fun authenticate(): String {
        sessionId?.let { return it }

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
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = clientAPI.newCall(authRequest).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) throw Exception("Error de autenticación: ${response.code}")

                val jsonResponse = JSONObject(responseBody)
                sessionId = jsonResponse.optJSONObject("result")?.optString("session_id")

                sessionId ?: throw Exception("No se encontró session_id en la respuesta.")
            } catch (e: Exception) {
                Log.e("ApiService", "Error en autenticación: ${e.message}")
                throw e
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
        esUsuarioApp: Boolean,
        barcode: String
    ): Boolean {
        val sessionId = authenticate()

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
            put("barcode", barcode)
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


    suspend fun getAllProducts(): Result<List<Product>> {
        val sessionId = authenticate()
        val request = Request.Builder()
            .url(productsUrl)
            .get()
            .addHeader("Cookie", "session_id=$sessionId")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = clientAPI.newCall(request).execute()
                val responseBody = response.body?.string() ?: return@withContext Result.failure(Exception("Respuesta vacía"))

                if (!response.isSuccessful) {
                    Log.e("ApiService", "Error ${response.code}: ${response.message}")
                    return@withContext Result.failure(Exception("Error de red: ${response.code}"))
                }

                val jsonResponse = JSONObject(responseBody)
                if (!jsonResponse.optBoolean("success", false)) {
                    return@withContext Result.failure(Exception("La API no devolvió éxito"))
                }

                val productsJsonArray = jsonResponse.optJSONArray("products") ?: return@withContext Result.failure(Exception("No se encontraron productos."))

                val productList = mutableListOf<Product>()
                for (i in 0 until productsJsonArray.length()) {
                    val productJson = productsJsonArray.getJSONObject(i)
                    productList.add(
                        Product(
                            id = productJson.getInt("id"),
                            name = productJson.getString("name"),
                            listPrice = productJson.getDouble("price"),
                            qtyAvailable = productJson.getInt("stock")
                        )
                    )
                }

                Result.success(productList)
            } catch (e: IOException) {
                Log.e("ApiService", "Error de red: ${e.message}")
                Result.failure(e)
            } catch (e: JSONException) {
                Log.e("ApiService", "Error al parsear la respuesta: ${e.message}")
                Result.failure(e)
            }
        }
    }

}








