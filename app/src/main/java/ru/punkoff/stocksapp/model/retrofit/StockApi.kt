package ru.punkoff.stocksapp.model.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    @GET("stock/symbol?")
    fun getRequest(
        @Query("exchange") exchange: String,
        @Query("token") token: String = KEY
    ): Call<List<StockExample>>

    companion object {
        private const val KEY = "c10fsj748v6oijd8e5gg"
    }
}