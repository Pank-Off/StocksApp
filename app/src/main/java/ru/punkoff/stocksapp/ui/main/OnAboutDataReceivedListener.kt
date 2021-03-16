package ru.punkoff.stocksapp.ui.main

import ru.punkoff.stocksapp.model.Stock

interface OnAboutDataReceivedListener {
    fun onDataReceived(stocks: List<Stock>)
    fun onDataLoading()
}