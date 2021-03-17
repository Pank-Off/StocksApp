package ru.punkoff.stocksapp.repository

import ru.punkoff.stocksapp.ui.stocks.StocksViewState

interface RepositoryRemote {

    suspend fun getData(): StocksViewState

    suspend fun getDataBySymbol(symbol: String): StocksViewState
}