package ru.punkoff.stocksapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_stocks")
data class Stock(
    val ticket: String,
    val name: String,
    val price: Double,
    val stock: Double,
    val logo: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)