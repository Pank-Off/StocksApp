package ru.punkoff.stocksapp.ui.detail.fragments.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ChartFragmentBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.model.Candle
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class ChartFragment : Fragment() {
    private val chartViewModel by viewModel<ChartViewModel>()
    private var _binding: ChartFragmentBinding? = null
    private val binding: ChartFragmentBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChartFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stock = arguments?.get(Constant.EXTRA_STOCK) as Stock
        setInitialData(stock)
        chartViewModel.observeViewState().observe(viewLifecycleOwner) { result ->
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
                        context,
                        "\uD83D\uDE28 Wooops $result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    with(binding) {
                        loadingBar.visibility = View.INVISIBLE
                        retryBtn.visibility = View.VISIBLE
                    }

                }
                StocksViewState.Loading -> {

                    with(binding) {
                        loadingBar.visibility = View.VISIBLE
                        retryBtn.visibility = View.INVISIBLE
                    }
                }
                is StocksViewState.StockValue -> Unit
            }
        }
        with(binding) {
            retryBtn.setOnClickListener {
                retryBtn.visibility = View.GONE
                loadingBar.visibility = View.VISIBLE
                chartViewModel.getCandles(stock.ticker)
            }
        }
    }

    private fun plotGraph(candle: Candle) {
        val listOfCandles = mutableListOf<DataPoint>()
        val simpleDateFormat = SimpleDateFormat("H:mm", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        for ((index, element) in candle.openPrice.withIndex()) {
            val time = Date(candle.time[index] * 1000)
            listOfCandles.add(DataPoint(time, element))
        }
        val series = LineGraphSeries(
            listOfCandles.toTypedArray()
        )
        val dayFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val graphTitleBuilder = StringBuilder("Stock candles for ")
        graphTitleBuilder.append(dayFormat.format(listOfCandles[0].x))
        with(binding) {
            graph.title = graphTitleBuilder.toString()
            graph.addSeries(series)
            graph.gridLabelRenderer.numHorizontalLabels = 5
            graph.gridLabelRenderer.setHumanRounding(false, true)
            graph.viewport.isXAxisBoundsManual = true
            graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    return if (isValueX) {
                        simpleDateFormat.format(Date(value.toLong()))
                    } else {
                        super.formatLabel(value, isValueX)
                    }
                }
            }
        }
    }

    private fun setInitialData(stock: Stock) {
        chartViewModel.getCandles(stock.ticker)
        val currentPriceBuilder = StringBuilder("$")
        currentPriceBuilder.append(stock.price)
        val changeStockBuilder = StringBuilder(stock.difPrice.toString())
        changeStockBuilder.append(" (${stock.stock}%)")
        val btnTextBuilder = java.lang.StringBuilder("Buy for ")
        btnTextBuilder.append(currentPriceBuilder)
        with(binding) {
            price.text = currentPriceBuilder
            if (stock.difPrice < 0) {
                changeStock.setTextColor(Color.RED)
            }
            changeStock.text = changeStockBuilder
            buyBtn.text = btnTextBuilder
            graph.titleTextSize = resources.getDimension(R.dimen.graphTitle)
            graph.titleColor = ContextCompat.getColor(requireContext(), R.color.black)
            buyBtn.setOnClickListener {
                Toast.makeText(
                    context,
                    getString(R.string.successful_purchase),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}