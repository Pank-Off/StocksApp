package ru.punkoff.stocksapp.repository

import kotlinx.coroutines.flow.Flow
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.detail.fragments.cats.CatsViewState
import ru.punkoff.stocksapp.ui.main.fragments.stocks.PaginationViewStateResult
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState

interface Repository {

    fun setCache(stocks: List<Stock>)

    suspend fun updatePrice(): StocksViewState

    suspend fun getProfile(ticker: String): StocksViewState

    suspend fun getNews(ticker: String, from: String, to: String): StocksViewState

    suspend fun getCandles(
        ticker: String,
        from: Long,
        to: Long,
        resolution: String
    ): StocksViewState

    suspend fun requestMore(query: String)

    suspend fun getSearchResultStream(query: String): Flow<PaginationViewStateResult>

    suspend fun getRequest(symbol: String? = null): StocksViewState

    suspend fun saveStock(stock: Stock)

    suspend fun deleteStock(ticker: String)

    suspend fun getCache(): CacheStock

    suspend fun saveCache(stock: CacheStock)

    suspend fun startSocket(symbol: String)

    fun closeSocket()

    suspend fun getCat(): CatsViewState
}