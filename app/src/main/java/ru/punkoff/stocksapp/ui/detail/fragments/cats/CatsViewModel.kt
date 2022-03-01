package ru.punkoff.stocksapp.ui.detail.fragments.cats

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState

class CatsViewModel(private val repository: Repository) : BaseViewModel() {

    private val _catsStateFlow = MutableStateFlow<CatsViewState>(CatsViewState.EMPTY)
    val catsStateFlow = _catsStateFlow.asStateFlow()

    fun getCat() {
        cancelJob()
        _catsStateFlow.value = CatsViewState.Loading
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            _catsStateFlow.value = repository.getCat()
        }
    }
}