package ru.punkoff.stocksapp.model

import com.google.gson.annotations.SerializedName

data class StockLogo(
    @SerializedName("logo") val logo: String,
    @SerializedName("weburl") val weburl: String
)