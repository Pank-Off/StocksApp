package ru.punkoff.stocksapp.repository

import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.stocks.StocksViewState

interface RepositoryRemote {

    fun setCache(stocks: List<Stock>)

    suspend fun getData(): StocksViewState

    suspend fun getDataBySymbol(symbol: String): StocksViewState
}