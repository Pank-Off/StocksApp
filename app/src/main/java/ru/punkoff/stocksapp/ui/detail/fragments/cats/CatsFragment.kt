package ru.punkoff.stocksapp.ui.detail.fragments.cats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.databinding.CatsFragmentBinding
import ru.punkoff.stocksapp.utils.PicassoLoader

class CatsFragment : Fragment() {
    private var _binding: CatsFragmentBinding? = null
    private val binding: CatsFragmentBinding get() = _binding!!
    private val catsViewModel by viewModel<CatsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            catsViewModel.getCat()
        }

        binding.anotherBtn.setOnClickListener {
            catsViewModel.getCat()
        }

        catsViewModel.observeViewState().observe(viewLifecycleOwner) {
            when (it) {
                is CatsViewState.CatValue -> {
                    Log.d(javaClass.simpleName, it.cats[0].url)
                    PicassoLoader.loadImage(it.cats[0].url, binding.catsImage)
                    binding.loadingBar.visibility = View.GONE
                }
                CatsViewState.EMPTY -> Unit
                CatsViewState.Loading -> binding.loadingBar.visibility = View.VISIBLE
            }
        }
    }
}