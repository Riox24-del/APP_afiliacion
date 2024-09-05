package com.example.plesapp


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Checkbox
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
import kotlinx.coroutines.launch


@Composable
fun MainNavigation(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "greeting") {
        composable("greeting") { Greeting(navController, userViewModel) }
        composable("login") { LoginScreen(navController, userViewModel) }
        composable("mainApp") { MyApp(navController, userViewModel) }
        composable("inicio") { Inicio(navController, userViewModel) }
        composable("afiliate") { Afiliate(navController, userViewModel) }
        composable("subsidios") { Subsidios(navController, userViewModel) }
        composable("catalogoDelMes") { CatalogoDelMes(navController, userViewModel) }
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
fun Greeting(navController: NavHostController, userViewModel: UserViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Inicio(navController, userViewModel)
        //LoginScreen(navController, userViewModel)
    }
}




@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiService = remember { ApiService(context) }
    var errorUsername by remember { mutableStateOf("") }
    var errorPassword by remember { mutableStateOf("") }
    var errorLogin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Habilitando el scroll
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
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorUsername.isNotEmpty(),
                colors = TextFieldDefaults.colors(
                    contentColorFor(backgroundColor = Color.White),
                    focusedIndicatorColor = Color(0xFFFFA726),
                    focusedLabelColor = Color(0xFFFFA726),
                )
            )
            if (errorUsername.isNotEmpty()) {
                Text(text = errorUsername, color = Color.Red, fontSize = 12.sp)
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
                            errorUsername = ""
                            errorPassword = ""
                            errorLogin = ""

                            // Validación de campos
                            if (username.isBlank()) {
                                errorUsername = "El nombre de usuario no puede estar en blanco"
                            }
                            if (password.isBlank()) {
                                errorPassword = "La contraseña no puede estar en blanco"
                            }

                            if (errorUsername.isEmpty() && errorPassword.isEmpty()) {
                                isLoading = true
                                try {
                                    // Llama a authenticate sin parámetros
                                    val authMessage = apiService.authenticate()
                                    if (authMessage == "Authentication successful") {
                                        val authUser = apiService.getApiPartners()
                                        val filteredRecords = authUser?.partners
                                        val foundRecord = filteredRecords?.find { it.nameUserApp == username }
                                        if (foundRecord != null) {
                                            if (foundRecord.passwordApp == password) {
                                                userViewModel.setUser(User(
                                                    foundRecord.id.toString(),
                                                    foundRecord.name,
                                                    foundRecord.email,
                                                    foundRecord.phone,
                                                    nameUserApp = foundRecord.nameUserApp!!

                                                ))

                                                // Navegar a la pantalla principal
                                                navController.navigate("inicio")
                                                Toast.makeText(context, "Bienvenid@", Toast.LENGTH_SHORT).show()
                                                //navController.navigate("mainApp")
                                            } else {
                                                errorLogin = "Contraseña incorrecta para el usuario $username"
                                            }
                                        } else {
                                            errorLogin = "No se encontró ningún usuario con el nombre de usuario $username"
                                        }
                                    } else {
                                        errorLogin = "Usuario o contraseña incorrectos"
                                    }
                                } catch (e: Exception) {
                                    errorLogin = "Error al autenticar: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
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
    var showGrupoForm by remember { mutableStateOf(false) }

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

        //  Button(
          //  onClick = { showGrupoForm = !showGrupoForm },
            //    modifier = Modifier.fillMaxWidth(),
              //  colors = ButtonDefaults.buttonColors(
                //    containerColor = Color(0xFFFFA726),
                 //   contentColor = Color.White
                //)
           // ) {
             //   Text("Crea tu grupo PLES")
          //  }

            Spacer(modifier = Modifier.height(16.dp))

            if (showAfiliateForm) {
                AfiliateForm()
            }

            // if (showGrupoForm) {
              //  GrupoForm()
            //}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfiliateForm() {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var selectedSex by remember { mutableStateOf("") }
    var isSexDropdownExpanded by remember { mutableStateOf(false) }
    var fechaNacimiento by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var selectedEstadoCivil by remember { mutableStateOf("") }
    var isEstadoCivilDropdownExpanded by remember { mutableStateOf(false) }
    var direccion by remember { mutableStateOf("") }
    var perteneceGrupoSocial by remember { mutableStateOf(false) }
    var ocupacion by remember { mutableStateOf("") }
    val sexOptions = listOf("Hombre", "Mujer", "Prefiero no especificar")
    val estadoCivilOptions = listOf("Soltero", "Casado", "Divorciado", "Viudo", "Unión libre")

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = isSexDropdownExpanded,
            onExpandedChange = { isSexDropdownExpanded = !isSexDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedSex,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sexo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSexDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = isSexDropdownExpanded,
                onDismissRequest = { isSexDropdownExpanded = false }
            ) {
                sexOptions.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            selectedSex = label
                            isSexDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de Nacimiento") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = curp,
            onValueChange = { curp = it },
            label = { Text("CURP") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = isEstadoCivilDropdownExpanded,
            onExpandedChange = { isEstadoCivilDropdownExpanded = !isEstadoCivilDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedEstadoCivil,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado Civil") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isEstadoCivilDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = isEstadoCivilDropdownExpanded,
                onDismissRequest = { isEstadoCivilDropdownExpanded = false }
            ) {
                estadoCivilOptions.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            selectedEstadoCivil = label
                            isEstadoCivilDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = perteneceGrupoSocial,
                onCheckedChange = { perteneceGrupoSocial = it }
            )
            Text("Pertenece a un grupo social")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = ocupacion,
            onValueChange = { ocupacion = it },
            label = { Text("Ocupación") },
            modifier = Modifier.fillMaxWidth()
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
        Spacer(modifier = Modifier.width(160.dp))
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
        Spacer(modifier = Modifier.height(80.dp))
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Subsidios(navController: NavHostController, userViewModel: UserViewModel) {
    // Subsidio de ejemplo
    val subsidio = Subsidio("Subsidio Ejemplo", "Descripción del subsidio de ejemplo", 150.0)

    var showDetailsDialog by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Funcionando",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { showDetailsDialog = true },
                //elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = subsidio.nombre,
                     //   style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Precio: \$${subsidio.precio}",
                       // style = MaterialTheme.typography.body1
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
    val precio: Double
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