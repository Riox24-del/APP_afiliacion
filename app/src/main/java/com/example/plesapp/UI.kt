package com.example.plesapp


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontStyle
import android.util.Base64
import androidx.activity.result.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun MainNavigation(userViewModel: UserViewModel, apiService: ApiService) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "greeting") {
        composable("greeting") { Greeting(navController) }
        composable("login") { LoginScreen(navController, userViewModel) }
        composable("mainApp") { MyApp(navController, userViewModel) }
        composable("inicio") { Inicio(navController, userViewModel) }
        composable("afiliate") { Afiliate(navController, userViewModel) }
        composable("subsidios") { Subsidios(navController, userViewModel, apiService) }
        composable("camera") { CameraScreen(navController) }
        composable("politicaInformacion") { PoliticaInformacionScreen(navController) }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp(navController: NavHostController, userViewModel: UserViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiService = remember { ApiService(context) }
    val responseText by remember { mutableStateOf("Response will be shown here") }
    val isLoading by remember { mutableStateOf(false) }
    var apiPartnersResponse by remember { mutableStateOf<ApiResponsePartners?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { MyAppNavBar(navController) }
    ) {

    }
}

@Composable
fun MyAppNavBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Inicio"
                )
            },
            modifier = Modifier.size(24.dp),
            label = { Text("Inicio") },
            selected = navController.currentDestination?.route == "inicio",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.document),
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Afiliate") },
            modifier = Modifier.size(24.dp),
            selected = navController.currentDestination?.route == "afiliate",
            onClick = { navController.navigate("afiliate") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.shop),
                    contentDescription = "Inicio"
                )
            },
            modifier = Modifier.size(24.dp),
            label = { Text("Subsidios") },
            selected = navController.currentDestination?.route == "subsidios",
            onClick = { navController.navigate("subsidios") }
        )
    }
}


@Composable
fun Greeting(navController: NavHostController) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000L)
        showSplash = false
        navController.navigate("inicio") {
            popUpTo("greeting") { inclusive = true }
        }
    }
    if (showSplash) {
        AnimatedSplashScreen()
    }
}

@RequiresApi(Build.VERSION_CODES.FROYO)
@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiService = remember { ApiService(context) }
    var errorEmail by remember { mutableStateOf("") }
    var errorPassword by remember { mutableStateOf("") }
    var errorLogin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA726)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorEmail.isNotEmpty(),
                colors = TextFieldDefaults.colors(
                    contentColorFor(backgroundColor = Color.White),
                    focusedIndicatorColor = Color(0xFFFFA726),
                    focusedLabelColor = Color(0xFFFFA726),
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            if (errorEmail.isNotEmpty()) {
                Text(text = errorEmail, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = errorPassword.isNotEmpty(),
                colors = TextFieldDefaults.colors(
                    contentColorFor(backgroundColor = Color.White),
                    focusedIndicatorColor = Color(0xFFFFA726),
                    focusedLabelColor = Color(0xFFFFA726),
                )
            )
            if (errorPassword.isNotEmpty()) {
                Text(text = errorPassword, color = Color.Red, fontSize = 12.sp)
            }

            if (errorLogin.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorLogin, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isLoading) {
                        scope.launch {
                            errorEmail = ""
                            errorPassword = ""
                            errorLogin = ""

                            // Validación de campos
                            if (email.isBlank()) {
                                errorEmail = "El correo electrónico no puede estar en blanco"
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                errorEmail = "Correo electrónico no válido"
                            }
                            if (password.isBlank()) {
                                errorPassword = "La contraseña no puede estar en blanco"
                            }


                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isLoading) "Cargando..." else "Acceder",
                    color = if (isLoading) Color.Gray else Color.White
                )
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Inicio(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = { MyAppNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen principal con el logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inicio),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "¡Síguenos en nuestras redes sociales!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFFFA726),
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botones debajo de la imagen
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedButton(
                    onClick = {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/plesmx"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFF2675AE)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2675AE)
                    )
                ) {
                    Text("Facebook")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://plesmx.com/"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFFFFA726)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFFA726)
                    )
                ) {
                    Text(
                        text = "Visita nuestra página web",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFFFA726)
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¿Ya estás afiliado?, Inicia Sesión",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF000000),
                       // fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = {
                        navController.navigate("login")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFFFFA726)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFFA726)
                    )
                ) {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Información de contacto en la parte inferior
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Lunes a Viernes de 09:00 a 19:00, Sábado de 09:00 a 14:00",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Privada de, C. Prolongación Eucaliptos 105, Ricardo Flores Magon, 68020 Oaxaca de Juárez, Oax.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Teléfono: 529511433017",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Afiliate(navController: NavHostController, userViewModel: UserViewModel) {
    var showCamera by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { MyAppNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Afiliate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            AfiliateForm(navController)
           /* if (showForm) {
                AfiliateForm()
            } */
        }
    }
}


