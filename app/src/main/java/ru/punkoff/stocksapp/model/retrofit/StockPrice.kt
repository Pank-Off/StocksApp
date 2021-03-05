package ru.punkoff.stocksapp.model.retrofit

import com.google.gson.annotations.SerializedName

data class StockPrice(
    @SerializedName("c") val currentPrice: Double,
    @SerializedName("pc") val previousPrice: Double
)