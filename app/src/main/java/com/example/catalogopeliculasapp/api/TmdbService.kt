package com.example.catalogopeliculasapp.api

import com.example.catalogopeliculasapp.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1
    ): MovieResponse
}