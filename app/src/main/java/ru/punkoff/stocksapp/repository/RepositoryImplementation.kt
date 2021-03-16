package ru.punkoff.stocksapp.repository

import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.stocks.StocksViewState

class RepositoryImplementation(
    private val repositoryRemote: RepositoryRemote,
    private val repositoryLocal: RepositoryLocal
) : Repository {

    override suspend fun getRequest(symbol: String?): StocksViewState =
        if (symbol == null) {
            repositoryRemote.getData()
        } else {
            repositoryRemote.getDataBySymbol(symbol)
        }

    override suspend fun saveStock(stock: Stock) {
        repositoryLocal.saveStock(stock)
    }

    override suspend fun deleteStock(ticker: String) {
        repositoryLocal.deleteStock(ticker)
    }

    override suspend fun getCache(): CacheStock =
        repositoryLocal.getCache()

    override suspend fun saveCache(stock: CacheStock) {
        repositoryLocal.saveCache(stock)
    }
}