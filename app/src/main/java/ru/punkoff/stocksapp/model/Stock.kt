package ru.punkoff.stocksapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_favourite_stocks")
data class Stock(
    val ticker: String,
    val name: String,
    val price: Double,
    val stock: Double,
    val logo: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var isFavourite: Boolean = false
)