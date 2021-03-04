package ru.punkoff.stocksapp.ui.stocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.databinding.StocksFragmentBinding
import ru.punkoff.stocksapp.ui.stocks.adapter.StocksAdapter

class StocksFragment : Fragment() {

    private val stocksViewModel by viewModel<StocksViewModel>()
    private val adapter = StocksAdapter()
    private var _binding: StocksFragmentBinding? = null
    private val binding: StocksFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StocksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.attachListener {
            Toast.makeText(context, it.ticket, Toast.LENGTH_SHORT).show()
        }
        with(binding) {
            listStocks.adapter = adapter
            listStocks.layoutManager = LinearLayoutManager(context)
        }

        stocksViewModel.observeViewState().observe(viewLifecycleOwner) {
            when (it) {
                is StocksViewState.Value -> adapter.submitList(it.stocks)
                StocksViewState.Loading -> Toast.makeText(context, "Loading", Toast.LENGTH_SHORT)
                    .show()
                StocksViewState.EMPTY -> Toast.makeText(context, "EMPTY", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}