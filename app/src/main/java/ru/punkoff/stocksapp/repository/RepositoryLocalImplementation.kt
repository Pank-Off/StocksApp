package ru.punkoff.stocksapp.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.room.StockDao

class RepositoryLocalImplementation(private val stockDao: StockDao) : RepositoryLocal {
    override suspend fun getCache(): CacheStock = withContext(Dispatchers.IO) {
        stockDao.getCacheStocks()
    }

    override suspend fun saveStock(stock: Stock) = withContext(Dispatchers.IO) {
        stockDao.insert(stock)
    }

    override suspend fun saveCache(stocks: CacheStock) = withContext(Dispatchers.IO) {
        stockDao.insertList(stocks)
    }

    override suspend fun deleteStock(ticker: String) = withContext(Dispatchers.IO) {
        stockDao.delete(ticker)
    }
}