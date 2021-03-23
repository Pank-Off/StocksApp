package ru.punkoff.stocksapp.ui.detail.fragments.cats

import androidx.lifecycle.MutableLiveData
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState

class CatsViewModel(private val repository: Repository) : BaseViewModel() {

    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    fun observeViewState() = stocksLiveData
}