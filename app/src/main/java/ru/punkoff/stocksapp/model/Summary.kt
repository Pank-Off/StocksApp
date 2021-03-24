package ru.punkoff.stocksapp.model

import com.google.gson.annotations.SerializedName

data class Summary(
    @SerializedName("Symbol") val symbol: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Country") val country: String,
    @SerializedName("Industry") val industry: String,
)