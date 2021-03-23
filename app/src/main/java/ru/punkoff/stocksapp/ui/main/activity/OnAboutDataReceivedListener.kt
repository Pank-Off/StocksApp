package ru.punkoff.stocksapp.ui.main.activity

import ru.punkoff.stocksapp.model.Stock

interface OnAboutDataReceivedListener {
    fun onDataReceived(stocks: List<Stock>)
    fun onDataLoading()
    fun onDataChange(stock: Stock)
}