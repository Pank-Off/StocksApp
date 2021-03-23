package ru.punkoff.stocksapp.model

import com.google.gson.annotations.SerializedName

data class StockLookup(
    @SerializedName("result") val result: List<StockSymbol>
)