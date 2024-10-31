package com.example.plesapp

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


//animacion de inicio
@Composable
fun AnimatedSplashScreen() {
    val descendingTextPosition by animateDpAsState(
        targetValue = 50.dp,
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
    )
    val ascendingTextPosition by animateDpAsState(
        targetValue = (-50).dp,
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfiliateForm(navController: NavHostController) {
    // Variables de estado para los campos
    var nombre by remember { mutableStateOf("") }
    var domicilio by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Función para cargar los datos guardados en SharedPreferences
    fun loadDataFromSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("AfiliatePrefs", Context.MODE_PRIVATE)
        nombre = sharedPreferences.getString("name", "") ?: ""
        domicilio = sharedPreferences.getString("domicilio", "") ?: ""
        sexo = sharedPreferences.getString("sexo", "") ?: ""
        curp = sharedPreferences.getString("curp", "") ?: ""
        fechaNacimiento = sharedPreferences.getString("fechaNacimiento", "") ?: ""
        telefono = sharedPreferences.getString("telefono", "") ?: ""
        correo = sharedPreferences.getString("correo", "") ?: ""
        // Nota: No cargamos la contraseña por razones de seguridad

        Log.d(
            "AfiliateForm",
            "Datos cargados: Nombre=$nombre, Domicilio=$domicilio, Sexo=$sexo, CURP=$curp, FechaNacimiento=$fechaNacimiento, Telefono=$telefono, Correo=$correo"
        )
    }

    // Llamar a la función para cargar los datos cuando la Composable se inicie
    LaunchedEffect(Unit) {
        loadDataFromSharedPreferences()
    }

    // Función para validar el formulario
    fun isFormValid(): Boolean {
        return when {
            nombre.isBlank() -> {
                message = "El nombre es obligatorio."
                isError = true
                false
            }

            domicilio.isBlank() -> {
                message = "El domicilio es obligatorio."
                isError = true
                false
            }

            sexo.isBlank() -> {
                message = "El sexo es obligatorio."
                isError = true
                false
            }

            curp.isBlank() -> {
                message = "El CURP es obligatorio."
                isError = true
                false
            }

            curp.length != 18 -> { // CURP estándar tiene 18 caracteres
                message = "El CURP debe tener 18 caracteres."
                isError = true
                false
            }

            fechaNacimiento.isBlank() -> {
                message = "La fecha de nacimiento es obligatoria."
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

            correo.isBlank() -> {
                message = "El correo es obligatorio."
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

    // Función para guardar todos los datos en SharedPreferences
    fun saveDataToSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("AfiliatePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", nombre)
        editor.putString("domicilio", domicilio)
        editor.putString("sexo", sexo)
        editor.putString("curp", curp)
        editor.putString("fechaNacimiento", fechaNacimiento)
        editor.putString("telefono", telefono)
        editor.putString("correo", correo)
        // No guardamos la contraseña por razones de seguridad
        editor.apply()
    }

    // Función para enviar el formulario
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
                    companyId = 1,
                    domicilio = domicilio,
                    sexo = sexo,
                    curp = curp,
                    fechaNacimiento = fechaNacimiento
                )

                if (response) {
                    saveDataToSharedPreferences()
                    "Usuario creado exitosamente"
                } else {
                    "Error al crear el usuario"
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

    // UI del formulario
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Contenido con scroll
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text("Registra tus datos", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de entrada (Nombre, Domicilio, etc.)
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
                value = domicilio,
                onValueChange = { domicilio = it },
                label = { Text("Domicilio") },
                modifier = Modifier.fillMaxWidth(),
                isError = domicilio.isBlank(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            SexoSelector(sexo = sexo, onSexoSeleccionado = { sexo = it }, enabled = !isLoading)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = curp,
                onValueChange = { curp = it },
                label = { Text("CURP") },
                modifier = Modifier.fillMaxWidth(),
                isError = curp.isBlank() || curp.length != 18,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            FechaNacimientoField(
                fechaNacimiento = fechaNacimiento,
                onFechaSeleccionada = { fechaNacimiento = it },
                enabled = !isLoading
            )


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                isError = correo.isBlank(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading
            )

            // Fila para los botones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (!isLoading) {
                            submitForm()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp), // Espaciado entre botones
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

                Button(
                    onClick = { navController.navigate("camera") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726),
                        contentColor = Color.White
                    )
                ) {
                    Text("Escanea tu INE")
                }
            }


            // Spacer grande para permitir el scroll
            Spacer(modifier = Modifier.height(50.dp))

            // Diálogo de mensaje
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
    }
}

    @Composable
fun SexoSelector(
    sexo: String,
    onSexoSeleccionado: (String) -> Unit,
    enabled: Boolean = true
) {
    val opcionesSexo = listOf("Masculino", "Femenino", "Otro")
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = sexo,
            onValueChange = {},
            label = { Text("Sexo") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { if (enabled) expanded = true },
            enabled = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar Sexo",
                    modifier = Modifier.clickable(enabled = enabled) { if (enabled) expanded = true }
                )
            },
            isError = sexo.isBlank()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            opcionesSexo.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSexoSeleccionado(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaNacimientoField(
    fechaNacimiento: String,
    onFechaSeleccionada: (String) -> Unit,
    enabled: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    // Estado del DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    // Mostrar el DatePicker cuando showDatePicker sea verdadero
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Selecciona una fecha") },
            text = {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        onFechaSeleccionada(sdf.format(Date(selectedDateMillis)))
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column {
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = {}, // Mantener vacío ya que no se puede editar
            label = { Text("Fecha de Nacimiento (dd/mm/yyyy)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}, // Desactivar el clic en el TextField
            enabled = false, // Hacer el TextField no editable
            isError = fechaNacimiento.isBlank(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Seleccionar Fecha",
                    modifier = Modifier
                        .clickable(enabled = enabled) { if (enabled) showDatePicker = true }
                        .padding(16.dp)
                )
            }
        )
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



