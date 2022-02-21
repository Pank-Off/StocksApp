package ru.punkoff.stocksapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock

@Dao
interface StockDao {

    @Insert
    fun insert(stock: Stock)

    @Query("DELETE FROM table_favourite_stocks WHERE ticker = :ticker")
    fun delete(ticker: String)

    @Query("SELECT * FROM table_favourite_stocks")
    fun getStocks(): Flow<List<Stock>>

    @Query("SELECT * FROM table_cache_stocks")
    fun getCacheStocks(): CacheStock

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(stocks: CacheStock)
}