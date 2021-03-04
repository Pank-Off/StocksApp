package ru.punkoff.stocksapp.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.punkoff.stocksapp.di.DependencyGraph

class StocksApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StocksApp)
            modules(DependencyGraph.modules)
        }
    }
}