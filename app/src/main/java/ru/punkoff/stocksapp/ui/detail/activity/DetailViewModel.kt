package ru.punkoff.stocksapp.ui.detail.activity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState

class DetailViewModel(private val repository: Repository) : BaseViewModel() {

    private val _stocksStateFlow = MutableStateFlow<StocksViewState>(StocksViewState.EMPTY)
    val stocksLiveData = _stocksStateFlow.asStateFlow()
}