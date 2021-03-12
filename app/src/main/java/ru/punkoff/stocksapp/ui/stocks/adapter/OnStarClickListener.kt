package ru.punkoff.stocksapp.ui.stocks.adapter

import ru.punkoff.stocksapp.model.Stock

interface OnStarClickListener {
    fun deleteStock(stock: Stock)
    fun saveStock(stock: Stock)
}