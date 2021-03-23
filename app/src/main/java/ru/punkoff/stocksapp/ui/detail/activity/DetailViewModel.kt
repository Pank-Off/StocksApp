package ru.punkoff.stocksapp.ui.detail.activity

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState

class DetailViewModel(private val repository: Repository) : BaseViewModel() {

    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    fun getCandles(symbol: String) {
        cancelJob()
        stocksLiveData.value = StocksViewState.Loading
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stocksLiveData.postValue(repository.getCandles(symbol))
        }
    }

    fun observeViewState() = stocksLiveData
}