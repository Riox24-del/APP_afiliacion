package com.example.plesapp

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
        composable("mainApp") { MyApp(userViewModel) }

    }
}



@Composable
fun Greeting(navController: NavHostController, userViewModel: UserViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {

        LoginScreen(navController, userViewModel)
    }
    }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(userViewModel: UserViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val context = LocalContext.current
    val apiService = remember { ApiService(context) }
    val responseText by remember { mutableStateOf("Response will be shown here") }
    val isLoading by remember { mutableStateOf(false) }
    var apiPartnersResponse by remember { mutableStateOf<ApiResponsePartners?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    PlesappTheme {
    LaunchedEffect(Unit) {
        scope.launch {
            apiService.authenticate()
            apiPartnersResponse = apiService.getApiPartners()
        }
    }

    BackHandler(enabled = true) {
        showExitDialog = true
    }

    if (showExitDialog) {
        Dialog(
            onDismissRequest = { showExitDialog = false },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "¿Estás seguro de cerrar tu sesión?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                userViewModel.logout()
                                navController.popBackStack()
                                navController.navigate("greeting")
                                showExitDialog = false

                                // Reiniciar la Activity para refrescar la aplicación
                                val activity = (context as? Activity)
                                activity?.finish()
                                activity?.startActivity(activity.intent)
                                Toast.makeText(context, "Adiós", Toast.LENGTH_LONG).show()
                                //activity?.overridePendingTransition(0, 0)
                            },
                        ) {
                            Text("Sí, adiós", color = Color.White)
                        }
                        Button(
                            onClick = { showExitDialog = false },
                        ) {
                            Text("Cancelar", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController, drawerState, userViewModel)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Serfineg") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationHost(
                    navController,
                    apiService,
                    responseText,
                    isLoading,
                    scope,
                    apiPartnersResponse,
                    userViewModel
                )
            }
        }
    }
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
    fun DrawerContent(
        navController: NavHostController,
        drawerState: DrawerState,
        userViewModel: UserViewModel
    ) {
        val scope = rememberCoroutineScope()
        val userState by userViewModel.userState.collectAsState()
        val context = LocalContext.current

        if (userState != null) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Text("SERFINEG", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                val drawerItems = listOf("Inicio", "Perfil", "Inversiones") // "Crowdfunding", "Noticias"
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate(item.lowercase(Locale.getDefault()))
                            }
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Button(
                    onClick = {
                        scope.launch {
                            userViewModel.logout()
                            drawerState.close()

                            // Navegar a "greeting"
                            navController.navigate("greeting") {
                                popUpTo("greeting") {
                                    inclusive = true
                                }
                            }

                            // Reiniciar la actividad
                            val activity = (context as? Activity)
                            activity?.let {
                                it.finish()
                                it.startActivity(it.intent)
                                // it.overridePendingTransition(0, 1)
                                Toast.makeText(context, "Adiós", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }



@Composable
fun WelcomeAndList(navController: NavHostController,
                   apiService: ApiService, responseText: String,
                   loading: Boolean, scope: CoroutineScope,
                   apiPartnersResponse: ApiResponsePartners?,
                   userViewModel: UserViewModel) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController,
                userViewModel: UserViewModel) {

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
                                            userViewModel.setUser(User(
                                                foundRecord.id.toString(),
                                                foundRecord.name,
                                                foundRecord.email,
                                                foundRecord.phone,
                                            ))


                                            navController.navigate("mainApp")
                                        } else {
                                            errorLogin = "Contraseña incorrecta para el usuario $username"
                                        }
                                    } else {
                                        errorLogin = "No se encontró ningún usuario con el nombre de usuario $username"
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