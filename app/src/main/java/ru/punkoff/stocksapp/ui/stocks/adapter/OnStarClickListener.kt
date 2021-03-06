package ru.punkoff.stocksapp.ui.stocks.adapter

import ru.punkoff.stocksapp.model.Stock

fun interface OnStarClickListener {
    fun onClick(stock: Stock)
}