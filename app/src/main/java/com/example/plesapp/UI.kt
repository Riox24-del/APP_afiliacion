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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.plesapp.ui.theme.PlesappTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun MainNavigation(userViewModel: UserViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "greeting") {
        composable("greeting") { Greeting(navController, userViewModel) }
      //  composable("login") { LoginScreen(navController, userViewModel) }
        composable("mainApp") { MyApp(userViewModel) }

    }
}

@Composable
fun Greeting(navController: NavHostController, userViewModel: UserViewModel) {


    Column(modifier = Modifier.padding(16.dp)) {

        Text(text = "Hola, si sirvo", style = MaterialTheme.typography.headlineMedium)


        Button(
            onClick = {
               // navController.navigate("tu_ruta_aqui")
            },
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange40
            )
        ) {
            Text(text = "cxkwaeiked", color = Color.White)
        }
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
