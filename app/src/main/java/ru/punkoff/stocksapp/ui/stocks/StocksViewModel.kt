package ru.punkoff.stocksapp.ui.stocks

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.retrofit.StockApi
import ru.punkoff.stocksapp.model.room.StockDao
import ru.punkoff.stocksapp.repository.RepositoryImplementation
import ru.punkoff.stocksapp.ui.base.BaseViewModel

class StocksViewModel(
    private val stockDao: StockDao,
    private val repository: RepositoryImplementation
) : BaseViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    var stocks = mutableListOf<Stock>()

    init {
        runBlocking {
            viewModelCoroutineScope.launch(Dispatchers.IO) {
                stocks = stockDao.getCacheStocks().listStock as MutableList<Stock>
                Log.d(javaClass.simpleName, "StockDao: ${stockDao.getCacheStocks()}")
            }.join()
            if (stocks.size != 0) {
                stocksLiveData.value = StocksViewState.Value(stocks)
            } else {
                getRequest()
            }
        }
    }

    fun saveToDB(stock: Stock) {
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stockDao.insert(stock)
        }
    }

    fun saveCache() {
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stockDao.insertList(CacheStock(stocks))
        }
    }

    fun deleteFromDB(stock: Stock) {
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stockDao.delete(stock.ticker)
        }
    }

    private fun getRequest() {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stocksLiveData.postValue(repository.getData())
        }
    }

    fun observeViewState() = stocksLiveData

    fun setViewState(state: StocksViewState) {
        stocksLiveData.value = state
    }
}