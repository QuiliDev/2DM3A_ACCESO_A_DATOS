package com.example.ad0.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ad_auth.components.DefaultColumn
import com.example.ad_auth.components.EmailTextField
import com.example.ad_auth.components.PasswordTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


@Composable
fun VentanaLogin(navController: NavController, modifier: Modifier) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    // El contexto es una interfaz para acceder a información global sobre el entorno de una aplicación
    // Acceder al entorno global de la app y hacer cosas que requieren información del sistema
    val context = LocalContext.current

    DefaultColumn {
        Text("Inicia Sesion",fontWeight = FontWeight.Bold)
        EmailTextField(email,{ email = it }, modifier)
        PasswordTextField(pass,{ pass = it }, modifier)
        Spacer(modifier = Modifier.height(16.dp))

        // Botón de login: valida campos y llama a sendLogin
        Button({
            // Si algún campo está vacío, mostrar mensaje de error
            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_LONG).show()
            }
            else {
                // Intentar iniciar sesión con Firebase
                sendLogin(email, pass) { success, message ->
                    // Mostrar resultado del login en un Toast
                    Toast.makeText(context, message ?: "Error desconocido", Toast.LENGTH_LONG).show()
                }
            }
        }) { Text("Iniciar sesión") }

        // Botón para navegar a la pantalla de crear cuenta
        TextButton(onClick = {
            // Usamos NavController para cambiar de pantalla a "crearCuenta"
            navController.navigate("crearCuenta") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true // evita duplicar la pantalla si ya está en el tope del stack
            }
        }) {
            Text("Crear cuenta")
        }

    }
}


fun sendLogin(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val emailTrimmed = email.trim()

    auth.signInWithEmailAndPassword(emailTrimmed, pass).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = auth.currentUser
            onResult(true, "Bienvenido ${user?.email}")
        } else {
            val exception = task.exception
            val message = if (exception is FirebaseAuthException) {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Formato de correo inválido"
                    "ERROR_USER_NOT_FOUND" -> "Usuario no registrado"
                    "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta"
                    "ERROR_INVALID_CREDENTIAL" -> "Credencial no válida"
                    else -> "Error desconocido: ${exception.errorCode}, ${exception.message}"
                }
            } else {
                "Error desconocido: ${exception?.message}"
            }
            onResult(false, message)
        }
    }
}

