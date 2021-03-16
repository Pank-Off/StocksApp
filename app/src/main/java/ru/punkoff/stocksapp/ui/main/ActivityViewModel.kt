package ru.punkoff.stocksapp.ui.main

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.repository.RepositoryImplementation
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.stocks.StocksViewState

class ActivityViewModel(
    private val repository: RepositoryImplementation
) :
    BaseViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    fun getRequest() {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stocksLiveData.postValue(repository.getData())
        }
    }

    fun getRequestBySymbol(symbol: String) {
        cancelJob()
        stocksLiveData.value = StocksViewState.Loading
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stocksLiveData.postValue(repository.getDataBySymbol(symbol))
        }
    }

    fun observeViewState() = stocksLiveData
}