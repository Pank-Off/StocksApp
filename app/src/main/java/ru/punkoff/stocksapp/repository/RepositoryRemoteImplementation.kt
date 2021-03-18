package ru.punkoff.stocksapp.repository

import android.util.Log
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.retrofit.StockApi
import ru.punkoff.stocksapp.model.retrofit.StockSymbol
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import kotlin.math.floor

class RepositoryRemoteImplementation(private val api: StockApi) : RepositoryRemote {
    private var stocks = mutableListOf<Stock>()
    private suspend fun getStocks(exchange: String) = api.getStocks(exchange).await()
    private suspend fun getStockByQuery(symbol: String) = api.getStockByQuery(symbol).await()
    private suspend fun getLogo(symbol: String) = api.getLogo(symbol).await()
    private suspend fun getPrice(symbol: String) = api.getPrice(symbol).await()

    override fun setCache(stocks: List<Stock>) {
        this.stocks = stocks as MutableList<Stock>
    }

    override suspend fun getData(): StocksViewState {
        var state: StocksViewState = StocksViewState.EMPTY
        val stocksRequest = getStocks(Constant.EXCHANGE).subList(START, END)
        stocksRequest.forEach {
            try {
                getDataForStock(it)
            } catch (exc: retrofit2.HttpException) {
                Log.e(javaClass.simpleName, exc.stackTraceToString())
            }
        }

        Log.d(javaClass.simpleName, "Stocks: ${stocks.size}")
        if (stocks.isNotEmpty()) {
            state = StocksViewState.Value(stocks)
        }
        return state
    }

    private suspend fun getDataForStock(stockSymbol: StockSymbol) {
        val price = getPrice(stockSymbol.ticker)
        val logo = getLogo(stockSymbol.ticker)
        Log.d(javaClass.simpleName, "LOGO: $logo")
        var percent = (price.currentPrice - price.previousPrice) / price.currentPrice * 100
        if (percent.isNaN()) {
            percent = 0.0
        }
        logo.logo?.let {
            stocks.add(
                0,
                Stock(
                    stockSymbol.displaySymbol,
                    stockSymbol.name,
                    floor(price.currentPrice * 1000) / 1000,
                    floor(percent * 1000) / 1000,
                    it
                )
            )
        }
    }

    override suspend fun getDataBySymbol(symbol: String): StocksViewState {
        Log.d(javaClass.simpleName, "Class: $this")
        var state: StocksViewState = StocksViewState.EMPTY
        val stocksRequest = getStockByQuery(symbol)
        stocksRequest.result.forEach {
            try {
                getDataForStock(it)
            } catch (exc: retrofit2.HttpException) {
                Log.e(javaClass.simpleName, exc.stackTraceToString())
            }
        }
        if (stocks.isNotEmpty()) {
            state = StocksViewState.Value(stocks)
        }
        return state
    }

    companion object {
        const val START = 0
        private var END = 10
    }
}