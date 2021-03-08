package ru.punkoff.stocksapp.model.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    @GET("stock/symbol?")
    fun getStocks(
        @Query("exchange") exchange: String,
        @Query("token") token: String = KEY
    ): Call<List<StockSymbol>>

    @GET("stock/profile2?")
    fun getLogo(
        @Query("symbol") symbol: String,
        @Query("token") token: String = KEY
    ): Call<StockLogo>

    @GET("quote?")
    fun getPrice(
        @Query("symbol") symbol: String,
        @Query("token") token: String = KEY
    ): Call<StockPrice>

    @GET("search?")
    fun getStockByQuery(
        @Query("q") symbol: String,
        @Query("token") token: String = KEY
    ): Call<StockLookup>

    companion object {
        private const val KEY = "c10fsj748v6oijd8e5gg"
    }
}