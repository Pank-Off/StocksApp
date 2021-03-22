package ru.punkoff.stocksapp.ui.favourite

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.room.StockDao
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.stocks.StocksViewState

class FavouriteViewModel(private val stockDao: StockDao) : BaseViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    fun getStocksFromDB() {
        stocksLiveData.postValue(StocksViewState.Loading)
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            Log.d(javaClass.simpleName, "From room: ${stockDao.getStocks()}")
            stocksLiveData.postValue(StocksViewState.StockValue(stockDao.getStocks().reversed()))
        }
    }

    fun saveToDB(stock: Stock) {
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stockDao.insert(stock)
        }
    }

    fun deleteFromDB(stock: Stock) {
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stockDao.delete(stock.ticker)
        }
    }

    fun observeViewState() = stocksLiveData
}