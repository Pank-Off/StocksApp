package ru.punkoff.stocksapp.ui.stocks

import ru.punkoff.stocksapp.model.Stock

sealed class StocksViewState {
    object Loading : StocksViewState()
    object EMPTY : StocksViewState()
    data class Value(val stocks: List<Stock>) : StocksViewState()
    data class Error(val error: Exception) : StocksViewState()
}