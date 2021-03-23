package ru.punkoff.stocksapp.ui.main.fragments.stocks.adapter

import ru.punkoff.stocksapp.model.Stock

interface OnStarClickListener {
    fun deleteStock(stock: Stock)
    fun saveStock(stock: Stock)
}