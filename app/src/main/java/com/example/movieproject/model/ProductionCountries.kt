package com.example.movieproject.model

import com.google.gson.annotations.SerializedName

data class ProductionCountries(
    @SerializedName("iso_3166_1") val productCountry: String?
)
