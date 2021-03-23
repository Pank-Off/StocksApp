package ru.punkoff.stocksapp.ui.main.fragments.stocks

import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.Candle

sealed class StocksViewState {
    object Loading : StocksViewState()
    object EMPTY : StocksViewState()
    data class StockValue(val stocks: List<Stock>) : StocksViewState()
    data class CandleValue(val candle: Candle) : StocksViewState()
    data class Error(val error: Exception) : StocksViewState()
}