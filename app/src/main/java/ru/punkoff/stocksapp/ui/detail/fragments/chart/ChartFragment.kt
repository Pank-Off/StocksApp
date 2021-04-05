package ru.punkoff.stocksapp.ui.detail.fragments.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ChartFragmentBinding
import ru.punkoff.stocksapp.model.Candle
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.utils.Period
import ru.punkoff.stocksapp.utils.activateButton
import java.util.*
import kotlin.math.floor

class ChartFragment : Fragment() {
    private val chartViewModel by viewModel<ChartViewModel>()
    private var _binding: ChartFragmentBinding? = null
    private val binding: ChartFragmentBinding get() = _binding!!

    private lateinit var saveStatePeriod: Period
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
        chartViewModel.startSocket(symbol = stock.ticker)
        if (savedInstanceState == null) {
            with(binding) {
                graph.gridLabelRenderer.numHorizontalLabels = 7
                weekBtn.backgroundTintList =
                    ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                weekBtn.setTextColor(Color.WHITE)
                saveStatePeriod = Period.WEEK
            }
            val currentTime = System.currentTimeMillis()
            chartViewModel.getCandles(
                symbol =
                stock.ticker,
                from = (currentTime - Constant.UNIX_WEEK_TIME) / 1000,
                to = currentTime / 1000,
                resolution = Constant.RESOLUTION_W
            )
        } else {
            with(binding) {
                when (savedInstanceState.get(EXTRA_PERIOD)) {
                    Period.DAY -> {
                        dayBtn.backgroundTintList =
                            ContextCompat.getColorStateList(dayBtn.context, R.color.black)
                        dayBtn.setTextColor(Color.WHITE)
                        saveStatePeriod = Period.DAY
                    }
                    Period.WEEK -> {
                        weekBtn.backgroundTintList =
                            ContextCompat.getColorStateList(weekBtn.context, R.color.black)
                        weekBtn.setTextColor(Color.WHITE)
                        saveStatePeriod = Period.WEEK
                    }
                    Period.MONTH -> {
                        monthBtn.backgroundTintList =
                            ContextCompat.getColorStateList(monthBtn.context, R.color.black)
                        monthBtn.setTextColor(Color.WHITE)
                        saveStatePeriod = Period.MONTH
                    }
                    Period.YEAR -> {
                        yearBtn.backgroundTintList =
                            ContextCompat.getColorStateList(yearBtn.context, R.color.black)
                        yearBtn.setTextColor(Color.WHITE)
                        saveStatePeriod = Period.YEAR
                    }
                }
            }
        }

