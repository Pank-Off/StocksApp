package ru.punkoff.stocksapp.ui.stocks

import ru.punkoff.stocksapp.model.Stock

sealed class PaginationViewStateResult {
    data class Success(val stocks: List<Stock>) : PaginationViewStateResult()
    data class Error(val error: Exception) : PaginationViewStateResult()
}