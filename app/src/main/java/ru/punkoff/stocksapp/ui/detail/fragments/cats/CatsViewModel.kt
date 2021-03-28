package ru.punkoff.stocksapp.ui.detail.fragments.cats

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState

class CatsViewModel(private val repository: Repository) : BaseViewModel() {

    private val catsLiveData = MutableLiveData<CatsViewState>(CatsViewState.EMPTY)


    fun getCat() {
        cancelJob()
        catsLiveData.value = CatsViewState.Loading
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            catsLiveData.postValue(repository.getCat())
        }
    }

    fun observeViewState() = catsLiveData
}