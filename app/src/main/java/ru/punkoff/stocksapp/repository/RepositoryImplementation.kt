package ru.punkoff.stocksapp.repository

import android.util.Log
import kotlinx.coroutines.*
import retrofit2.Response
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.retrofit.*
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import kotlin.math.floor

class RepositoryImplementation(private val api: StockApi) : Repository {
    lateinit var state: StocksViewState
    var stocks = mutableListOf<Stock>()
    override suspend fun getData(): StocksViewState {
        val response: Response<List<StockSymbol>> =
            api.getStocks("US").execute()
        if (response.isSuccessful && response.body() != null) {
            val json: List<StockSymbol>? = response.body()
            if (json != null) {
                Log.d(javaClass.simpleName, "jsonSize: ${json.size}")
                state = createList(json)
                return state
            }
        }
        return StocksViewState.EMPTY
    }

    override suspend fun getDataBySymbol(symbol: String): StocksViewState {
        val response: Response<StockLookup> =
            api.getStockByQuery(symbol).execute()
        if (response.isSuccessful && response.body() != null) {
            val json: StockLookup? = response.body()
            if (json != null) {
                Log.d(javaClass.simpleName, "Symbol query: $symbol")
                Log.d(javaClass.simpleName, "query:$json")
                END = json.result.size
                state = createList(json.result)
                return state
            }
        }
        return StocksViewState.EMPTY
    }

    private fun createList(json: List<StockSymbol>): StocksViewState {
        runBlocking {
            val jobs = mutableListOf<Job>()
            for (i in START until END) {
                jobs.add(GlobalScope.launch(Dispatchers.IO) {
                    getQuote(json[i])
                }
                )
            }
            Log.d(javaClass.simpleName, "runBlocking:Before")
            jobs.joinAll()
            Log.d(javaClass.simpleName, "runBlocking:After")

            Log.d(javaClass.simpleName, "Stocks: $stocks")
        }
        return StocksViewState.Value(stocks)
    }

    private fun getQuote(stockSymbol: StockSymbol) {
        Log.d(javaClass.simpleName, "symbolQuote ${stockSymbol.ticker}")
        val price = getPrice(stockSymbol.ticker)
        var percent = (price.currentPrice - price.previousPrice) / price.currentPrice * 100
        Log.d(javaClass.simpleName, "Nan ${stockSymbol.ticker} $percent")
        if (percent.isNaN()) {
            percent = 0.0
        }
        val logo = getLogo(stockSymbol.ticker)
        stocks.add(
            0,
            Stock(
                stockSymbol.displaySymbol,
                stockSymbol.name,
                floor(price.currentPrice * 1000) / 1000,
                floor(percent * 1000) / 1000,
                logo.logo
            )
        )
    }

    private fun getLogo(symbol: String): StockLogo {
        val response: Response<StockLogo> =
            api.getLogo(symbol).execute()
        if (response.isSuccessful && response.body() != null) {
            val json: StockLogo? = response.body()
            if (json != null && json.logo != null) {
                Log.d(javaClass.simpleName, "LOGO: $json")
                return json
            }
        }
        return StockLogo("")
    }

    private fun getPrice(symbol: String): StockPrice {
        val response: Response<StockPrice> =
            api.getPrice(symbol).execute()
        if (response.isSuccessful && response.body() != null) {
            val json: StockPrice? = response.body()
            if (json != null) {
                Log.d(javaClass.simpleName, "PRICE Symbol in json: $symbol")
                Log.d(javaClass.simpleName, "PRICE:$json")
                return json
            }
        }
        Log.d(javaClass.simpleName, "PRICE Symbol outer json: $symbol")
        return StockPrice(0.0, 0.0)
    }

    companion object {
        const val START = 0
        private var END = 10
    }
}