@Composable
fun CameraScreen(navController: NavHostController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var analysisResult by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var imageLoadedMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Función para guardar los resultados en SharedPreferences
    fun saveDataToSharedPreferences(analysisResult: Map<String, String>) {
        val sharedPreferences = context.getSharedPreferences("AfiliatePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        analysisResult.forEach { (key, value) ->
            when (key) {
                "Nombre" -> editor.putString("name", value)
                "Domicilio" -> editor.putString("domicilio", value)
                "Sexo" -> editor.putString("sexo", value)
                "CURP" -> editor.putString("curp", value)
                "Fecha de Nacimiento" -> editor.putString("fechaNacimiento", value)
                "Teléfono" -> editor.putString("telefono", value)
                "Correo Electrónico" -> editor.putString("correo", value)
            }
        }
        editor.apply()
    }

    // Función auxiliar para guardar el Bitmap y obtener su Uri
    fun saveBitmapToFile(bitmap: Bitmap, context: Context): Uri? {
        return try {
            val filename = "temp_image_${System.currentTimeMillis()}.png"
            val file = File(context.cacheDir, filename)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            Log.d("CameraScreen", "Foto guardada en: $uri")
            uri
        } catch (e: IOException) {
            Log.e("CameraScreen", "Error al guardar la foto: ${e.localizedMessage}")
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            Log.e("CameraScreen", "Error de URI: ${e.localizedMessage}")
            e.printStackTrace()
            null
        }
    }

    // Lanzador para tomar una foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Log.d("CameraScreen", "Bitmap recibido de la cámara")
            val uri = saveBitmapToFile(bitmap, context)
            if (uri != null) {
                imageUri = uri
                imageLoadedMessage = "Foto tomada correctamente"
                errorMessage = null
                Log.d("CameraScreen", "Foto guardada en: $uri")
            } else {
                errorMessage = "Error al guardar la foto"
                imageLoadedMessage = null
                Log.e("CameraScreen", "URI nulo después de guardar la foto")
            }
        } else {
            errorMessage = "No se tomó ninguna foto"
            imageLoadedMessage = null
            Log.e("CameraScreen", "Bitmap nulo recibido de la cámara")
        }
    }

    // seleccionar una imagen desde el almacenamiento
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            errorMessage = null
            imageLoadedMessage = "Imagen cargada correctamente"
        } else {
            errorMessage = "No se seleccionó ninguna imagen"
            imageLoadedMessage = null
        }
    }

    // Permisos para acceder al almacenamiento
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            errorMessage = "Permiso denegado para acceder a almacenamiento"
        }
    }

    // Verificar permisos y lanzar el selector de archivos
    fun launchImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 y superior: Usar READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                imagePickerLauncher.launch("image/*")
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Android 12 y versiones anteriores: Usar READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                imagePickerLauncher.launch("image/*")
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    // Contenido de la UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón para tomar una foto
        Button(
            onClick = { cameraLauncher.launch() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726),
                contentColor = Color.White
            )
        ) {
            Text("Tomar Foto")
        }

        Text("O")

        // Botón para abrir el selector de archivos y elegir una imagen
        Button(
            onClick = { launchImagePicker() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726),
                contentColor = Color.White
            )
        ) {
            Text("Seleccionar Imagen de INE")
        }

        // Mostrar mensaje si la imagen ha sido cargada
        imageLoadedMessage?.let {
            Text(it, color = Color.Green)
        }

        // Mostrar la imagen seleccionada o tomada
        imageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = null,
                contentScale = ContentScale.Fit, // Ajusta a ContentScale.Fit para mantener la relación de aspecto
                modifier = Modifier
                    .fillMaxWidth() // Llenar el ancho disponible
                    .height(300.dp) // Establece una altura adecuada
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            )
        }


        imageUri?.let { uri ->
            // Mostrar el botón de "Analizar Imagen" solo si ya se ha tomado o seleccionado una imagen
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            isLoading = true
                            analysisResult = analyzeImage(context, uri)
                            errorMessage = null
                        } catch (e: Exception) {
                            errorMessage = "Error al analizar la imagen: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text("Analizar Imagen")
            }
        }

        // Mostrar indicador de carga
        if (isLoading) {
            CircularProgressIndicator()
        }

        // Mostrar resultados del análisis
        if (analysisResult.isNotEmpty()) {
            Text("Resultados del Análisis:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            analysisResult.forEach { (tag, value) ->
                Text("$tag:", fontWeight = FontWeight.Bold)
                Text(value, modifier = Modifier.padding(bottom = 8.dp))
            }
        }

        // Mostrar mensajes de error si ocurren
        errorMessage?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar el texto reconocido
        if (analysisResult.isNotEmpty()) {
            Button(
                onClick = {
                    saveDataToSharedPreferences(analysisResult)
                    navController.popBackStack()
                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text("Mis datos son correctos")
            }
            Text("Si sus datos son incorrectos, tome o cargue una nueva imagen")
            Text("Si algunos de sus datos no son aparecieron, guarde y agreguelos manualmente")
        }


        // Botón para leer la política de información
        TextButton(
            onClick = { navController.navigate("politicaInformacion") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFFFFA726) // Color del texto
            )
        ) {
            Text("Leer Política de Información", style = MaterialTheme.typography.bodySmall) // Estilo más discreto
        }


        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726),
                contentColor = Color.White
            )
        ) {
            Text("Regresar")
        }

    }
}

