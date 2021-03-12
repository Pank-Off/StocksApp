package ru.punkoff.stocksapp.ui.stocks

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.StocksFragmentBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.stocks.adapter.OnStarClickListener
import ru.punkoff.stocksapp.ui.stocks.adapter.StocksAdapter


class StocksFragment : Fragment() {

    private val stocksViewModel by viewModel<StocksViewModel>()
    private val adapter = StocksAdapter()
    private var _binding: StocksFragmentBinding? = null
    private val binding: StocksFragmentBinding get() = _binding!!

    private lateinit var searchView: TextInputEditText
    private lateinit var searchViewLayout: TextInputLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private val searchViewTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            adapter.filter.filter(s)
        }

        override fun afterTextChanged(p0: Editable?) {
        }
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
        tabLayout = requireActivity().findViewById(R.id.tab_layout)
        setSearchViewOptions()
        attachListenerToAdapter()
        with(binding) {
            listStocks.adapter = adapter
            listStocks.layoutManager = LinearLayoutManager(context)
            swipeRefreshLayout.setOnRefreshListener {
                stocksViewModel.getRequest()
                setEnabledView(false)
            }
            stocksViewModel.observeViewState().observe(viewLifecycleOwner) {
                when (it) {
                    is StocksViewState.Value -> {
                        Log.d(javaClass.simpleName, it.stocks.toString())
                        adapter.setData(it.stocks)
                        adapter.filter.filter(searchView.text)
                        swipeRefreshLayout.isEnabled = true
                        swipeRefreshLayout.isRefreshing = false
                        loadingBar.visibility = View.INVISIBLE
                        setEnabledView(true)
                    }
                    StocksViewState.Loading -> {
                        loadingBar.visibility = View.VISIBLE
                        swipeRefreshLayout.isEnabled = false
                        setEnabledView(false)
                    }
                    StocksViewState.EMPTY -> Unit
                }
            }
        }
    }

    private fun setEnabledView(isEnabled: Boolean) {
        searchViewLayout.isEnabled = isEnabled
        viewPager.isUserInputEnabled = isEnabled
        adapter.setEnabled(isEnabled)
        val tabStrip = tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener { view, _ ->
                if (false) {
                    view?.performClick()
                }
                !isEnabled
            }
        }
    }

    private fun setSearchViewOptions() {
        searchView = requireActivity().findViewById(R.id.textInputSearch)
        searchViewLayout = requireActivity().findViewById(R.id.textFieldSearch)
        viewPager = requireActivity().findViewById(R.id.viewpager)
        searchView.addTextChangedListener(searchViewTextWatcher)
        searchView.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(
                view: TextView?,
                actionId: Int,
                keyEvent: KeyEvent?
            ): Boolean {
                if (keyEvent != null) {
                    if (!keyEvent.isShiftPressed && searchView.text != null && searchView.text.toString() != "") {
                        stocksViewModel.getStockBySymbol(searchView.text.toString())
                    }
                    return true
                }
                return false
            }
        })
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

    override fun onStop() {
        super.onStop()
        stocksViewModel.saveCache()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}