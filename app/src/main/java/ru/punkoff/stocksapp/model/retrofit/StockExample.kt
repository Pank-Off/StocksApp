package ru.punkoff.stocksapp.model.retrofit

import com.google.gson.annotations.SerializedName

data class StockExample(
    @SerializedName("currency") val currency: String,
    @SerializedName("description") val description: String,
    @SerializedName("displaySymbol") val displaySymbol: String,
    @SerializedName("figi") val figi: String,
    @SerializedName("mic") val mic: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("type") val type: String
)