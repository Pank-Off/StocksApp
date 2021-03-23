package ru.punkoff.stocksapp.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import ru.punkoff.stocksapp.model.*
import ru.punkoff.stocksapp.utils.Constant

interface StockApi {

    @GET("stock/symbol?")
    suspend fun searchPagination(
        @Query("exchange") exchange: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("token") token: String = KEY
    ): List<StockSymbol>

    @GET("stock/symbol?")
    fun getStocks(
        @Query("exchange") exchange: String,
        @Query("token") token: String = KEY
    ): Deferred<List<StockSymbol>>

    @GET("stock/profile2?")
    fun getLogo(
        @Query("symbol") symbol: String,
        @Query("token") token: String = KEY
    ): Deferred<StockLogo>

    @GET("quote?")
    fun getPrice(
        @Query("symbol") symbol: String,
        @Query("token") token: String = KEY
    ): Deferred<StockPrice>

    @GET("search?")
    fun getStockByQuery(
        @Query("q") symbol: String,
        @Query("token") token: String = KEY
    ): Deferred<StockLookup>

    @GET("stock/candle?")
    fun getCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String = Constant.RESOLUTION,
        @Query("from") from: Long = Constant.CANDLE_FROM_TIME,
        @Query("to") to: Long = Constant.CANDLE_TO_TIME,
        @Query("token") token: String = KEY
    ): Deferred<Candle>

    companion object {
        private const val KEY = "c10fsj748v6oijd8e5gg"
    }
}