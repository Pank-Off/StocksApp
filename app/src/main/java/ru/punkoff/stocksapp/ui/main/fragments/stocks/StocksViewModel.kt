package ru.punkoff.stocksapp.ui.main.fragments.stocks

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.punkoff.stocksapp.model.CacheStock
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.utils.Constant

class StocksViewModel(
    private val repository: Repository
) : BaseViewModel() {
    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)
    val stocksPaginationLiveData: LiveData<PaginationViewStateResult> = currentQuery.switchMap {
        liveData {
            val repos = repository.getSearchResultStream(it).asLiveData(Dispatchers.Main)
            emitSource(repos)
        }
    }

    init {
        var stocks = emptyList<Stock>()
        runBlocking {
            cancelJob()
            viewModelCoroutineScope.launch(Dispatchers.IO) {
                stocks = repository.getCache().listStock
                repository.setCache(stocks)
                Log.d(javaClass.simpleName, "Cache: $stocks")
            }.join()
            if (stocks.isNotEmpty()) {
                stocksLiveData.value = StocksViewState.StockValue(stocks)
            } else {
                getRequest()
            }
        }
    }

    private fun getRequest() {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stocksLiveData.postValue(repository.getRequest())
        }
    }

    fun saveToDB(stock: Stock) {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            repository.saveStock(stock)
        }
    }

    fun saveCache(stocks: List<Stock>) {
        Log.d(javaClass.simpleName, "Cache: $stocks")
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            repository.saveCache(CacheStock(stocks))
        }
    }

    fun deleteFromDB(stock: Stock) {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            repository.deleteStock(stock.ticker)
        }
    }

    fun searchStocks(query: String) {
        currentQuery.value = query
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = currentQuery.value
            if (immutableQuery != null) {
                viewModelScope.launch {
                    repository.requestMore(immutableQuery)
                }
            }
        }
    }

    fun observeViewState() = stocksLiveData

    fun setViewState(state: StocksViewState) {
        stocksLiveData.value = state
    }

    companion object {
        private const val DEFAULT_QUERY = Constant.EXCHANGE
        private const val VISIBLE_THRESHOLD = 5
    }
}