@Composable
fun PoliticaInformacionScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(getCheckboxState(context)) } // Recupera el estado al iniciar

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Política de Información",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Al aceptar, estás de acuerdo con nuestras políticas de privacidad y el manejo de tus datos personales. "
                    + "Asegúrate de leerlas detenidamente antes de continuar.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Checkmark
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    saveCheckboxState(context, it) // Guarda el estado al cambiar
                }
            )
            Text("He leído y acepto la política de información")
        }

        // Botón de confirmar
        Button(
            onClick = {
                if (isChecked) {
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Debes aceptar la política de información", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726),
                contentColor = Color.White
            )
        ) {
            Text("Aceptar")
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726),
                contentColor = Color.White
            )
        ) {
            Text("Regresar")
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Subsidios(navController: NavHostController, userViewModel: UserViewModel, apiService: ApiService) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Estado para los productos obtenidos y el estado de carga
    val products = remember { mutableStateOf(emptyList<Product>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Llamada para obtener los productos disponibles al iniciar la pantalla
    LaunchedEffect(Unit) {
        try {
            val availableProducts = apiService.getAvailableProducts()
            Log.d("Subsidios", "Available Products: $availableProducts")
            if (availableProducts != null) {
                products.value = availableProducts
            } else {
                Log.e("Subsidios", "No se encontraron productos disponibles")
            }
        } catch (e: Exception) {
            Log.e("Subsidios", "Error al obtener productos: ${e.message}")
        } finally {
            isLoading = false // Finalizar el estado de carga
        }
    }

    // Filtrar la lista de productos basándose en la consulta de búsqueda
    val filteredProducts = products.value.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { MyAppNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Productos Disponibles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Filtro de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar producto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                // Mostrar el indicador de carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (filteredProducts.isEmpty()) {
                    // Mostrar mensaje si no hay resultados
                    Text(
                        text = "No se encontraron productos.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    // Usar Column con scroll para listas grandes
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        filteredProducts.forEach { product ->
                            ProductCard(
                                product = product,
                                onClick = {
                                    selectedProduct = product
                                    showDetailsDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // Mostrar detalles del producto en un diálogo
    if (showDetailsDialog) {
        selectedProduct?.let { product ->
            AlertDialog(
                onDismissRequest = { showDetailsDialog = false },
                title = { Text(product.name) },
                text = {
                    Column {
                        Text(product.description)
                        val bitmap = decodeBase64ToBitmap(product.image)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Log.e("Subsidios", "Error al decodificar la imagen para el producto en el diálogo: ${product.name}")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDetailsDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}


@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF9752)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.description,
                color = Color.White
            )

            // Decodificar y mostrar la imagen
            val bitmap = decodeBase64ToBitmap(product.image)
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Log.e("Subsidios", "Error al decodificar la imagen para el producto: ${product.name}")
            }
        }
    }
}

// Función para decodificar la imagen de base64 a Bitmap
private fun decodeBase64ToBitmap(base64: String): Bitmap? {
    return try {
        val decodedString = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        Log.e("Subsidios", "Error al decodificar la imagen: ${e.message}")
        null
    }
}
