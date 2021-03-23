package ru.punkoff.stocksapp.ui.detail.fragments.news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.NewsFragmentBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.web.WebActivity
import ru.punkoff.stocksapp.ui.detail.fragments.news.adapter.NewsAdapter
import ru.punkoff.stocksapp.ui.detail.fragments.news.adapter.OnNewsClickListener
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant

class NewsFragment : Fragment() {
    private var _binding: NewsFragmentBinding? = null
    private val binding: NewsFragmentBinding get() = _binding!!

    private val newsViewModel by viewModel<NewsViewModel>()
    private val newsAdapter = NewsAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stock = arguments?.get(Constant.EXTRA_STOCK) as Stock
        if (savedInstanceState == null) {
            newsViewModel.getNews(stock.ticker)
        }
        setAdapter()
        newsViewModel.observeViewState().observe(viewLifecycleOwner) { result ->
            when (result) {
                is StocksViewState.CandleValue -> Unit
                StocksViewState.EMPTY -> {
                    with(binding) {
                        noDataTextView.visibility = View.VISIBLE
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
                is StocksViewState.NewsValue -> {
                    Log.d(javaClass.simpleName, "news: ${result.news}")
                    with(binding) {
                        loadingBar.visibility = View.INVISIBLE
                        retryBtn.visibility = View.INVISIBLE
                    }
                    newsAdapter.submitList(result.news)
                }
                is StocksViewState.StockValue -> Unit
            }
        }
    }

    private fun setAdapter() {
        with(binding) {
            listNews.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            listNews.adapter = newsAdapter
            val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider_drawable,
                null
            )?.let {
                dividerItemDecoration.setDrawable(
                    it
                )
            }
            listNews.addItemDecoration(dividerItemDecoration)
        }

        newsAdapter.attachListener(object : OnNewsClickListener {
            override fun onClick(url: String) {
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra(Constant.EXTRA_NEWS_URL, url)
                startActivity(intent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}