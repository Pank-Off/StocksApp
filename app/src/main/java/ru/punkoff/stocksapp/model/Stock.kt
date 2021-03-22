package ru.punkoff.stocksapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_favourite_stocks")
data class Stock(
    val ticker: String,
    val name: String,
    val price: Double,
    val difPrice: Double,
    val stock: Double,
    val logo: String,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var isFavourite: Boolean = false
) : Serializable