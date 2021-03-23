package ru.punkoff.stocksapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.punkoff.stocksapp.databinding.ChartFragmentBinding
import ru.punkoff.stocksapp.databinding.FavouriteFragmentBinding
import ru.punkoff.stocksapp.databinding.NewsFragmentBinding

class NewsFragment : Fragment() {
    private var _binding: NewsFragmentBinding? = null
    private val binding: NewsFragmentBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}