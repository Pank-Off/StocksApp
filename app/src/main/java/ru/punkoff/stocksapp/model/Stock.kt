package ru.punkoff.stocksapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_stocks")
data class Stock(
    val ticket: String,
    val name: String,
    val price: Double,
    val stock: Double,
    val logo: String = "https://static.finnhub.io/logo/87cb30d8-80df-11ea-8951-00000000092a.png",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)