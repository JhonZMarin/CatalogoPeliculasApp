package com.example.catalogopeliculasapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movies")
data class FavoriteMovie(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val category: String // "favorito", "visto", "por_ver"
)

