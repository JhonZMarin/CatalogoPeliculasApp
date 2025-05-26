package com.example.catalogopeliculasapp

@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Pantalla de Registro")
        Button(onClick = { onNavigateBack() }) {
            Text("Volver")
        }
    }
}