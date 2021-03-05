package ru.punkoff.stocksapp.model.retrofit

import com.google.gson.annotations.SerializedName

data class StockLogo(
    @SerializedName("logo") val logo: String,
)