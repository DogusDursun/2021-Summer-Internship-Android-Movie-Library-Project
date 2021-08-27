package com.example.movieproject.model

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @SerializedName("adult") val adult: String?,
    @SerializedName("budget") val budget: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("popularity") val popularity: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("revenue") val revenue: String?,
    @SerializedName("runtime") val runtime: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("vote_average") val voteAverage: String?,
    @SerializedName("vote_count") val voteCount: String?,
    @SerializedName("genres") val genres: List<Genres?>?,
    @SerializedName("production_countries") val productionCountries: List<ProductionCountries?>?
)
