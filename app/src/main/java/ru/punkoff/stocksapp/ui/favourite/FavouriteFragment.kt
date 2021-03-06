package ru.punkoff.stocksapp.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.databinding.FavouriteFragmentBinding
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import ru.punkoff.stocksapp.ui.stocks.adapter.StocksAdapter

class FavouriteFragment : Fragment() {

    private val favouriteViewModel by viewModel<FavouriteViewModel>()
    private val adapter = StocksAdapter()
    private var _binding: FavouriteFragmentBinding? = null
    private val binding: FavouriteFragmentBinding get() = _binding!!
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
        adapter.attachListener { item, position ->
            Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
        }

        with(binding) {
            listStocks.adapter = adapter
            listStocks.layoutManager = LinearLayoutManager(context)
            favouriteViewModel.observeViewState().observe(viewLifecycleOwner) {
                when (it) {
                    is StocksViewState.Value -> {
                        adapter.submitList(it.stocks)
                        loadingBar.visibility = View.INVISIBLE
                    }
                    StocksViewState.Loading -> loadingBar.visibility = View.VISIBLE
                    StocksViewState.EMPTY -> Toast.makeText(context, "EMPTY", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        favouriteViewModel.getStocksFromDB()
    }
}