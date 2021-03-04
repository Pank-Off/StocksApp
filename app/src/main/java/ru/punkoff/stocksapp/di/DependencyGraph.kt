package ru.punkoff.stocksapp.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.punkoff.stocksapp.model.retrofit.IApiHelper
import ru.punkoff.stocksapp.ui.stocks.StocksViewModel

object DependencyGraph {
    private val apiModule by lazy {
        module {
            single { IApiHelper() }
        }
    }
    private val viewModelModule by lazy {
        module {
            viewModel {
                StocksViewModel(get())
            }
        }
    }

    val modules = listOf(viewModelModule, apiModule)
}