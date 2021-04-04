package ru.punkoff.stocksapp.retrofit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


class FinWebSocketListener : WebSocketListener() {

    private val msgLiveData = MutableLiveData<String>()

    fun observeMessage() = msgLiveData

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.e("WebSockets", "Connection accepted!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        msgLiveData.postValue(text)
        Log.e("WebSockets", "Receiving : $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.e("WebSockets", "Receiving bytes : " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.e("WebSockets", "Closing : $code / $reason");
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSockets", "Failure : $t")
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}