package ru.punkoff.stocksapp.model

import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("category") val category: String,
    @SerializedName("datetime") val datetime: Long,
    @SerializedName("headline") val headline: String,
    @SerializedName("id") val id: Long,
    @SerializedName("image") val image: String,
    @SerializedName("related") val related: String,
    @SerializedName("source") val source: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("url") val url: String,
)
