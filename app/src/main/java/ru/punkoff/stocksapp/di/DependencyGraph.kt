package ru.punkoff.stocksapp.di

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.model.retrofit.StockApi
import ru.punkoff.stocksapp.ui.stocks.StocksViewModel
import java.util.concurrent.TimeUnit

object DependencyGraph {
    private val apiModule by lazy {
        module {
            single { GsonBuilder().create() }
            single {
                Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(get()))
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .client(get())
                    .build()
            }
            single { get<Retrofit>().create(StockApi::class.java) }

            single {
                OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()
            }
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