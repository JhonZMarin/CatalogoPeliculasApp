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

@Composable
fun HomeScreen() {
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val dao = db.movieDao()


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

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Películas Populares", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(movies) { movie ->
                MovieItem(movie) { favoriteMovie ->
                    scope.launch {
                        dao.insert(favoriteMovie)
                    }
                }
                Divider()
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onSave: (FavoriteMovie) -> Unit) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        Row {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(movie.title, style = MaterialTheme.typography.titleMedium)
                Text(movie.overview, maxLines = 3)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                onSave(FavoriteMovie(movie.id, movie.title, movie.overview, movie.poster_path, "favorito"))
            }) {
                Text("⭐ Favorito")
            }
            Button(onClick = {
                onSave(FavoriteMovie(movie.id, movie.title, movie.overview, movie.poster_path, "visto"))
            }) {
                Text("✅ Visto")
            }
            Button(onClick = {
                onSave(FavoriteMovie(movie.id, movie.title, movie.overview, movie.poster_path, "por_ver"))
            }) {
                Text("📌 Por Ver")
            }
        }
    }
}
