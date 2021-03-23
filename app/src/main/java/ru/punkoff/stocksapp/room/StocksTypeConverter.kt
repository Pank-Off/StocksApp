package ru.punkoff.stocksapp.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.punkoff.stocksapp.model.Stock

class StocksTypeConverter {
    @TypeConverter
    fun stocksToJson(stocks: List<Stock>): String = Gson().toJson(stocks)

    @TypeConverter
    fun jsonToStocks(stocks: String) =
        Gson().fromJson(stocks, Array<Stock>::class.java).toList()
}