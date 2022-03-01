package ru.punkoff.stocksapp.ui.detail.fragments.summary

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.databinding.SummaryFragmentBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.ui.web.WebActivity
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.utils.FragmentArgumentDelegate
import ru.punkoff.stocksapp.utils.PicassoLoader

class SummaryFragment : Fragment() {
    private var _binding: SummaryFragmentBinding? = null
    private val binding: SummaryFragmentBinding get() = _binding!!

    private val stock by FragmentArgumentDelegate<Stock>()
    private val summaryViewModel by viewModel<SummaryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SummaryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialData(stock)
        if (savedInstanceState == null) {
            summaryViewModel.getProfile(stock.ticker)
        }
        summaryViewModel.observeViewState().observe(viewLifecycleOwner) { result ->
            when (result) {
                StocksViewState.EMPTY -> {
                    with(binding) {
                        noDataTextView.visibility = View.VISIBLE
                        loadingBar.visibility = View.INVISIBLE
                        retryBtn.visibility = View.INVISIBLE
                    }
                }
                StocksViewState.Loading -> {
                    with(binding) {
                        loadingBar.visibility = View.VISIBLE
                        retryBtn.visibility = View.INVISIBLE
                    }
                }
                is StocksViewState.SummaryValue -> {
                    with(binding) {
                        description.text = result.summary.description
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
            }
        }

        with(binding) {
            retryBtn.setOnClickListener {
                summaryViewModel.getProfile(symbol = stock.ticker)
                retryBtn.visibility = View.GONE
                loadingBar.visibility = View.VISIBLE
            }
        }
    }

    private fun setInitialData(stock: Stock) {
        val currentPrice = StringBuilder("$")
        currentPrice.append(stock.price)
        val changeStock = StringBuilder(stock.difPrice.toString())
        changeStock.append(" (${stock.stock}%)")

        with(binding) {
            item.ticker.text = stock.ticker
            item.name.text = stock.name
            item.price.text = currentPrice
            item.stock.text = changeStock
            item.favorite.visibility = View.GONE
            webUrl.text = stock.webUrl
            if (stock.isFavourite) {
                item.favorite.background =
                    context?.let { ContextCompat.getDrawable(it, android.R.drawable.star_on) }
            } else {
                item.favorite.background =
                    context?.let { ContextCompat.getDrawable(it, android.R.drawable.star_off) }
            }
            if (stock.stock < 0) {
                item.stock.setTextColor(Color.RED)
            }
            PicassoLoader.loadImage(stock.logo, item.logo)

            webUrl.setOnClickListener {
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra(Constant.EXTRA_NEWS_URL, stock.webUrl)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}