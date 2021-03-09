package ru.punkoff.stocksapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_cache_stocks")
data class CacheStock(
    val listStock: List<Stock>,
    @PrimaryKey
    val id: Int = 0,
)
