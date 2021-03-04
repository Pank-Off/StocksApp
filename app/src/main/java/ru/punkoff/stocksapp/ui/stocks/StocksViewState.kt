package ru.punkoff.stocksapp.ui.stocks

import ru.punkoff.stocksapp.model.Stock

sealed class StocksViewState {
    data class Value(val stocks: List<Stock>) : StocksViewState()
    object Loading : StocksViewState()
    object EMPTY : StocksViewState()
}