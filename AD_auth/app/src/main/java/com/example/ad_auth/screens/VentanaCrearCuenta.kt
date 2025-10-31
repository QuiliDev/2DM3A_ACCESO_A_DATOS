package com.example.ad0.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ad_auth.components.DefaultColumn
import com.example.ad_auth.components.EmailTextField
import com.example.ad_auth.components.PasswordTextField
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VentanaCrearCuenta(navController: NavController,modifier: Modifier) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val context = LocalContext.current


    DefaultColumn {
        /**
         * headlineLarge → título grande
         * headlineMedium → título mediano
         * headlineSmall → título pequeño
         * titleLarge, titleMedium también son útiles para subtítulos
         */
        Text("Crea aqui tu cuenta",style = MaterialTheme.typography.headlineMedium)

        EmailTextField(email,{ email = it }, modifier)
        PasswordTextField(pass,{ pass = it }, modifier)
        Spacer(modifier = Modifier.height(16.dp))


        Button(onClick = {
            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_LONG).show()
            } else {
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Cuenta creada. Se ha enviado un correo de verificación a ${user.email}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Cuenta creada, pero no se pudo enviar el correo: ${verifyTask.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            // Opcional: navegar a Login después
                            navController.navigate("login") {
                                popUpTo("crearCuenta") { inclusive = true }
                                launchSingleTop = true
                            }

                        } else {
                            Toast.makeText(
                                context,
                                traducirErrorFirebase(task.exception?.message) ?: "Error desconocido",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }) {
            Text("Crear Cuenta")
        }

        // Botón para navegar a la pantalla de login.
        TextButton (onClick = {
            // Usamos NavController para cambiar de pantalla a "login"
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true

            }
        }) {Text("Tengo ya una cuenta")}
    }


}

fun traducirErrorFirebase(msg: String?): String = when {
    msg?.contains("password is invalid") == true ->
        "La contraseña debe tener al menos 6 caracteres"
    msg?.contains("badly formatted") == true ->
        "El formato del correo no es válido"
    msg?.contains("already in use") == true ->
        "El correo ya está registrado"
    else -> "Error desconocido"
}
