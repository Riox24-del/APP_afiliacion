package com.example.plesapp


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch




@Composable
fun MainNavigation(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "greeting") {
        composable("greeting") { Greeting(navController) }
        composable("login") { LoginScreen(navController, userViewModel) }
        composable("mainApp") { MyApp(navController, userViewModel) }
        composable("inicio") { Inicio(navController, userViewModel) }
        composable("afiliate") { Afiliate(navController, userViewModel) }
        composable("subsidios") { Subsidios(navController, userViewModel) }
        composable("catalogoDelMes") { CatalogoDelMes(navController, userViewModel) }
        composable("camera") {
            CameraScreen(navController)
        }
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
            icon = { Icon(Icons.Filled.Home, "Inicio") },
            label = { Text("Inicio") },
            selected = navController.currentDestination?.route == "inicio",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, "Afiliate") },
            label = { Text("Afiliate") },
            selected = navController.currentDestination?.route == "afiliate",
            onClick = { navController.navigate("afiliate") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, "Subsidios") },
            label = { Text("Subsidios") },
            selected = navController.currentDestination?.route == "subsidios",
            onClick = { navController.navigate("subsidios") }
        )
      //  NavigationBarItem(
       //     icon = { Icon(Icons.Filled.Home, "Catálogo del Mes") },
        //    label = { Text("Catálogo del Mes") },
        //    selected = navController.currentDestination?.route == "catalogoDelMes",
         //   onClick = { navController.navigate("catalogoDelMes") }
        //)
    }
}

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inicio),
                    contentDescription = "Logo de Plataforma Latinoamericana",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Plataforma Latinoamericana Económica y Social",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        shadow = Shadow(color = Color.Black, blurRadius = 4f)
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lunes a Viernes de 09:00 a 19:00, Sábado de 09:00 a 14:00",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Privada de, C. Prolongación Eucaliptos 105, Ricardo Flores Magon, 68020 Oaxaca de Juárez, Oax.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Teléfono: 529511433017",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/plesmx"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFF2675AE)
                    ),
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
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFFFA726)
                    ),
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
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = {
                        navController.navigate("login")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFFFA726)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFFA726)
                    )
                ) {
                    Text("Iniciar sesión")
                }

            }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Afiliate(navController: NavHostController, userViewModel: UserViewModel) {
    var showAfiliateForm by remember { mutableStateOf(false) }
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

            Button(
                onClick = { showAfiliateForm = !showAfiliateForm },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text("Afíliate")
            }

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar el formulario de afiliación
            if (showAfiliateForm) {
                AfiliateForm()
            }

            // Mostrar la vista de la cámara
            Button(
                onClick = { navController.navigate("camera") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text("Abrir Cámara")
            }
        }
    }
}

@Composable
fun CameraScreen(navController: NavHostController) {
    var recognizedText by remember { mutableStateOf("El texto reconocido aparecerá aquí") }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    // Inicializamos el objeto de reconocimiento de texto
    val textRecognizer: TextRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    // Inicializamos el objeto cámara
    val cameraJhr: CameraJhr = remember {
        CameraJhr(context as ComponentActivity)
    }

    // Ciclo de vida de la cámara
    LaunchedEffect(Unit) {
        if (cameraJhr.allpermissionsGranted()) {
            startCameraJhr(cameraJhr, textRecognizer) { result, bitmap ->
                recognizedText = result
                bitmapImage = bitmap
            }
        } else {
            cameraJhr.noPermissions()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Escanea tu credencial", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la vista previa de la cámara como imagen si está disponible
        bitmapImage?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Vista previa de la cámara",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el texto reconocido
        Text(
            text = recognizedText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.LightGray)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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

private fun startCameraJhr(
    cameraJhr: CameraJhr,
    textRecognizer: TextRecognizer,
    onTextDetected: (String, Bitmap?) -> Unit
) {
    var timeCurrent = System.currentTimeMillis()
    val timeWait = 1000L

    cameraJhr.addlistenerBitmap(object : BitmapResponse {
        override fun bitmapReturn(bitmap: Bitmap?) {
            if (System.currentTimeMillis() - timeCurrent > timeWait && bitmap != null) {
                val image = InputImage.fromBitmap(bitmap, 0)
                textRecognizer.process(image)
                    .addOnSuccessListener { result ->
                        onTextDetected(result.text, bitmap)
                    }
                    .addOnFailureListener {

                    }
                timeCurrent = System.currentTimeMillis()
            }
        }
    })

    cameraJhr.initBitmap()
    cameraJhr.start(1, 0, null, true, false, true)
}


@RequiresApi(Build.VERSION_CODES.FROYO)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfiliateForm() {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Validaciones básicas
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
                    companyId = 1
                )

                if (response) {
                    "Usuario creado exitosamente"
                } else {
                  //  "Error: el servidor no pudo procesar la solicitud."
                    "Usuario creado exitosamente?"
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
        Text("Formulario de Afiliación", style = MaterialTheme.typography.titleMedium)

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

        // Botones de Cancelar y Enviar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    nombre = ""
                    correo = ""
                    telefono = ""
                    password = ""
                    message = ""
                    isError = false
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

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

        // Mostrar el mensaje de éxito o error
        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (isError) Color.Red else Color.Green,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Mostrar diálogo de detalles si hay un mensaje
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





@Composable
fun GrupoForm() {
    var nombreGrupo by remember { mutableStateOf("") }
    var representante by remember { mutableStateOf("") }
    var cantidadPersonas by remember { mutableStateOf("") }
    var procedencia by remember { mutableStateOf("") }
    var numeroContacto by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Formulario de Creación de Grupo", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nombreGrupo,
            onValueChange = { nombreGrupo = it },
            label = { Text("Nombre del Grupo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = representante,
            onValueChange = { representante = it },
            label = { Text("Representante") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = cantidadPersonas,
            onValueChange = { cantidadPersonas = it },
            label = { Text("Cantidad de Personas") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = procedencia,
            onValueChange = { procedencia = it },
            label = { Text("Procedencia") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = numeroContacto,
            onValueChange = { numeroContacto = it },
            label = { Text("Número de Contacto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de Cancelar y Enviar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Acción de cancelar */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { /* Acción de enviar */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Enviar")
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Subsidios(navController: NavHostController, userViewModel: UserViewModel) {
    // Subsidio de ejemplo
    val subsidio = Subsidio("Subsidio Ejemplo", "Descripción del subsidio de ejemplo", 150.0)

    var showDetailsDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { MyAppNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Subsidios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // Filtro de búsqueda (solo visual)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar subsidio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { showDetailsDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFA726)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = subsidio.nombre,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Precio: \$${subsidio.precio}"
                    )
                }
            }
        }
    }

    // Mostrar detalles del subsidio en un diálogo
    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text(subsidio.nombre) },
            text = {
                Column {
                    Text("Precio: \$${subsidio.precio}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Descripción: ${subsidio.descripcion}")
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

data class Subsidio(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
)



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CatalogoDelMes(navController: NavHostController, userViewModel: UserViewModel) {
    Scaffold(
        bottomBar = { MyAppNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Catálogo del Mes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Funcionando",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}