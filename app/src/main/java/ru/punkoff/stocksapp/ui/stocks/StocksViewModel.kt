package ru.punkoff.stocksapp.ui.stocks

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.ui.base.BaseViewModel

class StocksViewModel(
    private val repository: Repository
) : BaseViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    init {
        var stocks = emptyList<Stock>()
        runBlocking {
            cancelJob()
            viewModelCoroutineScope.launch(Dispatchers.IO) {
                stocks = repository.getCache().listStock
                repository.setCache(stocks)
                Log.d(javaClass.simpleName, "Cache: $stocks")
            }.join()
            if (stocks.isNotEmpty()) {
                stocksLiveData.value = StocksViewState.Value(stocks)
            } else {
                getRequest()
            }
        }
    }

    fun saveToDB(stock: Stock) {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            repository.saveStock(stock)
        }
    }

    fun saveCache(stocks: List<Stock>) {
        Log.d(javaClass.simpleName, "Cache: $stocks")
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            repository.saveCache(CacheStock(stocks))
        }
    }

    fun deleteFromDB(stock: Stock) {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            repository.deleteStock(stock.ticker)
        }
    }

    private fun getRequest() {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stocksLiveData.postValue(repository.getRequest())
        }
    }

    fun observeViewState() = stocksLiveData

    fun setViewState(state: StocksViewState) {
        stocksLiveData.value = state
    }
}