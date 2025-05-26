package com.example.catalogopeliculasapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.catalogopeliculasapp.room.AppDatabase
import com.example.catalogopeliculasapp.room.FavoriteMovie
import kotlinx.coroutines.launch

@Composable
fun ListsScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val dao = db.movieDao()
    val scope = rememberCoroutineScope()

    var currentCategory by remember { mutableStateOf("favorito") }
    var movies by remember { mutableStateOf(listOf<FavoriteMovie>()) }

    LaunchedEffect(currentCategory) {
        scope.launch {
            movies = dao.getByCategory(currentCategory)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mis Películas", style = MaterialTheme.typography.headlineSmall)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { currentCategory = "favorito" }) { Text("⭐ Favoritos") }
            Button(onClick = { currentCategory = "visto" }) { Text("✅ Vistas") }
            Button(onClick = { currentCategory = "por_ver" }) { Text("📌 Por Ver") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(movies) { movie ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(movie.title, style = MaterialTheme.typography.titleMedium)
                        Text(movie.overview, maxLines = 3)
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    dao.delete(movie)
                                    movies = dao.getByCategory(currentCategory)
                                }
                            }) {
                                Text("🗑 Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
