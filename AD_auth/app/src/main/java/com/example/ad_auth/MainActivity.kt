package com.example.ad_auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ad0.screens.VentanaCrearCuenta
import com.example.ad0.screens.VentanaLogin
import com.example.ad_auth.ui.theme.AD_authTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AD_authTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VentanaMain(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun VentanaMain(modifier: Modifier) {
    // estado sincronizado de la ruta de la ventana
    val navController = rememberNavController()

    // Contenedor principal de la navegación, aquí estan cargadas las direcciones a las pantallas
    NavHost(
        navController = navController,
        startDestination = "login", // Cual es la primera pantalla que se visualiza
        modifier = modifier
    ) {
        /**
         * composable("login") define una pantalla dentro del navHost
         * login es la ruta o nombre de pantalla
         * VentanaLogin es la funcion composable que contiene el código de la ventana
         * argumentos
         *      navController: para tener la capacidad de gestionar un cambio de ventana
         *      modifier: para gestionar el inne
         */
        composable("login") {VentanaLogin(navController,modifier = modifier)}
        composable("crearCuenta") {VentanaCrearCuenta(navController,modifier = modifier)}
    }
}


