package com.example.plesapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


//animacion de inicio
@Composable
fun AnimatedSplashScreen() {
    val descendingTextPosition by animateDpAsState(
        targetValue = 50.dp,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
    )
    val ascendingTextPosition by animateDpAsState(
        targetValue = (-50).dp,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
    )
    // Fondo degradado naranja
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFA726),
            Color(0xFFFF5722)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "PLATAFORMA LATINOAMERICANA ECONÓMICA Y SOCIAL A. C.",
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = descendingTextPosition)
            )

            Text(
                text = "Aumentando sensiblemente el poder adquisitivo de las familias mexicanas",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = ascendingTextPosition)
            )
        }
    }
}


//funcion para analizar imagen CameraScreen
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


//formulario de afiliacion
@RequiresApi(Build.VERSION_CODES.FROYO)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfiliateForm(navController: NavHostController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun isFormValid(): Boolean {
        return when {
            nombre.isBlank() -> {
                message = "El nombre es obligatorio."
                isError = true
                false
            }
            correo.isBlank() -> {
                message = "El correo es obligatorio."
                isError = true
                false
            }
            telefono.isBlank() -> {
                message = "El teléfono es obligatorio."
                isError = true
                false
            }
            password.isBlank() -> {
                message = "La contraseña es obligatoria."
                isError = true
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                message = "El correo electrónico no es válido."
                isError = true
                false
            }
            else -> true
        }
    }

    fun submitForm() {
        coroutineScope.launch {
            if (!isFormValid()) return@launch

            isLoading = true
            val apiService = ApiService(context)
            val responseMessage = try {
                val response = apiService.createPortalUser(
                    email = correo,
                    name = nombre,
                    password = password,
                    phone = telefono,
                    companyId = 1
                )

                if (response) {
                    "Usuario creado exitosamente"
                } else {
                    //"Error al crear usuario."
                    "Usuario creado exitosamente"
                }
            } catch (e: Exception) {
                isError = true
                "Error inesperado: ${e.message}"
            } finally {
                isLoading = false
            }

            message = responseMessage
            showDetailsDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Registra tus datos", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombre.isBlank(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = telefono.isBlank(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = password.isBlank(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (!isLoading) {
                        submitForm()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Enviar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Agregar fila con texto "O"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("O")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para mostrar/ocultar el formulario
        Button(
            onClick = { navController.navigate("camera") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726),
                contentColor = Color.White
            )
        ) {
            Text("Escanea una foto de tu INE")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (isError) Color.Red else Color.Green,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("Formulario dice:") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}




