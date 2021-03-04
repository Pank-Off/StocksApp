package ru.punkoff.stocksapp.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.punkoff.stocksapp.ui.stocks.StocksViewModel

object DependencyGraph {
    private val viewModelModule by lazy {
        module {
            viewModel {
                StocksViewModel()
            }
        }
    }

    val modules = listOf(viewModelModule)
}