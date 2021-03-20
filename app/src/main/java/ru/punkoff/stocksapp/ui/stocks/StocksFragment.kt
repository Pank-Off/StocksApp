package ru.punkoff.stocksapp.ui.stocks

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.StocksFragmentBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.main.MainActivity
import ru.punkoff.stocksapp.ui.main.OnAboutDataReceivedListener
import ru.punkoff.stocksapp.ui.stocks.adapter.OnStarClickListener
import ru.punkoff.stocksapp.ui.stocks.adapter.StocksAdapter
import ru.punkoff.stocksapp.utils.Constant


class StocksFragment : Fragment(), OnAboutDataReceivedListener {

    private val stocksViewModel by viewModel<StocksViewModel>()
    private val adapter = StocksAdapter()
    private var _binding: StocksFragmentBinding? = null
    private val binding: StocksFragmentBinding get() = _binding!!

    private lateinit var searchView: TextInputEditText
    private val searchViewTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            adapter.filter.filter(s)
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mActivity = activity as MainActivity
        mActivity.setAboutDataListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StocksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchViewOptions()
        attachListenerToAdapter()
        with(binding) {
            listStocks.setHasFixedSize(true)
            listStocks.layoutManager = LinearLayoutManager(context)
            setupScrollListener()

            initAdapter()

            stocksViewModel.observeViewState().observe(viewLifecycleOwner) {
                when (it) {
                    is StocksViewState.Value -> {
                        Log.d(javaClass.simpleName, it.stocks.toString())
                        adapter.setData(it.stocks)
                        adapter.filter.filter(searchView.text)
                        loadingBar.visibility = View.INVISIBLE
                    }
                    StocksViewState.Loading -> {
                        loadingBar.visibility = View.VISIBLE
                    }
                    StocksViewState.EMPTY -> Unit
                }
            }

            retryBtn.setOnClickListener {
                retryBtn.visibility = View.GONE
                paginationLoadingBar.visibility = View.VISIBLE
                stocksViewModel.searchStocks(Constant.EXCHANGE)
            }
        }
    }

    private fun setupScrollListener() {
        val layoutManager = binding.listStocks.layoutManager as LinearLayoutManager
        binding.listStocks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                stocksViewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }

    private fun initAdapter() {
        binding.listStocks.adapter = adapter
        stocksViewModel.stocksPaginationLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is PaginationViewStateResult.Error -> {
                    Toast.makeText(
                        context,
                        "\uD83D\uDE28 Wooops $result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.paginationLoadingBar.visibility = View.GONE
                    binding.retryBtn.visibility = View.VISIBLE
                }
                is PaginationViewStateResult.Success -> {
                    Log.d(javaClass.simpleName, "SUCCESS: ${result.stocks}")
                    adapter.setData(result.stocks)
                    adapter.filter.filter(searchView.text)
                    binding.listStocks.visibility = View.VISIBLE
                    binding.loadingBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setSearchViewOptions() {
        searchView = requireActivity().findViewById(R.id.textInputSearch)
        searchView.addTextChangedListener(searchViewTextWatcher)

        searchView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.paginationLoadingBar.visibility = View.GONE
            } else {
                binding.paginationLoadingBar.visibility = View.VISIBLE
            }
        }
    }

    private fun attachListenerToAdapter() {
        adapter.attachListener { item, position ->
            Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
        }

        adapter.attachStarListener(object : OnStarClickListener {
            override fun deleteStock(stock: Stock) {
                stock.isFavourite = false
                stocksViewModel.deleteFromDB(stock)
            }

            override fun saveStock(stock: Stock) {
                Log.d(javaClass.simpleName, "StockToInsert $stock")
                stock.isFavourite = true
                stocksViewModel.saveToDB(stock)
            }
        })
    }

    override fun onDataReceived(stocks: List<Stock>) {
        adapter.setEnabled(true)
        if (stocks.isNotEmpty()) {
            val stockList = mutableListOf<Stock>()
            stockList.addAll(stocks)
            stocksViewModel.setViewState(StocksViewState.Value(stockList))
        }
    }

    override fun onDataLoading() {
        adapter.setEnabled(false)
    }

    override fun onStop() {
        super.onStop()
        stocksViewModel.saveCache(adapter.getData())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}