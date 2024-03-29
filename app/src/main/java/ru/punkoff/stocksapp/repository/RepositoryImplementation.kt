package ru.punkoff.stocksapp.repository

import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepositoryImplementation(
    private val repositoryRemote: RepositoryRemote,
    private val repositoryLocal: RepositoryLocal
) : Repository {

    override suspend fun getSearchResultStream(query: String) =
        repositoryRemote.getSearchResultStream(query)

    override suspend fun startSocket(symbol: String) {
        repositoryRemote.startSocket(symbol)
    }

    override fun closeSocket() {
        repositoryRemote.closeSocket()
    }

    override fun setCache(stocks: List<Stock>) {
        repositoryRemote.setCache(stocks)
    }

    override suspend fun updatePrice(): StocksViewState =
        try {
            repositoryRemote.updatePrice()
        } catch (exc: UnknownHostException) {
            StocksViewState.Error(exc)
        } catch (exc: SocketTimeoutException) {
            StocksViewState.Error(exc)
        }

    override suspend fun getProfile(ticker: String): StocksViewState =
        try {
            repositoryRemote.getProfileData(ticker)
        } catch (exc: UnknownHostException) {
            StocksViewState.Error(exc)
        } catch (exc: SocketTimeoutException) {
            StocksViewState.Error(exc)
        }

    override suspend fun getNews(ticker: String, from: String, to: String): StocksViewState =
        try {
            repositoryRemote.getNewsData(ticker, from, to)
        } catch (exc: UnknownHostException) {
            StocksViewState.Error(exc)
        } catch (exc: SocketTimeoutException) {
            StocksViewState.Error(exc)
        }

    override suspend fun getCandles(
        ticker: String,
        from: Long,
        to: Long,
        resolution: String
    ): StocksViewState =
        try {
            repositoryRemote.getCandlesData(ticker, from, to, resolution)
        } catch (exc: UnknownHostException) {
            StocksViewState.Error(exc)
        } catch (exc: SocketTimeoutException) {
            StocksViewState.Error(exc)
        }

    override suspend fun requestMore(query: String) {
        repositoryRemote.requestMore(query)
    }

    override suspend fun getRequest(symbol: String?): StocksViewState =
        try {
            if (symbol == null) {
                repositoryRemote.getData()
            } else {
                repositoryRemote.getDataBySymbol(symbol)
            }
        } catch (exc: UnknownHostException) {
            StocksViewState.Error(exc)
        } catch (exc: SocketTimeoutException) {
            StocksViewState.Error(exc)
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

    override suspend fun getCat() =
        repositoryRemote.getCat()
}