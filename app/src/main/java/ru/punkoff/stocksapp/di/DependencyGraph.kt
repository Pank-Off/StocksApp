package ru.punkoff.stocksapp.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.model.retrofit.StockApi
import ru.punkoff.stocksapp.model.room.AppDatabase
import ru.punkoff.stocksapp.repository.*
import ru.punkoff.stocksapp.ui.favourite.FavouriteViewModel
import ru.punkoff.stocksapp.ui.main.ActivityViewModel
import ru.punkoff.stocksapp.ui.stocks.StocksViewModel
import java.util.concurrent.TimeUnit

object DependencyGraph {

    private val repositoryModule by lazy {
        module {
            single {
                RepositoryImplementation(get(), get())
            } bind Repository::class
            single {
                RepositoryRemoteImplementation(get())
            } bind RepositoryRemote::class
            single {
                RepositoryLocalImplementation(get())
            } bind RepositoryLocal::class
        }
    }
    private val roomModule by lazy {
        module {
            single {
                Room.databaseBuilder(
                    get(),
                    AppDatabase::class.java,
                    "room_database"
                )
                    .build()
            }
            single {
                get<AppDatabase>().stockDao()
            }
        }
    }
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
                OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build()
            }
        }
    }
    private val viewModelModule by lazy {
        module {
            viewModel {
                StocksViewModel(get())
            }
            viewModel {
                FavouriteViewModel(get())
            }
            viewModel {
                ActivityViewModel(get())
            }
        }
    }

    val modules = listOf(viewModelModule, apiModule, roomModule, repositoryModule)
}