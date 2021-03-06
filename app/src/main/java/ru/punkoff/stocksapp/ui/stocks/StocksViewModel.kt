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
    val stocks = mutableListOf<Stock>()
    private fun createList(json: List<StockSymbol>) {
        cancelJob()
        runBlocking {
            val jobs: List<Job> = (1..20).map {
                viewModelCoroutineScope.launch(Dispatchers.IO) {
                    getQuote(json[it])
                }
            }
            Log.d(javaClass.simpleName, "runBlocking:Before")
            jobs.joinAll()
            Log.d(javaClass.simpleName, "runBlocking:After")
            stocksLiveData.postValue(StocksViewState.Value(stocks))
        }
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

    private fun getQuote(stock: StockSymbol) {
        val price = getPrice(stock.ticker)
        val logo = getLogo(stock.ticker)
        val percent = (price.currentPrice - price.previousPrice) / price.currentPrice * 100
        stocks.add(
            Stock(
                stock.ticker,
                stock.name,
                floor(price.currentPrice * 1000) / 1000,
                floor(percent * 1000) / 1000,
                logo.logo
            )
        )
        Log.d("STOCKS: ", stock.toString())
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

    private fun getLogo(symbol: String): StockLogo {
        val response: Response<StockLogo> =
            api.getLogo(symbol).execute()
        if (response.isSuccessful && response.body() != null) {
            val json: StockLogo? = response.body()
            if (json != null) {
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

    fun observeViewState() = stocksLiveData
}