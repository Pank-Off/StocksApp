package ru.punkoff.stocksapp.repository

import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.stocks.StocksViewState

interface Repository {

    fun setCache(stocks: List<Stock>)

    suspend fun getRequest(symbol: String? = null): StocksViewState

    suspend fun saveStock(stock: Stock)

    suspend fun deleteStock(ticker: String)

    suspend fun getCache(): CacheStock

    suspend fun saveCache(stock: CacheStock)
}