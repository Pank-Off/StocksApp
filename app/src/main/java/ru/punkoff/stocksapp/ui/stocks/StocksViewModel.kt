package ru.punkoff.stocksapp.ui.stocks

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import retrofit2.Response
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.retrofit.*
import ru.punkoff.stocksapp.model.room.StockDao
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import kotlin.math.floor

class StocksViewModel(private val api: StockApi, private val stockDao: StockDao) : BaseViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    @Volatile
    var stocks = mutableListOf<Stock>()

    init {
        getRequest()
    }

    fun saveToDB(stock: Stock) {
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stockDao.insert(stock)
        }
    }

    fun getRequest() {
        stocksLiveData.value = StocksViewState.Loading
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            getStocks()
        }
    }

    private fun getStocks() {
        val response: Response<List<StockSymbol>> =
            api.getStocks("US").execute()
        if (response.isSuccessful && response.body() != null) {
            val json: List<StockSymbol>? = response.body()
            if (json != null) {
                Log.d(javaClass.simpleName, "jsonSize: ${json.size}")
                createList(json)
            }
        }
    }

    private fun createList(json: List<StockSymbol>) = runBlocking {
        cancelJob()
        val jobs = mutableListOf<Job>()

        for (i in START until END) {
            jobs.add(viewModelCoroutineScope.launch(Dispatchers.IO) {
                getQuote(json[i])
            }
            )
        }
        Log.d(javaClass.simpleName, "runBlocking:Before")
        jobs.joinAll()
        Log.d(javaClass.simpleName, "runBlocking:After")
        stocksLiveData.postValue(StocksViewState.Value(stocks))
    }

    private fun getQuote(stockSymbol: StockSymbol) {
        Log.d(javaClass.simpleName, "symbolQuote ${stockSymbol.ticker}")
        val price = getPrice(stockSymbol.ticker)
        val percent = (price.currentPrice - price.previousPrice) / price.currentPrice * 100

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
        Log.d("STOCKS: ", stocks.toString())
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

    fun getStockBySymbol(symbol: String) {
        cancelJob()
        stocksLiveData.value = StocksViewState.Loading
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            val response: Response<StockLookup> =
                api.getStockByQuery(symbol).execute()
            if (response.isSuccessful && response.body() != null) {
                val json: StockLookup? = response.body()
                if (json != null) {
                    Log.d(javaClass.simpleName, "Symbol query: $symbol")
                    Log.d(javaClass.simpleName, "query:$json")
                    END = json.result.size
                    createList(json.result)
                }
            }
        }
    }

    fun observeViewState() = stocksLiveData

    companion object {
        const val START = 0
        private var END = 2
    }
}