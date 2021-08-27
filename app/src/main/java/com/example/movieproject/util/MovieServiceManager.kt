package com.example.movieproject.util

import com.example.movieproject.network.MovieService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieServiceManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val movieService: MovieService = retrofit.create(MovieService::class.java)
}
