package ru.punkoff.stocksapp.ui.stocks

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.retrofit.IApiHelper
import ru.punkoff.stocksapp.model.retrofit.StockExample
import ru.punkoff.stocksapp.ui.base.BaseViewModel

class StocksViewModel(private val apiHelper: IApiHelper) : BaseViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    private fun createList() {
        val stocks = mutableListOf<Stock>()
        for (stock in 0..5) {
            stocks.add(Stock("A $stock", "Azaza", "123", "0.13"))
        }
        stocksLiveData.postValue(StocksViewState.Value(stocks))
    }

    fun getRequest() {
        stocksLiveData.value = StocksViewState.Loading
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            val response: Response<List<StockExample>> =
                apiHelper.getStocksApi().getRequest("US").execute()
            if (response.isSuccessful && response.body() != null) run {
                createList()
                val json: List<StockExample>? = response.body()
                if (json != null) {
                    for (stock in json) {
                        Log.d("STOCKS: ", stock.toString())
                    }
                }
            }
        }
    }

    fun observeViewState() = stocksLiveData
}