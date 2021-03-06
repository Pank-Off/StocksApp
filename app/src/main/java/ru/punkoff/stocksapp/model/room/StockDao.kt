package ru.punkoff.stocksapp.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.punkoff.stocksapp.model.Stock

@Dao
interface StockDao {

    @Insert
    fun insert(stock: Stock)

    @Query("SELECT * FROM table_stocks")
    fun getStocks(): List<Stock>
}