        setListenerOnBtns(stock.ticker)
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
                    Log.d(javaClass.simpleName, result.toString())
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
                        graph.visibility = View.INVISIBLE
                        noDataTextView.visibility = View.INVISIBLE
                        loadingBar.visibility = View.VISIBLE
                        retryBtn.visibility = View.INVISIBLE
                    }
                }
                is StocksViewState.StockValue -> Unit
            }
        }

        chartViewModel.observeMessage().observe(viewLifecycleOwner) { socketData ->
            Log.e(javaClass.simpleName, "SocketData: $socketData")
            socketData.data?.let { trades ->
                if (stock.ticker == trades[0].symbol) {
                    val previousPrice = binding.price.text.toString()
                    val trade = trades[0].price.toString()
                    val diffPrice = trade.toDouble() - previousPrice.substringAfter('$').toDouble()
                    val percent = (diffPrice) / trade.toDouble() * 100
                    val currentPriceBuilder = StringBuilder("$")
                    currentPriceBuilder.append(trade)
                    val btnTextBuilder = StringBuilder("Buy for $")
                    btnTextBuilder.append(trade)
                    val changeStockBuilder =
                        StringBuilder((floor(diffPrice * 100) / 100).toString())
                    changeStockBuilder.append(" (${floor(percent * 100) / 100}%)")
                    with(binding) {
                        price.text = currentPriceBuilder
                        if (diffPrice < 0) {
                            changeStock.setTextColor(Color.RED)
                        } else {
                            changeStock.setTextColor(
                                ContextCompat.getColor(
                                    changeStock.context,
                                    R.color.green
                                )
                            )
                        }
                        changeStock.text = changeStockBuilder
                        buyBtn.text = btnTextBuilder
                    }
                }
            }
        }

        with(binding) {
            retryBtn.setOnClickListener {
                val currentTime = System.currentTimeMillis() / 1000
                chartViewModel.getCandles(
                    symbol =
                    stock.ticker, from = currentTime - Constant.UNIX_MONTH_TIME, to = currentTime
                )
                retryBtn.visibility = View.GONE
                loadingBar.visibility = View.VISIBLE
                chartViewModel.closeSocket()
                chartViewModel.startSocket(stock.ticker)
            }
        }
    }

    private fun setListenerOnBtns(ticker: String) {
        with(binding) {
            dayBtn.setOnClickListener {
                graph.gridLabelRenderer.numHorizontalLabels = 8
                val currentTime = System.currentTimeMillis()
                chartViewModel.getCandles(
                    symbol =
                    ticker,
                    from = (currentTime - Constant.UNIX_DAY_TIME) / 1000,
                    to = currentTime / 1000,
                    resolution = Constant.RESOLUTION_D
                )
                dayBtn.activateButton(binding)
                saveStatePeriod = Period.DAY
            }

            weekBtn.setOnClickListener {
                graph.gridLabelRenderer.numHorizontalLabels = 7
                val currentTime = System.currentTimeMillis()
                chartViewModel.getCandles(
                    symbol =
                    ticker,
                    from = (currentTime - Constant.UNIX_WEEK_TIME) / 1000,
                    to = currentTime / 1000,
                    resolution = Constant.RESOLUTION_W
                )
                weekBtn.activateButton(binding)
                saveStatePeriod = Period.WEEK
            }

            monthBtn.setOnClickListener {
                graph.gridLabelRenderer.numHorizontalLabels = 10
                val currentTime = System.currentTimeMillis()
                chartViewModel.getCandles(
                    symbol =
                    ticker,
                    from = (currentTime - Constant.UNIX_MONTH_TIME) / 1000,
                    to = currentTime / 1000,
                    resolution = Constant.RESOLUTION_M
                )
                monthBtn.activateButton(binding)
                saveStatePeriod = Period.MONTH
            }
            yearBtn.setOnClickListener {
                graph.gridLabelRenderer.numHorizontalLabels = 12
                val currentTime = System.currentTimeMillis()
                chartViewModel.getCandles(
                    symbol =
                    ticker,
                    from = (currentTime - Constant.UNIT_YEAR_TIME) / 1000,
                    to = currentTime / 1000,
                    resolution = Constant.RESOLUTION_Y
                )
                yearBtn.activateButton(binding)
                saveStatePeriod = Period.YEAR
            }
        }
    }

    private fun plotGraph(candle: Candle) {
        val listOfCandles = mutableListOf<DataPoint>()
        Log.d(javaClass.simpleName, "Plot $candle")
        for ((index, element) in candle.openPrice.withIndex()) {
            val time = Date(candle.time[index] * 1000)
            listOfCandles.add(DataPoint(time, element))
        }
        val series = LineGraphSeries(
            listOfCandles.toTypedArray()
        )
        with(binding) {
            series.color = ContextCompat.getColor(requireContext(), R.color.black)
            graph.removeAllSeries()
            graph.addSeries(series)
            graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        }
    }

    private fun setInitialData(stock: Stock) {
        val currentPriceBuilder = StringBuilder("$")
        currentPriceBuilder.append(stock.price)
        val changeStockBuilder = StringBuilder(stock.difPrice.toString())
        changeStockBuilder.append(" (${stock.stock}%)")
        val btnTextBuilder = StringBuilder("Buy for ")
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


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_PERIOD, saveStatePeriod)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        chartViewModel.closeSocket()
    }

    companion object {
        const val EXTRA_PERIOD = "EXTRA_PERIOD"
    }
}