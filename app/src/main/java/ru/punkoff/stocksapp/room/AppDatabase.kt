package ru.punkoff.stocksapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock

@Database(entities = [Stock::class, CacheStock::class], version = 1, exportSchema = false)
@TypeConverters(
    StocksTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
}