package ru.punkoff.stocksapp.repository

import kotlinx.coroutines.flow.Flow
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.stocks.PaginationViewStateResult
import ru.punkoff.stocksapp.ui.stocks.StocksViewState

interface RepositoryRemote {

    suspend fun requestMore(query: String)
    suspend fun getSearchResultStream(query: String): Flow<PaginationViewStateResult>
    fun setCache(stocks: List<Stock>)

    suspend fun getData(): StocksViewState

    suspend fun getDataBySymbol(symbol: String): StocksViewState
}