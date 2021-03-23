package ru.punkoff.stocksapp.ui.detail.fragments.cats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.punkoff.stocksapp.databinding.CatsFragmentBinding

class CatsFragment : Fragment() {
    private var _binding: CatsFragmentBinding? = null
    private val binding: CatsFragmentBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}