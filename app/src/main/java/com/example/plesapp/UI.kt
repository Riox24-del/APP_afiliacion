package com.example.plesapp

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plesapp.ui.theme.Orange40
import com.example.plesapp.ui.theme.Orange80
import com.example.plesapp.ui.theme.OrangeGrey40
import com.example.plesapp.ui.theme.Pink80
import com.example.plesapp.ui.theme.PlesappTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun MainNavigation(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "greeting") {
        composable("greeting") { Greeting(navController, userViewModel) }
        composable("login") { LoginScreen(navController, userViewModel) }
        composable("mainApp") { MyApp(userViewModel, PaddingValues()) }
    }
}

@Preview
@Composable
fun NavigationBarSample() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Main", "Afiliate", "Catalogo")

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

@Composable
fun Greeting(navController: NavHostController, userViewModel: UserViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
Inicio(navController, userViewModel)
        MyApp(userViewModel, PaddingValues())
        //LoginScreen(navController, userViewModel)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(userViewModel: UserViewModel, contentPadding: PaddingValues) {
    PlesappTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorite") },
                        label = { Text("Favorite") },
                        selected = false,
                        onClick = { /* Acción al seleccionar */ }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Afiliate") },
                        label = { Text("Afiliate") },
                        selected = false,
                        onClick = { /* Acción al seleccionar */ }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Catalogo") },
                        label = { Text("Catalogo") },
                        selected = false,
                        onClick = { /* Acción al seleccionar */ }
                    )
                }
            },
            content = { innerPadding ->
                Box(Modifier.padding(innerPadding)) {
                    // Contenido de la pantalla
                }
            },
            modifier = Modifier.padding(contentPadding)
        )
    }
}
@Composable
fun NavigationHost(
    navController: NavHostController,
    apiService: ApiService,
    responseText: String,
    isLoading: Boolean,
    scope: CoroutineScope,
    apiPartnersResponse: ApiResponsePartners?,
    userViewModel: UserViewModel
) {
    NavHost(navController, startDestination = "inicio") {
        composable("inicio") {
            WelcomeAndList(
                navController,
                apiService,
                responseText,
                isLoading,
                scope,
                apiPartnersResponse,
                userViewModel
            )
        }
        composable("greeting") { Greeting(navController, userViewModel) }


    }



    @Composable
    fun NavigationHost(
        navController: NavHostController,
        apiService: ApiService,
        responseText: String,
        isLoading: Boolean,
        scope: CoroutineScope,

        apiPartnersResponse: ApiResponsePartners?,
        userViewModel: UserViewModel
    ) {
        NavHost(navController, startDestination = "inicio") {
            composable("inicio") {
                WelcomeAndList(
                    navController,
                    apiService,
                    responseText,
                    isLoading,
                    scope,
                    apiPartnersResponse,
                    userViewModel
                )
            }
            composable("greeting") { Greeting(navController, userViewModel) }


        }
    }
}


@Composable
fun WelcomeAndList(
    navController: NavHostController,
    apiService: ApiService, responseText: String,
    loading: Boolean, scope: CoroutineScope,
    apiPartnersResponse: ApiResponsePartners?,
    userViewModel: UserViewModel
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val apiService = remember { ApiService(context) }
    var errorUsername by remember { mutableStateOf("") }
    var errorPassword by remember { mutableStateOf("") }
    var errorLogin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),  // Agregando scroll
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Inicio de sesión", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                //  textColor = Color.Black,
                cursorColor = Orange40,
                focusedIndicatorColor = Orange40,
                unfocusedIndicatorColor = OrangeGrey40,
                containerColor = Pink80
            )
        )

        if (errorUsername.isNotEmpty()) {
            Text(text = errorUsername, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                // textColor = Color.Black,
                cursorColor = Orange40,
                focusedIndicatorColor = Orange40,
                unfocusedIndicatorColor = OrangeGrey40,
                containerColor = Pink80 // Sets the background color of the TextField
            )
        )

        if (errorPassword.isNotEmpty()) {
            Text(text = errorPassword, color = Color.Red)
        }
        if (errorLogin.isNotEmpty()) {
            Text(text = errorLogin, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isLoading) {
                    scope.launch {
                        try {
                            errorUsername = ""
                            errorPassword = ""
                            errorLogin = ""
                            if (username.isBlank() && password.isBlank()) {
                                errorUsername = "El nombre de usuario no puede estar en blanco"
                                errorPassword = "La contraseña no puede estar en blanco"
                            } else if (password.isBlank()) {
                                errorPassword = "La contraseña no puede estar en blanco"
                            } else if (username.isBlank()) {
                                errorUsername = "El nombre de usuario no puede estar en blanco"
                            }
                            if (username.isEmpty() && password.isEmpty()) {
                                errorUsername = "Ingrese un nombre de usuario"
                                errorPassword = "Ingrese una contraseña"
                            } else if (username.isEmpty()) {
                                errorUsername = "Ingrese un usuario"
                            } else if (password.isEmpty()) {
                                errorPassword = "Ingrese una contraseña"
                            } else {
                                isLoading = true
                                val authMessage = apiService.authenticate()
                                if (authMessage == "Authentication successful") {
                                    val authUser = apiService.getApiPartners()
                                    val filteredRecords = authUser?.records
                                    val foundRecord = filteredRecords?.find { it.name == username }
                                    if (foundRecord != null) {
                                        if (foundRecord.passwordApp == password) {
                                            userViewModel.setUser(
                                                User(
                                                    foundRecord.id.toString(),
                                                    foundRecord.name,
                                                    foundRecord.email,
                                                    foundRecord.phone,
                                                )
                                            )


                                            navController.navigate("mainApp")
                                        } else {
                                            errorLogin =
                                                "Contraseña incorrecta para el usuario $username"
                                        }
                                    } else {
                                        errorLogin =
                                            "No se encontró ningún usuario con el nombre de usuario $username"
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            errorLogin = "Error al autenticar: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange40
            )


        ) {
            Text(
                text = if (isLoading) "Cargando..." else "Acceder",
                color = if (isLoading) Color.Gray else Color.White
            )
        }
    }
}


@Composable
fun Inicio( navController: NavHostController,
            userViewModel: UserViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "PLATAFORMA LATINOAMERICA ECONOMICA Y SOCIAL     LUNES A VIERNES DE 09:00 A 19:00, SABADO DE 09:00 A 14:00",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Privada de, C. Prolongación Eucaliptos 105, Ricardo Flores Magon, 68020 Oaxaca de Juárez, Oax.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "529511433017",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = { /* Abrir Facebook */ },
                modifier = Modifier.size(24.dp)
            ) {
               // Icon(
                 //   imageVector = Icons.Filled.Facebook,
                   // contentDescription = "Facebook"
                //)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { /* Abrir enlace */ },
                modifier = Modifier.size(24.dp)
            ) {
                Text(
                    text = "Enlace",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}