package ru.punkoff.stocksapp.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock

@Dao
interface StockDao {

    @Insert
    fun insert(stock: Stock)

    @Query("SELECT * FROM table_favourite_stocks")
    fun getStocks(): List<Stock>

    @Query("SELECT * FROM table_cache_stocks")
    fun getCacheStocks(): CacheStock

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(stocks: CacheStock)
}