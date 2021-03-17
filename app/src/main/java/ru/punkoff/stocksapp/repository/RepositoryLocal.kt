package ru.punkoff.stocksapp.repository

import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock

interface RepositoryLocal {
    suspend fun getCache(): CacheStock
    suspend fun saveStock(stock: Stock)
    suspend fun saveCache(stocks: CacheStock)
    suspend fun deleteStock(ticker: String)
}