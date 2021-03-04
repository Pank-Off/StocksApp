package ru.punkoff.stocksapp.ui.stocks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.punkoff.stocksapp.model.Stock

class StocksViewModel : ViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    init {
        val stocks = mutableListOf<Stock>()
        for (stock in 0..5) {
            stocks.add(Stock("A $stock", "Azaza", "123", "0.13"))
        }
        stocksLiveData.value = StocksViewState.Value(stocks)
    }

    fun observeViewState() = stocksLiveData
}