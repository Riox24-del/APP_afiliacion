package com.example.plesapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@Serializable
data class RecordPartner(
    val id: Int,
    val name: String?,
    val email: String?,
    val phone: String?,
    @SerialName("password_app")
    val passwordApp: String?
)
@Serializable
data class ApiResponsePartners(
    val records: List<RecordPartner>
)


class ApiService(private val context: Context) {
    private val clientAPI = OkHttpClient()
    private val apiKey: String = "500845c5-dc6d-49ca-b394-eeae21b75409"
    private val usuario: String = "admin"
    private val contrasena: String = "admin"
    private val authUrl = "https://pruebas.stples.mx/odoo_connect"
    private val host = "pruebas.stples.mx"

    private fun saveSessionCookie(context: Context, sessionCookie: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("sessionCookie", sessionCookie)
            apply()
        }
    }

    private fun getSessionCookie(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("sessionCookie", null)
    }



    suspend fun authenticate(): String {
        val authRequest = Request.Builder()
            .url(authUrl)
            .addHeader("db", "inversiones")
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
                                "session_id=" + cookie.substringAfter("session_id=").substringBefore(";")
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

    suspend fun getApiPartners(): ApiResponsePartners? {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegment("send_request")
            .addQueryParameter("model", "res.partner")
            .addQueryParameter("fields", "name")
            .addQueryParameter("fields", "email")
            .addQueryParameter("fields", "phone")
            .addQueryParameter("fields", "password_app")
            .build()

        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("login", usuario)
            .addHeader("password", contrasena)
            .addHeader("api-key", apiKey)

        val sessionCookie = getSessionCookie(context)
        sessionCookie?.let {
            requestBuilder.addHeader("Cookie", it)
        }

        val request = requestBuilder.build()

        return withContext(Dispatchers.IO) {
            try {
                val response = clientAPI.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Json.decodeFromString<ApiResponsePartners>(responseBody)
                    }
                } else {
                    println("Request failed: ${response.code}")
                    null
                }
            } catch (e: IOException) {
                println("Request failed: ${e.message}")
                null
            }
        }
    }
}
