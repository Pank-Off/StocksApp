package ru.punkoff.stocksapp.ui.main.fragments.favourite

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.FavouriteFragmentBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.detail.activity.DetailActivity
import ru.punkoff.stocksapp.ui.main.activity.MainActivity
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.ui.main.fragments.stocks.adapter.OnStarClickListener
import ru.punkoff.stocksapp.ui.main.fragments.stocks.adapter.StocksAdapter
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.utils.addRepeatingJob

class FavouriteFragment : Fragment() {

    private val favouriteViewModel by viewModel<FavouriteViewModel>()
    private val adapter = StocksAdapter()
    private var _binding: FavouriteFragmentBinding? = null
    private val binding: FavouriteFragmentBinding get() = _binding!!
    private lateinit var searchView: TextInputEditText
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var activity: MainActivity
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
        activity = getActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavouriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = requireActivity().findViewById(R.id.swipe_refresh_layout)
        initSearchView()
        attachListenerToAdapter()
        with(binding) {
            listStocks.adapter = adapter
            listStocks.layoutManager = LinearLayoutManager(context)

            viewLifecycleOwner.addRepeatingJob(Lifecycle.State.STARTED) {
                favouriteViewModel.stocksFlow.collect {
                    when (it) {
                        is StocksViewState.StockValue -> {
                            adapter.setData(it.stocks)
                            adapter.filter.filter(searchView.text)
                            loadingBar.visibility = View.INVISIBLE
                        }
                        is StocksViewState.Loading -> loadingBar.visibility = View.VISIBLE
                        is StocksViewState.EMPTY -> Unit
                    }
                }
            }
        }
    }

    private fun initSearchView() {
        searchView = requireActivity().findViewById(R.id.textInputSearch)
        searchView.addTextChangedListener(searchViewTextWatcher)
    }

    private fun attachListenerToAdapter() {
        adapter.attachListener { item, position ->
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(Constant.EXTRA_STOCK, item)
            startActivity(intent)
        }

        adapter.attachStarListener(object : OnStarClickListener {
            override fun deleteStock(stock: Stock) {
                activity.onDataChange(stock)
                stock.isFavourite = false
                favouriteViewModel.deleteFromDB(stock)
            }

            override fun saveStock(stock: Stock) {
                Log.d(javaClass.simpleName, "StockToInsert $stock")
                stock.isFavourite = true
                favouriteViewModel.saveToDB(stock)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayout.isEnabled = false
    }

    override fun onPause() {
        super.onPause()
        swipeRefreshLayout.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}