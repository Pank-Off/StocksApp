package ru.punkoff.stocksapp.ui.stocks

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.StocksFragmentBinding
import ru.punkoff.stocksapp.ui.stocks.adapter.StocksAdapter

class StocksFragment : Fragment() {

    private val stocksViewModel by viewModel<StocksViewModel>()
    private val adapter = StocksAdapter()
    private var _binding: StocksFragmentBinding? = null
    private val binding: StocksFragmentBinding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener
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
        setHasOptionsMenu(true)
        adapter.attachListener { item, position ->
            Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
        }

        adapter.attachSaveListener { stock ->
            Log.d(javaClass.simpleName, "StockToInsert $stock")
            stocksViewModel.saveToDB(stock)
        }

        with(binding) {
            listStocks.adapter = adapter
            listStocks.layoutManager = LinearLayoutManager(context)
            swipeRefreshLayout.setOnRefreshListener {
                stocksViewModel.getRequest()
                swipeRefreshLayout.isRefreshing = false
            }
            stocksViewModel.observeViewState().observe(viewLifecycleOwner) {
                when (it) {
                    is StocksViewState.Value -> {
                        Log.d(javaClass.simpleName, it.stocks.toString())
                        adapter.submitList(it.stocks)
                        adapter.notifyDataSetChanged()
                        try {
                            adapter.filter.filter(searchView.query)
                        } catch (exc: UninitializedPropertyAccessException) {
                            Log.e(javaClass.simpleName, exc.stackTraceToString())
                        }
                        loadingBar.visibility = View.INVISIBLE
                    }
                    StocksViewState.Loading -> {
                        loadingBar.visibility = View.VISIBLE
                    }
                    StocksViewState.EMPTY -> Unit
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.main, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i("onQueryTextSubmit", query)
                stocksViewModel.getStockBySymbol(query)
                Log.i("ItemCount()", adapter.itemCount.toString())
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.i("onQueryTextChange", newText)
                adapter.filter.filter(newText)
                Log.i("ItemCount()", adapter.itemCount.toString())
                return true
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {
            return false
        }
        searchView.setOnQueryTextListener(queryTextListener)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}