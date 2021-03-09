package ru.punkoff.stocksapp.model.retrofit

import com.google.gson.annotations.SerializedName

data class StockSymbol(
    @SerializedName("description") val name: String,
    @SerializedName("symbol") val ticker: String,
    @SerializedName("displaySymbol") val displaySymbol: String
)