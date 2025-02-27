package com.example.plesapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Checkbox
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


//animacion de inicio
@Composable
fun AnimatedSplashScreen() {
    // Estado de la animación
    var startAnimation by remember { mutableStateOf(false) }
    val topTextPosition by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-700).dp, // Desde arriba hacia el centro
        animationSpec = tween(durationMillis = 6000, easing = LinearOutSlowInEasing)
    )
    val bottomTextPosition by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 300.dp, // Desde abajo hacia el centro
        animationSpec = tween(durationMillis = 6000, easing = LinearOutSlowInEasing)
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    // Fondo degradado naranja
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFA726),
            Color(0xFFFF5722)
        )
    )

    // Inicia la animación automáticamente
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .graphicsLayer(alpha = alpha), // Aparecen progresivamente
        contentAlignment = Alignment.Center
    ) {
        // Texto que se mueve desde arriba
        Text(
            text = "PLATAFORMA LATINOAMERICANA ECONÓMICA Y SOCIAL A. C.",
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(y = topTextPosition)
        )

        // Texto que se mueve desde abajo
        Text(
            text = "Aumentando sensiblemente el poder adquisitivo de las familias mexicanas",
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(y = bottomTextPosition)
        )
    }
}


//funcion para analizar imagen en la cameraScreen
suspend fun analyzeImage(context: Context, uri: Uri): Map<String, String> {
    return try {
        val image = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val visionText = recognizer.process(image).await()
        Log.d("CameraScreen", "OCR completado: ${visionText.text}")

        val extractedInfo = mutableMapOf<String, String>()
        var currentTag: String? = null
        var currentValue = StringBuilder()

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text.trim()

                when {
                    lineText.lowercase().startsWith("nombre") && "Nombre" !in extractedInfo -> {
                        if (currentTag != null) {
                            extractedInfo[currentTag] = currentValue.toString().trim()
                        }
                        currentTag = "Nombre"
                        currentValue = StringBuilder(lineText.substringAfter("nombre", "").trim())
                    }
                    lineText.lowercase().startsWith("domicilio") && "Domicilio" !in extractedInfo -> {
                        if (currentTag != null) {
                            extractedInfo[currentTag] = currentValue.toString().trim()
                        }
                        currentTag = "Domicilio"
                        currentValue = StringBuilder(lineText.substringAfter("domicilio", "").trim())
                    }

                            // Detectar "Clave de Elector"
                            lineText.lowercase().startsWith("clave de elector") && "Clave de Elector" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Clave de Elector"
                                currentValue = StringBuilder(lineText.substringAfter("clave de elector", "").trim())
                            }

                            // Detectar "CURP"
                            lineText.lowercase().startsWith("curp") && "CURP" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "CURP"
                                currentValue = StringBuilder(lineText.substringAfter("curp", "").trim())
                            }

                            // Detectar "Estado"
                            lineText.lowercase().startsWith("estado") && "Estado" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Estado"
                                currentValue = StringBuilder(lineText.substringAfter("estado", "").trim())
                            }

                            // Detectar "Localidad"
                            lineText.lowercase().startsWith("localidad") && "Localidad" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Localidad"
                                currentValue = StringBuilder(lineText.substringAfter("localidad", "").trim())
                            }

                            // Detectar "Municipio"
                            lineText.lowercase().startsWith("municipio") && "Municipio" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Municipio"
                                currentValue = StringBuilder(lineText.substringAfter("municipio", "").trim())
                            }

                            // Detectar "Emisión"
                            lineText.lowercase().startsWith("emision") && "Emisión" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Emisión"
                                currentValue = StringBuilder(lineText.substringAfter("emision", "").trim())
                            }

                            // Detectar "Sección"
                            lineText.lowercase().startsWith("sección") && "Sección" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Sección"
                                currentValue = StringBuilder(lineText.substringAfter("sección", "").trim())
                            }

                            // Detectar "Vigencia"
                            lineText.lowercase().startsWith("vigencia") && "Vigencia" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Vigencia"
                                currentValue = StringBuilder(lineText.substringAfter("vigencia", "").trim())
                            }

                            // Detectar "Año de Registro"
                            lineText.lowercase().startsWith("año de registro") && "Año de Registro" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Año de Registro"
                                currentValue = StringBuilder(lineText.substringAfter("año de registro", "").trim())
                            }

                            // Detectar "Fecha de Nacimiento"
                            lineText.lowercase().startsWith("fecha de nacimiento") && "Fecha de Nacimiento" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Fecha de Nacimiento"
                                currentValue = StringBuilder(lineText.substringAfter("fecha de nacimiento", "").trim())
                            }

                            // Detectar "Sexo"
                            lineText.lowercase().startsWith("sexo") && "Sexo" !in extractedInfo -> {
                                if (currentTag != null) {
                                    extractedInfo[currentTag] = currentValue.toString().trim()
                                }
                                currentTag = "Sexo"
                                currentValue = StringBuilder(lineText.substringAfter("sexo", "").trim())
                            }

                    else -> {
                        if (currentTag != null) {
                            currentValue.append(" ").append(lineText)
                        }
                    }
                }
            }
        }

        // Añadir el último campo procesado
        if (currentTag != null) {
            extractedInfo[currentTag] = currentValue.toString().trim()
        }

        Log.d("CameraScreen", "Información extraída: $extractedInfo")
        extractedInfo
    } catch (e: Exception) {
        Log.e("CameraScreen", "Error al analizar la imagen: ${e.localizedMessage}")
        throw e
    }
}


//para el checkmark
fun saveCheckboxState(context: Context, isChecked: Boolean) {
    val sharedPreferences = context.getSharedPreferences("PoliticaPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putBoolean("isChecked", isChecked).apply()
}

fun getCheckboxState(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("PoliticaPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isChecked", false) // Devuelve false si no se encuentra el valor
}


//para actualizar el horario
fun getBusinessStatus(): String {
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)

    // Convertir la hora actual a minutos desde la medianoche
    val currentMinutes = currentHour * 60 + currentMinute

    return when (currentDay) {
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY -> {
            val openingMinutes = 9 * 60 // 9:00 AM
            val closingMinutes = 17 * 60 // 5:00 PM
            if (currentMinutes in openingMinutes until closingMinutes) "Abierto ahora" else "Cerrado ahora"
        }
        Calendar.SATURDAY -> {
            val openingMinutes = 9 * 60 // 9:00 AM
            val closingMinutes = 14 * 60 // 2:00 PM
            if (currentMinutes in openingMinutes until closingMinutes) "Abierto ahora" else "Cerrado ahora"
        }
        else -> {
            "Cerrado ahora" // Domingo
        }
    }
}

//muestra el estado de abierto o cerrado
@Composable
fun BusinessStatusText() {
    val status = getBusinessStatus()

    Text(
        text = status,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = if (status == "Abierto ahora") Color(0xFF4CAF50) else Color(0xFFF44336),
            textAlign = TextAlign.Center
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

// Función para decodificar la imagen de base64 a Bitmap
fun decodeBase64ToBitmap(base64: String): Bitmap? {
    return try {
        val base64Cleaned = base64.substringAfter(",") // Elimina el prefijo MIME si está presente
        val decodedBytes = Base64.decode(base64Cleaned, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: IllegalArgumentException) {
        Log.e("Base64Decode", "Error al decodificar Base64: ${e.message}")
        null
    }
}


