package com.example.movieproject.model

import com.google.gson.annotations.SerializedName

data class MovieResults(
    @SerializedName("results") val results: ArrayList<Movie>,
    @SerializedName("total_pages") val totalPages: Int?
)
