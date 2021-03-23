package ru.punkoff.stocksapp.model

import com.google.gson.annotations.SerializedName

data class Candle(
    @SerializedName("o") val openPrice: List<Double>,
    @SerializedName("t") val time: List<Long>,
    @SerializedName("s") val exist: String
)