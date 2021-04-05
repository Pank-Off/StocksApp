package ru.punkoff.stocksapp.ui.detail.fragments.chart

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.punkoff.stocksapp.model.SocketData
import ru.punkoff.stocksapp.repository.Repository
import ru.punkoff.stocksapp.retrofit.FinWebSocketListener
import ru.punkoff.stocksapp.ui.base.BaseViewModel
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant

class ChartViewModel(
    private val repository: Repository,
    finWebSocketListener: FinWebSocketListener,
    private val gson: Gson
) : BaseViewModel() {

    private val stocksLiveData = MutableLiveData<StocksViewState>(StocksViewState.EMPTY)

    private val socketLiveData = MediatorLiveData<SocketData>()

    private val msgLiveData = finWebSocketListener.observeMessage()
    fun getCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String = Constant.RESOLUTION_D
    ) {
        cancelJob()
        stocksLiveData.value = StocksViewState.Loading
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            stocksLiveData.postValue(repository.getCandles(symbol, from, to, resolution))
        }
    }

    fun startSocket(symbol: String) {
        cancelJob()
        viewModelCoroutineScope.launch(Dispatchers.IO) {
            repository.startSocket(symbol)
        }
        socketLiveData.addSource(msgLiveData) { msg ->
            socketLiveData.postValue(gson.fromJson(msg, SocketData::class.java))
        }
    }

    fun closeSocket() {
        repository.closeSocket()
        socketLiveData.removeSource(msgLiveData)
    }

    fun observeViewState() = stocksLiveData

    fun observeMessage() = socketLiveData
}