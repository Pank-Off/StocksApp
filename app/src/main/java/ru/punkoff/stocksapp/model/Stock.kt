package ru.punkoff.stocksapp.model

data class Stock(
    val ticket: String,
    val name: String,
    val price: String,
    val stock: String,
    val logo: String = "https://static.finnhub.io/logo/87cb30d8-80df-11ea-8951-00000000092a.png"
)