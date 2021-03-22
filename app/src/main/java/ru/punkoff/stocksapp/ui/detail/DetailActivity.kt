package ru.punkoff.stocksapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.databinding.ActivityDetailBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.retrofit.Candle
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModel<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val stock = intent.getSerializableExtra(Constant.EXTRA_STOCK) as Stock
        setInitialData(stock)
        detailViewModel.observeViewState().observe(this) { result ->
            when (result) {
                is StocksViewState.CandleValue -> {
                    with(binding) {
                        graph.visibility = View.VISIBLE
                        noDataTextView.visibility = View.INVISIBLE
                        loadingBar.visibility = View.INVISIBLE
                        retryBtn.visibility = View.INVISIBLE
                    }
                    plotGraph(result.candle)
                }
                StocksViewState.EMPTY -> {
                    with(binding) {
                        noDataTextView.visibility = View.VISIBLE
                        graph.visibility = View.INVISIBLE
                        loadingBar.visibility = View.INVISIBLE
                        retryBtn.visibility = View.INVISIBLE
                    }
                }
                is StocksViewState.Error -> {
                    Toast.makeText(
                        this,
                        "\uD83D\uDE28 Wooops $result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.loadingBar.visibility = View.INVISIBLE
                    binding.retryBtn.visibility = View.VISIBLE
                }
                StocksViewState.Loading -> {
                    binding.loadingBar.visibility = View.VISIBLE
                    binding.retryBtn.visibility = View.INVISIBLE
                }
                is StocksViewState.StockValue -> Unit
            }
        }
        with(binding) {
            retryBtn.setOnClickListener {
                retryBtn.visibility = View.GONE
                loadingBar.visibility = View.VISIBLE
                detailViewModel.getCandles(stock.ticker)
            }
        }
    }

    private fun plotGraph(candle: Candle) {
        val listOfCandles = mutableListOf<DataPoint>()
        val simpleDateFormat = SimpleDateFormat("H.mm", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        for ((index, element) in candle.openPrice.withIndex()) {
            val time = Date(candle.time[index] * 1000)
            val dateString = simpleDateFormat.format(time)
            listOfCandles.add(DataPoint(dateString.toDouble(), element))
        }
        val series = LineGraphSeries(
            listOfCandles.toTypedArray()
        )
        binding.graph.addSeries(series)
    }

    private fun setInitialData(stock: Stock) {
        detailViewModel.getCandles(stock.ticker)
        val currentPriceBuilder = StringBuilder("$")
        currentPriceBuilder.append(stock.price)
        val changeStockBuilder = StringBuilder(stock.difPrice.toString())
        changeStockBuilder.append(" (${stock.stock}%)")
        val btnTextBuilder = java.lang.StringBuilder("Buy for ")
        btnTextBuilder.append(currentPriceBuilder)
        with(binding) {
            ticker.text = stock.ticker
            name.text = stock.name
            price.text = currentPriceBuilder
            changeStock.text = changeStockBuilder
            buyBtn.text = btnTextBuilder
        }
    }
}