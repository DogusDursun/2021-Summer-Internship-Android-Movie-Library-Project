package com.example.movieproject.model

import com.google.gson.annotations.SerializedName

data class Genres(
    @SerializedName("name") val genreName: String?
)
