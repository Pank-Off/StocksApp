package ru.punkoff.stocksapp.ui.main.fragments.stocks.adapter

import ru.punkoff.stocksapp.model.Stock

fun interface OnStockClickListener {
    fun onClick(stock: Stock, position: Int)
}