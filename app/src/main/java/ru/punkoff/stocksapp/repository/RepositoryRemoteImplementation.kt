package ru.punkoff.stocksapp.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.retrofit.StockApi
import ru.punkoff.stocksapp.model.retrofit.StockSymbol
import ru.punkoff.stocksapp.ui.stocks.PaginationViewStateResult
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import java.io.IOException
import kotlin.math.floor

private const val FINHUB_STARTING_PAGE_INDEX = 1

class RepositoryRemoteImplementation(private val api: StockApi) : RepositoryRemote {
    private var stocks = mutableListOf<Stock>()
    private suspend fun getStocks(exchange: String) = api.getStocks(exchange).await()
    private suspend fun getStockByQuery(symbol: String) = api.getStockByQuery(symbol).await()
    private suspend fun getLogo(symbol: String) = api.getLogo(symbol).await()
    private suspend fun getPrice(symbol: String) = api.getPrice(symbol).await()
    private suspend fun getCandles(symbol: String) = api.getCandles(symbol).await()

    private val searchResults = MutableSharedFlow<PaginationViewStateResult>(replay = 1)
    private var isRequestInProgress = false

    private var lastRequestedPage = FINHUB_STARTING_PAGE_INDEX
    override fun setCache(stocks: List<Stock>) {
        this.stocks = stocks as MutableList<Stock>
    }

    override suspend fun getCandlesData(ticker: String): StocksViewState {
        var state: StocksViewState = StocksViewState.EMPTY
        val candle = getCandles(ticker)
        if (candle.exist != Constant.NO_DATA) {
            state = StocksViewState.CandleValue(candle)
        }
        return state
    }

    override suspend fun requestMore(query: String) {
        if (isRequestInProgress) return
        val successful = requestData(query)
        if (successful) {
            lastRequestedPage++
        }
    }

    override suspend fun getSearchResultStream(query: String): Flow<PaginationViewStateResult> {
        lastRequestedPage = 1
        requestData(query)
        return searchResults
    }

    private suspend fun requestData(query: String): Boolean {
        isRequestInProgress = true
        var successful = false
        try {
            val stocksSymbol =
                api.searchPagination(query, lastRequestedPage, NETWORK_PAGE_SIZE)
                    .subList(START, END)
            stocksSymbol.forEach {
                try {
                    getDataForStock(it, stocks.size)
                } catch (exc: HttpException) {
                    Log.e(javaClass.simpleName, exc.stackTraceToString())
                }
            }
            Log.d(javaClass.simpleName, "Stocks: ${stocks.size}")
            searchResults.emit(PaginationViewStateResult.Success(stocks))
            successful = true
        } catch (exception: IOException) {
            searchResults.emit(PaginationViewStateResult.Error(exception))
        } catch (exception: HttpException) {
            searchResults.emit(PaginationViewStateResult.Error(exception))
        }
        isRequestInProgress = false
        return successful
    }

    override suspend fun getData(): StocksViewState {
        var state: StocksViewState = StocksViewState.EMPTY
        val stocksRequest = getStocks(Constant.EXCHANGE).subList(START, END)
        stocksRequest.forEach {
            try {
                getDataForStock(it)
            } catch (exc: HttpException) {
                Log.e(javaClass.simpleName, exc.stackTraceToString())
            }
        }

        Log.d(javaClass.simpleName, "Stocks: ${stocks.size}")
        if (stocks.isNotEmpty()) {
            state = StocksViewState.StockValue(stocks)
        }
        return state
    }

    private suspend fun getDataForStock(stockSymbol: StockSymbol, position: Int = 0) {
        val price = getPrice(stockSymbol.ticker)
        val logo = getLogo(stockSymbol.ticker)
        Log.d(javaClass.simpleName, "LOGO: $logo")
        var percent = (price.currentPrice - price.previousPrice) / price.currentPrice * 100
        if (percent.isNaN()) {
            percent = 0.0
        }
        logo.logo?.let {
            stocks.add(
                position,
                Stock(
                    stockSymbol.displaySymbol,
                    stockSymbol.name,
                    floor(price.currentPrice * 100) / 100,
                    floor((price.currentPrice - price.previousPrice) * 100) / 100,
                    floor(percent * 100) / 100,
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
                getDataForStock(it, 0)
            } catch (exc: HttpException) {
                Log.e(javaClass.simpleName, exc.stackTraceToString())
            }
        }
        if (stocks.isNotEmpty()) {
            state = StocksViewState.StockValue(stocks)
        }
        return state
    }

    companion object {
        private const val START = 0
        private const val END = 2
        private const val NETWORK_PAGE_SIZE = 50
    }
}