package com.example.catalogopeliculasapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.catalogopeliculasapp.room.AppDatabase
import com.example.catalogopeliculasapp.room.FavoriteMovie
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import java.net.URLEncoder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(navController: NavHostController) {
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Películas", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Categorías
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                CategoryButton("⭐ Favoritos", currentCategory == "favorito") { currentCategory = "favorito" }
                CategoryButton("✅ Vistas", currentCategory == "visto") { currentCategory = "visto" }
                CategoryButton("📌 Por Ver", currentCategory == "por_ver") { currentCategory = "por_ver" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (movies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay películas en esta categoría", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movies) { movie ->
                        FavoriteMovieItem(movie, onDelete = {
                            scope.launch {
                                dao.delete(movie)
                                movies = dao.getByCategory(currentCategory)
                            }
                        }, onDetails = {
                            val encodedTitle = URLEncoder.encode(movie.title, "UTF-8")
                            val encodedOverview = URLEncoder.encode(movie.overview, "UTF-8")
                            val encodedPoster = URLEncoder.encode(movie.posterPath, "UTF-8")
                            navController.navigate("details/$encodedTitle/$encodedOverview/$encodedPoster")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = if (selected) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.padding(horizontal = 4.dp)

    ) {
        Text(
            text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun FavoriteMovieItem(movie: FavoriteMovie, onDelete: () -> Unit, onDetails: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp), // Corrección aquí
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.posterPath}"),
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(movie.overview, maxLines = 3, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🗑 Eliminar")
                    }
                    OutlinedButton(
                        onClick = onDetails,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📄 Ver Detalles")
                    }
                }
            }
        }
    }
}
