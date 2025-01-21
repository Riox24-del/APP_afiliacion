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
import java.net.HttpURLConnection
import java.net.URL


@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class Product(
    val name: String,
    val description: String,
    val Imagen: String
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ApiResponseProducts(
    val products: List<Product>
)


class ApiService(private val context: Context) {
    private val clientAPI = OkHttpClient()
    private val apiKey = "6f132e9b7b1f769b63908700291ae6d657c94488"
    private val authUrl = "https://plesmx.com/web/session/authenticate"
    private val registerUrl = "https://plesmx.com/api/create_portal_user"
    private val productsUrl = "https://plesmx.com/products/available"
    private val database = "ples"
    private val email = "administrador@plesmx.com"
    private val password = "admin"

    // Función para autenticar
    suspend fun authenticate(): String {
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
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    val sessionId = JSONObject(responseBody)
                        .getJSONObject("result")
                        .getString("session_id")
                    sessionId
                } else {
                    throw Exception("Error de autenticación: ${response.code}")
                }
            } catch (e: IOException) {
                throw Exception("Error de red: ${e.message}")
            }
        }
    }

// Crear un usuario en el portal
suspend fun createPortalUser(
    email: String,
    name: String,
    password: String,
    phone: String,
    companyId: Int,
    domicilio: String,
    sexo: String,
    curp: String,
    fechaNacimiento: String,
    tieneTarjetaFisica: Boolean,
): Boolean {
    val sessionId = authenticate()
    val jsonBody = JSONObject().apply {
        put("email", email)
        put("password", password)
        put("name", name)
        put("phone", phone)
        put("company_id", companyId)
        put("x_studio_domicilio_2", domicilio)
        put("x_studio_sexo", sexo)
        put("x_studio_curp", curp)
        put("x_studio_fechanacimiento", fechaNacimiento)
        put("x_studio_tiene_tarjeta_fisica", tieneTarjetaFisica)
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

    // Obtener todos los productos habilitados (sin filtro de disponibilidad)
    suspend fun getAllProducts(): List<Product>? {
        val sessionId = authenticate()  // Obtener el session_id
        val request = Request.Builder()
            .url(productsUrl)  // URL de la API que devuelve todos los productos
            .get()
            .addHeader("Authorization", "Bearer $apiKey")  // Autorización con la clave API
            .addHeader("session_id", sessionId)  // Enviar session_id
            .build()

        return withContext(Dispatchers.IO) {
            try {
                // Realizar la solicitud
                val response = clientAPI.newCall(request).execute()

                // Verificar si la respuesta es exitosa
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: return@withContext null
                    val productsJsonArray = JSONArray(responseBody)
                    val productList = mutableListOf<Product>()

                    // Procesar cada producto en la respuesta JSON
                    for (i in 0 until productsJsonArray.length()) {
                        val productJson = productsJsonArray.getJSONObject(i)
                        val product = Product(
                            name = productJson.getString("name"),
                            description = productJson.getString("description"),
                            Imagen = productJson.optString("image", "") // Si no hay imagen, devolver cadena vacía
                        )
                        productList.add(product)
                    }

                    productList // Devolver la lista completa de productos
                } else {
                    // En caso de error en la respuesta
                    println("Error: ${response.code} - ${response.message}")
                    null
                }
            } catch (e: IOException) {
                // Manejo de excepciones en la llamada de red
                println("Error al obtener productos: ${e.message}")
                null
            } catch (e: Exception) {
                // Capturar otros errores generales
                println("Error inesperado: ${e.message}")
                null
            }
        }
    }

}










