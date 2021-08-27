package com.example.movieproject.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("vote_average") val vote_average: String?,
    @SerializedName("poster_path") val poster_path: String?
)
