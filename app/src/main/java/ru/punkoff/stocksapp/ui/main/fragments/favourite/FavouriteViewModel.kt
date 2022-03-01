package ru.punkoff.stocksapp.ui.main.fragments.favourite

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.room.StockDao
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState

class FavouriteViewModel(private val stockDao: StockDao) : BaseViewModel() {
    private val _stocksFlow = MutableStateFlow<StocksViewState>(StocksViewState.EMPTY)
    val stocksFlow = _stocksFlow.stateIn(
        scope = viewModelCoroutineScope,
        initialValue = StocksViewState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    init {
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stockDao.getStocks().collect {
                _stocksFlow.value = StocksViewState.StockValue(it.reversed())
            }
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
}