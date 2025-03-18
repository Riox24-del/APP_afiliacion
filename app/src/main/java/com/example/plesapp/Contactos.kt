package com.example.plesapp

import android.content.Context
import android.Manifest

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//formulario de afiliacion
@Composable
fun AfiliateForm(navController: NavHostController) {
    // Variables de estado para los campos
    var nombre by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var tieneTarjetaFisica by remember { mutableStateOf(false) }
    var esUsuarioApp by remember { mutableStateOf(false) }
    var barcode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
// Variable para mostrar el diálogo de políticas
    var showPolicyDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val hasAcceptedPolicies = remember { mutableStateOf(false) }
    val cameraPermission = Manifest.permission.CAMERA

    // Función para cargar los datos guardados en SharedPreferences
    fun loadDataFromSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("AfiliatePrefs", Context.MODE_PRIVATE)
        nombre = sharedPreferences.getString("name", "") ?: ""
        sexo = sharedPreferences.getString("sexo", "") ?: ""
        curp = sharedPreferences.getString("curp", "") ?: ""
        fechaNacimiento = sharedPreferences.getString("fechaNacimiento", "") ?: ""
        telefono = sharedPreferences.getString("telefono", "") ?: ""
        correo = sharedPreferences.getString("correo", "") ?: ""
        barcode = sharedPreferences.getString("barcode", "") ?: ""

        //log para depuracion
        Log.d(
            "AfiliateForm",
            "Datos cargados: Nombre=$nombre, Sexo=$sexo, CURP=$curp, FechaNacimiento=$fechaNacimiento, Telefono=$telefono, Correo=$correo, Barcode=$barcode"
        )
    }

    // Lanzador para solicitar permisos
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate("camera")
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Llamar a la función para cargar los datos cuando el Composable se inicie
    LaunchedEffect(Unit) {
        loadDataFromSharedPreferences()
    }

    // Función para validar el formulario
    fun isFormValid(): Boolean {
        val isNombreValid = nombre.isNotBlank()
        val isSexoValid = sexo.isNotBlank()
        val isCurpValid = curp.isNotBlank() && curp.length == 18
        val isFechaNacimientoValid = fechaNacimiento.isNotBlank()
        val isTelefonoValid =
            telefono.isNotBlank() && telefono.length == 10 && telefono.all { it.isDigit() }
        val isCorreoValid =
            correo.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
        val isBarcodeValid = barcode.isNotBlank()

        isError =
            !(isNombreValid &&  isSexoValid && isCurpValid && isFechaNacimientoValid &&
                    isTelefonoValid && isCorreoValid  && isBarcodeValid)

        message = when {
            !isNombreValid -> "El nombre es obligatorio."

            !isSexoValid -> "El sexo es obligatorio."
            !isCurpValid -> "El CURP debe tener 18 caracteres."
            !isFechaNacimientoValid -> "La fecha de nacimiento es obligatoria."
            !isTelefonoValid -> "El teléfono debe tener 10 dígitos numéricos."
             !isCorreoValid -> "El correo electrónico no es válido."
            !isBarcodeValid -> "El código de barras es obligatorio y unico"
            else -> ""
        }

        return !isError
    }

    // Función para guardar todos los datos en SharedPreferences
    fun saveDataToSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("AfiliatePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", nombre)
        editor.putString("sexo", sexo)
        editor.putString("curp", curp)
        editor.putString("fechaNacimiento", fechaNacimiento)
        editor.putString("telefono", telefono)
        editor.putString("correo", correo)
        editor.putString("barcode", barcode)

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
                    email = correo.ifBlank { null }.toString(),
                    name = nombre,
                    phone = telefono,
                    sexo = sexo,
                    curp = curp,
                    fechaNacimiento = fechaNacimiento,
                    tieneTarjetaFisica = tieneTarjetaFisica,
                    esUsuarioApp = true,
                    barcode = barcode
                )
                if (response) {
                    saveDataToSharedPreferences()
                    "Usuario creado exitosamente"
                } else {
                    //  "Error al crear el usuario"
                    "Error: Curp, Telefono, Correo o Código de barras repetido(s)"
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text("Registra tus datos", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = isError && nombre.isBlank(),
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
                isError = isError && (curp.isBlank() || curp.length != 18),
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
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                isError = isError && (telefono.isBlank() || telefono.length != 10 || !telefono.all { it.isDigit() }),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = barcode,
                onValueChange = {barcode = it},
                label = {Text("Código de barras")},
                modifier = Modifier.fillMaxWidth(),
                isError = isError && barcode.isBlank(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tieneTarjetaFisica,
                    onCheckedChange = { tieneTarjetaFisica = it }
                )
                Text("¿Tiene tarjeta física?")
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (isError) {
                Text(
                    text = message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

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
                        .padding(bottom = 8.dp),
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

                // Botón para abrir la cámara
                Button(
                    onClick = { launcher.launch(cameraPermission) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726),
                        contentColor = Color.White
                    )
                ) {
                    Text("Escanea tu INE")
                }
            }

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
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato de entrada
    val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato de salida
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
                        val selectedDate = Date(selectedDateMillis)
                        // Convertir la fecha seleccionada a 'yyyy-MM-dd'
                        val formattedDate = outputSdf.format(selectedDate)
                        // Pasar la fecha formateada
                        onFechaSeleccionada(formattedDate)
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
