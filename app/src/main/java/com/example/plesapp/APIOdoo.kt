package com.example.plesapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@Serializable
data class RecordPartner(
    val id: Int,
    val name: String?,
    val email: String?,
    val phone: String?,
)

@Serializable
data class ApiResponsePartners(
    val partners: List<RecordPartner>
)


class ApiService(private val context: Context) {
    private val clientAPI = OkHttpClient()
    private val apiKey: String = "500845c5-dc6d-49ca-b394-eeae21b75409"
    private val usuario: String = "admin"
    private val contrasena: String = "admin"
    private val authUrl = "https://pruebas.stples.mx/odoo_connect"
    private val registerUrl = "https://pruebas.stples.mx/api/create_portal_user"
    private val host = "pruebas.stples.mx"
    private fun saveSessionCookie(context: Context, sessionCookie: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("sessionCookie", sessionCookie)
            apply()
        }
    }

    suspend fun authenticate(): String {
        val authRequest = Request.Builder()
            .url(authUrl)
            .addHeader("db", "afiliacion")
            .addHeader("login", usuario)
            .addHeader("password", contrasena)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val authResponse = clientAPI.newCall(authRequest).execute()
                if (authResponse.isSuccessful) {
                    val cookies = authResponse.headers("Set-Cookie")
                    for (cookie in cookies) {
                        if (cookie.startsWith("session_id")) {
                            val endIndex = cookie.indexOf(";")
                            val sessionCookie = if (endIndex != -1) {
                                "session_id=" + cookie.substringAfter("session_id=")
                                    .substringBefore(";")
                            } else {
                                "session_id=" + cookie.substringAfter("session_id=")
                            }
                            saveSessionCookie(context, sessionCookie)
                            break
                        }
                    }
                    "Authentication successful"
                } else {
                    "Authentication failed: ${authResponse.code}"
                }
            } catch (e: IOException) {
                "Authentication request failed: ${e.message}"
            }
        }
    }

    suspend fun createPortalUser(
        email: String,
        name: String,
        password: String,
        phone: String,
        companyId: Int
    ): Boolean {

        val url = registerUrl
        println("Using Register URL: $url")

        val jsonBody = JSONObject().apply {
            put("email", email)
            put("name", name)
            put("password", password)
            put("phone", phone)
            put("company_id", companyId)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        println("Request Body: $jsonBody")

        val requestBuilder = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("login", usuario)
            .addHeader("password", contrasena)
            .addHeader("api-key", apiKey)
            .addHeader("Content-Type", "application/json")

        return withContext(Dispatchers.IO) {
            try {
                val response = clientAPI.newCall(requestBuilder.build()).execute()
                val responseBody = response.body?.string()

                println("Response code: ${response.code}")
                println("Response body: $responseBody")

                if (response.isSuccessful) {
                    responseBody?.contains("User created successfully") == true
                } else {
                    println("Request failed with error code: ${response.code}")
                    // Add more details about the response
                    println("Error Response: $responseBody")
                    false
                }
            } catch (e: IOException) {
                println("Request failed: ${e.message}")
                e.printStackTrace()  // Print stack trace for more detail
                false
            }
        }
    }




}