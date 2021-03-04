package ru.punkoff.stocksapp.model.retrofit

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IApiHelper {

    private val mStockApi: StockApi

    init {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        mStockApi = retrofit.create(StockApi::class.java)
    }

    fun getStocksApi() = mStockApi

    companion object {
        private const val BASE_URL = "https://finnhub.io/api/v1/"
    }
}