package ru.punkoff.stocksapp.model

import com.google.gson.annotations.SerializedName

data class Trade(
    @SerializedName("p") val price: Double,
    @SerializedName("s") val symbol: String,
    @SerializedName("t") val unixTime: Long,
    @SerializedName("v") val volume: Double,
)

