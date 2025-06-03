package com.example.catalogopeliculasapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.catalogopeliculasapp.api.RetrofitClient
import com.example.catalogopeliculasapp.model.Movie
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.catalogopeliculasapp.room.AppDatabase
import com.example.catalogopeliculasapp.room.FavoriteMovie
import kotlinx.coroutines.CoroutineScope
import androidx.navigation.NavHostController
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ExitToApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val dao = db.movieDao()
    val snackbarHostState = remember { SnackbarHostState() }

    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.tmdbService.getPopularMovies("0fabe46bf3b2e4976613d400b029469d")
                movies = response.results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🎬 Catálogo de Películas") },
                actions = {
                    IconButton(onClick = { navController.navigate("lists") }) {
                        Icon(Icons.Filled.List, contentDescription = "Mis Películas")
                    }
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // 👈 Aquí se define el SnackbarHost
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Películas Populares", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(movies) { movie ->
                    MovieItem(
                        movie = movie,
                        navController = navController,
                        onSave = { favoriteMovie, category ->
                            scope.launch {
                                dao.insert(favoriteMovie)
                                snackbarHostState.showSnackbar("Añadido a $category")
                            }
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun MovieItem(
    movie: Movie,
    navController: NavHostController,
    onSave: (FavoriteMovie, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(movie.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(movie.overview, maxLines = 3, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButtonWithLabel("⭐", "Favorito") {
                    onSave(FavoriteMovie(movie.id, movie.title, movie.overview, movie.poster_path, "favorito"), "Favoritos")
                }
                IconButtonWithLabel("✅", "Visto") {
                    onSave(FavoriteMovie(movie.id, movie.title, movie.overview, movie.poster_path, "visto"), "Vistas")
                }
                IconButtonWithLabel("📌", "Por Ver") {
                    onSave(FavoriteMovie(movie.id, movie.title, movie.overview, movie.poster_path, "por_ver"), "Por Ver")
                }
                IconButtonWithLabel("📄", "Detalles") {
                    val encodedTitle = java.net.URLEncoder.encode(movie.title, "UTF-8")
                    val encodedOverview = java.net.URLEncoder.encode(movie.overview, "UTF-8")
                    val encodedPoster = java.net.URLEncoder.encode(movie.poster_path, "UTF-8")
                    navController.navigate("details/$encodedTitle/$encodedOverview/$encodedPoster")
                }
            }
        }
    }
}


@Composable
fun IconButtonWithLabel(emoji: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Button(onClick = onClick, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
            Text("$emoji $label", style = MaterialTheme.typography.labelLarge)
        }
    }
}

