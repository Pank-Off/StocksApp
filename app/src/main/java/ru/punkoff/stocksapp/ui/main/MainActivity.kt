package ru.punkoff.stocksapp.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ActivityMainBinding
import ru.punkoff.stocksapp.ui.main.adapter.OnButtonClickListener
import ru.punkoff.stocksapp.ui.main.adapter.PopularSearchAdapter
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.hideKeyboard
import ru.punkoff.stocksapp.utils.onLeftDrawableClicked

class MainActivity : AppCompatActivity() {

    private lateinit var mAboutDataListener: OnAboutDataReceivedListener
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModel<ActivityViewModel>()
    val adapter = PopularSearchAdapter()
    private val searchViewTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            with(binding) {
                viewpager.visibility = View.VISIBLE
                popularSearchLayout.visibility = View.GONE
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val pagerAdapter = MyPagerAdapter(this)
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                mainViewModel.getRequest()
                setEnabledView(false)
                mAboutDataListener.onDataLoading()
            }
            viewpager.adapter = pagerAdapter
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                tab.view.gravity = Gravity.START
                when (FragmentTypeEnum.values()[pagerAdapter.getItemViewType(position)]) {
                    FragmentTypeEnum.STOCKS -> tab.text = getString(R.string.stocks)
                    FragmentTypeEnum.FAVOURITE -> tab.text = getString(R.string.favourite)
                }
            }.attach()
            textFieldSearch.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    textFieldSearch.setStartIconDrawable(android.R.drawable.ic_menu_search)
                    tabLayout.visibility = View.VISIBLE
                    viewpager.visibility = View.VISIBLE
                    popularSearchLayout.visibility = View.GONE
                } else {
                    textFieldSearch.setStartIconDrawable(R.drawable.ic_arrow_arrows_back)
                    tabLayout.visibility = View.GONE
                    viewpager.visibility = View.GONE
                    popularSearchLayout.visibility = View.VISIBLE
                }
            }

            textInputSearch.onLeftDrawableClicked {
                hideKeyboard(this@MainActivity)
                textInputSearch.clearFocus()
                textInputSearch.text?.clear()
            }
            setAdapter()
            mainViewModel.observeViewState().observe(this@MainActivity) {
                when (it) {
                    StocksViewState.EMPTY -> Unit
                    StocksViewState.Loading -> {
                        setEnabledView(false)
                        swipeRefreshLayout.isEnabled = false
                        loadingBar.visibility = View.VISIBLE
                        popularSearchLayout.visibility = View.GONE
                    }
                    is StocksViewState.Value -> {
                        swipeRefreshLayout.isRefreshing = false
                        swipeRefreshLayout.isEnabled = true
                        setEnabledView(true)
                        loadingBar.visibility = View.GONE
                        viewpager.visibility = View.VISIBLE
                        mAboutDataListener.onDataReceived(it.stocks)
                    }
                }
            }
        }

        setOptionsForSearchView()
    }

    private fun setEnabledView(isEnabled: Boolean) {
        with(binding) {
            textFieldSearch.isEnabled = isEnabled
            viewpager.isUserInputEnabled = isEnabled
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
    }

    private fun setOptionsForSearchView() {
        with(binding) {
            textInputSearch.addTextChangedListener(searchViewTextWatcher)
            textInputSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    view: TextView?,
                    actionId: Int,
                    keyEvent: KeyEvent?
                ): Boolean {
                    if (keyEvent != null) {
                        if (!keyEvent.isShiftPressed && textInputSearch.text != null && textInputSearch.text.toString() != "") {
                            mainViewModel.getRequestBySymbol(textInputSearch.text.toString())
                        }
                        return true
                    }
                    return false
                }
            })
        }
    }

    private fun setAdapter() {
        adapter.attachListener(object : OnButtonClickListener {
            override fun onClick(name: String) {
                with(binding) {
                    textInputSearch.setText(name)
                }
                mainViewModel.getRequestBySymbol(name)
            }
        })
        val listPopular = findViewById<RecyclerView>(R.id.list_popular_btn)
        listPopular.adapter = adapter
        listPopular.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)

        val listRecent = findViewById<RecyclerView>(R.id.list_recent_btn)
        listRecent.adapter = adapter
        listRecent.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
    }

    fun setAboutDataListener(listener: OnAboutDataReceivedListener) {
        mAboutDataListener = listener
    }
}