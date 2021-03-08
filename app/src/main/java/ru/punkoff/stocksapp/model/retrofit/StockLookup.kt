package ru.punkoff.stocksapp.model.retrofit

import com.google.gson.annotations.SerializedName

data class StockLookup(
    @SerializedName("result") val result: List<StockSymbol>
)