package com.example.movieproject.network

import com.example.movieproject.model.MovieDetails
import com.example.movieproject.model.MovieResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("movie/popular/")
    suspend fun gatherMovies(
        @Query("api_key") aKey: String,
        @Query("page") pageNumber: Int
    ): MovieResults

    @GET("search/movie/")
    suspend fun gatherSearchedMovies(
        @Query("api_key") aKey: String,
        @Query("query") searchKey: String,
        @Query("page") pageNumber: Int
    ): MovieResults

    @GET("movie/{movie_id}")
    fun gatherDetails(
        @Path("movie_id") idMovie: String,
        @Query("api_key") aKey: String
    ): Call<MovieDetails>
}
