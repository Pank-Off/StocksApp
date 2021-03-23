package ru.punkoff.stocksapp.ui.favourite

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.FavouriteFragmentBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.main.MainActivity
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import ru.punkoff.stocksapp.ui.stocks.adapter.OnStarClickListener
import ru.punkoff.stocksapp.ui.stocks.adapter.StocksAdapter

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
            favouriteViewModel.observeViewState().observe(viewLifecycleOwner) {
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

    private fun initSearchView() {
        searchView = requireActivity().findViewById(R.id.textInputSearch)
        searchView.addTextChangedListener(searchViewTextWatcher)
    }

    private fun attachListenerToAdapter() {
        adapter.attachListener { item, position ->
            Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
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
        favouriteViewModel.getStocksFromDB()
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