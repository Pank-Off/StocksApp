package ru.punkoff.stocksapp.model

import com.google.gson.annotations.SerializedName

data class SocketData(
    @SerializedName("data") val data: List<Trade>,
    @SerializedName("type") val type: String
)
