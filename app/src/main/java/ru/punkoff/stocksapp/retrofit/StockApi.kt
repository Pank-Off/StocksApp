package ru.punkoff.stocksapp.retrofit

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.punkoff.stocksapp.model.*
import ru.punkoff.stocksapp.utils.Constant

interface StockApi {

    @GET("stock/symbol?")
    suspend fun searchPagination(
        @Query("exchange") exchange: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("token") token: String = FINHUB_API
    ): List<StockSymbol>

    @GET("stock/symbol?")
    fun getStocks(
        @Query("exchange") exchange: String,
        @Query("token") token: String = FINHUB_API
    ): Deferred<List<StockSymbol>>

    @GET("stock/profile2?")
    fun getLogo(
        @Query("symbol") symbol: String,
        @Query("token") token: String = FINHUB_API
    ): Deferred<StockLogo>

    @GET("quote?")
    fun getPrice(
        @Query("symbol") symbol: String,
        @Query("token") token: String = FINHUB_API
    ): Deferred<StockPrice>

    @GET("search?")
    fun getStockByQuery(
        @Query("q") symbol: String,
        @Query("token") token: String = FINHUB_API
    ): Deferred<StockLookup>

    @GET("stock/candle?")
    fun getCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String = Constant.RESOLUTION,
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("token") token: String = FINHUB_API
    ): Deferred<Candle>

    @GET("company-news?")
    fun getNews(
        @Query("symbol") symbol: String,
        @Query("from") from: String = Constant.NEWS_FROM_DEFAULT,
        @Query("to") to: String = Constant.NEWS_TO_DEFAULT,
        @Query("token") token: String = FINHUB_API
    ): Deferred<List<News>>

    @GET("https://www.alphavantage.co/query?")
    fun getCompanyProfile(
        @Query("symbol") symbol: String,
        @Query("function") function: String = Constant.ALPHAVANTAGE_PROFILE_FUNCTION_PARAMETER,
        @Query("apikey") apikey: String = ALPHAVANTAGE_API,
    ): Deferred<Summary>

    companion object {
        private const val FINHUB_API = "c1fjmln48v6r34ehd6hg"
        private const val ALPHAVANTAGE_API = "Y8ZBME7X49660B2Y"
    }
}