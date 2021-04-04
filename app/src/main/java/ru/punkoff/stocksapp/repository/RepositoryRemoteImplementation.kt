package ru.punkoff.stocksapp.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import org.json.JSONObject
import retrofit2.HttpException
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.StockSymbol
import ru.punkoff.stocksapp.retrofit.FinWebSocketListener
import ru.punkoff.stocksapp.retrofit.FinWebSocketListener.Companion.NORMAL_CLOSURE_STATUS
import ru.punkoff.stocksapp.retrofit.StockApi
import ru.punkoff.stocksapp.ui.detail.fragments.cats.CatsViewState
import ru.punkoff.stocksapp.ui.main.fragments.stocks.PaginationViewStateResult
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import java.io.IOException
import kotlin.math.floor

private const val FINHUB_STARTING_PAGE_INDEX = 1

class RepositoryRemoteImplementation(
    private val api: StockApi,
    private val request: Request,
    private val listener: FinWebSocketListener
) :
    RepositoryRemote {
    private var stocks = mutableListOf<Stock>()
    private suspend fun getStocks(exchange: String) = api.getStocks(exchange).await()
    private suspend fun getStockByQuery(symbol: String) = api.getStockByQuery(symbol).await()
    private suspend fun getLogo(symbol: String) = api.getLogo(symbol).await()
    private suspend fun getPrice(symbol: String) = api.getPrice(symbol).await()
    private suspend fun getCandles(symbol: String, from: Long, to: Long, resolution: String) =
        api.getCandles(symbol = symbol, from = from, to = to, resolution = resolution).await()

    private suspend fun getNews(symbol: String, from: String, to: String) =
        api.getNews(symbol = symbol, from = from, to = to).await()

    private suspend fun getProfile(symbol: String) = api.getCompanyProfile(symbol = symbol).await()
    private val searchResults = MutableSharedFlow<PaginationViewStateResult>(replay = 1)
    private var isRequestInProgress = false

    private var lastRequestedPage = FINHUB_STARTING_PAGE_INDEX

    private lateinit var webSocket: WebSocket
    override fun setCache(stocks: List<Stock>) {
        this.stocks = stocks as MutableList<Stock>
    }

    override suspend fun startSocket(symbol: String) {
        val jsonObject = JSONObject()
        jsonObject.put("type", "subscribe")
        jsonObject.put("symbol", symbol)
        val client = OkHttpClient()
        webSocket = client.newWebSocket(request, listener)
        webSocket.send(jsonObject.toString())
        Log.e("WebSockets", jsonObject.toString())
        webSocket.send(jsonObject.toString())
        client.dispatcher.executorService.shutdown()
    }

    override fun closeSocket() {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
    }

    override suspend fun getProfileData(ticker: String): StocksViewState {
        var state: StocksViewState = StocksViewState.EMPTY
        val profile = getProfile(ticker)
        profile.description?.let {
            state = StocksViewState.SummaryValue(profile)
        }
        return state
    }

    override suspend fun getNewsData(ticker: String, from: String, to: String): StocksViewState {
        var state: StocksViewState = StocksViewState.EMPTY
        val news = getNews(ticker, from, to)
        if (news.isNotEmpty()) {
            state = StocksViewState.NewsValue(news)
        }
        return state
    }

    override suspend fun getCandlesData(
        ticker: String,
        from: Long,
        to: Long,
        resolution: String
    ): StocksViewState {
        var state: StocksViewState = StocksViewState.EMPTY
        val candle = getCandles(ticker, from, to, resolution)
        Log.d(javaClass.simpleName, "Candle $candle")
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
                    it,
                    webUrl = logo.weburl,
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

    override suspend fun getCat(): CatsViewState {
        val state: CatsViewState
        val cat = api.getCat().await()
        state = CatsViewState.CatValue(cat)
        return state
    }

    companion object {
        private const val START = 0
        private const val END = 2
        private const val NETWORK_PAGE_SIZE = 50
    }
}