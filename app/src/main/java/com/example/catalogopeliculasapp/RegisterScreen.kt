package com.example.catalogopeliculasapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    // Variables de estado para almacenar los datos y errores
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // UI principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de usuario
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nuevo usuario") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && username.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && password.isBlank()
        )

        // Mensaje de error si algún campo está vacío
        if (showError && (username.isBlank() || password.isBlank())) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Los campos no pueden estar vacíos",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de registro con validación
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    showError = true
                } else {
                    showError = false
                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar y volver")
        }
    }
}
