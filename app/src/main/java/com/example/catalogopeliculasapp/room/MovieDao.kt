package com.example.catalogopeliculasapp.room

import androidx.room.*
@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE category = :category")
    suspend fun getByCategory(category: String): List<FavoriteMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: FavoriteMovie)

    @Delete
    suspend fun delete(movie: FavoriteMovie)

    @Query("DELETE FROM movies WHERE id = :movieId AND category = :category")
    suspend fun deleteByIdAndCategory(movieId: Int, category: String)
}