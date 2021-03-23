package ru.punkoff.stocksapp.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ActivityMainBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.main.adapter.OnButtonClickListener
import ru.punkoff.stocksapp.ui.main.adapter.PopularSearchAdapter
import ru.punkoff.stocksapp.ui.main.adapter.RecentSearchAdapter
import ru.punkoff.stocksapp.ui.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.utils.hideKeyboard
import ru.punkoff.stocksapp.utils.onLeftDrawableClicked
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    private lateinit var mAboutDataListener: OnAboutDataReceivedListener
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModel<ActivityViewModel>()
    private val popularSearchAdapter = PopularSearchAdapter()
    private val recentSearchAdapter = RecentSearchAdapter()
    private lateinit var mSettings: SharedPreferences
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
        initSharedPreferences()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val pagerAdapter = MainPagerAdapter(this)
        setAdapter()
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                setEnabledView(false)
                mAboutDataListener.onDataLoading()
                mainViewModel.getRequest()
            }
            viewpager.adapter = pagerAdapter
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                tab.view.gravity = Gravity.START
                when (FragmentTypeEnum.values()[pagerAdapter.getItemViewType(position)]) {
                    FragmentTypeEnum.STOCKS -> tab.text = getString(R.string.stocks)
                    FragmentTypeEnum.FAVOURITE -> tab.text = getString(R.string.favourite)
                }
            }.attach()

            mainViewModel.observeViewState().observe(this@MainActivity) {
                when (it) {
                    StocksViewState.EMPTY -> Unit
                    is StocksViewState.Loading -> {
                        setEnabledView(false)
                        swipeRefreshLayout.isEnabled = false
                        loadingBar.visibility = View.VISIBLE
                        popularSearchLayout.visibility = View.GONE
                        mAboutDataListener.onDataLoading()
                    }
                    is StocksViewState.StockValue -> {
                        setEnabledView(true)
                        swipeRefreshLayout.isEnabled = true
                        swipeRefreshLayout.isRefreshing = false
                        loadingBar.visibility = View.GONE
                        viewpager.visibility = View.VISIBLE
                        mAboutDataListener.onDataReceived(it.stocks)
                    }
                    is StocksViewState.Error -> {
                        when (it.error) {
                            is UnknownHostException -> Toast.makeText(
                                this@MainActivity,
                                getString(R.string.check_internet),
                                Toast.LENGTH_LONG
                            ).show()
                            is SocketTimeoutException -> Toast.makeText(
                                this@MainActivity,
                                getString(R.string.timeout),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        setEnabledView(true)
                        swipeRefreshLayout.isEnabled = true
                        swipeRefreshLayout.isRefreshing = false
                        loadingBar.visibility = View.GONE
                        viewpager.visibility = View.VISIBLE
                        mAboutDataListener.onDataReceived(emptyList())
                    }
                }
            }
        }

        setOptionsForSearchView()
    }

    private fun initSharedPreferences() {
        mSettings = getSharedPreferences(Constant.APP_PREFERENCES, Context.MODE_PRIVATE)
        if (mSettings.contains(Constant.RECENT_SEARCH_LIST)) {
            val recent = mSettings.getStringSet(Constant.RECENT_SEARCH_LIST, emptySet())
            if (recent != null) {
                recentSearchAdapter.initialSetData(recent)
            }
        }
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
                            recentSearchAdapter.setData(textInputSearch.text.toString())
                            viewpager.currentItem = FragmentTypeEnum.STOCKS.ordinal
                        }
                        return true
                    }
                    return false
                }
            })
        }
    }

    private fun setAdapter() {
        popularSearchAdapter.attachListener(object : OnButtonClickListener {
            override fun onClick(name: String) {
                binding.textInputSearch.setText(name)
                binding.viewpager.currentItem = FragmentTypeEnum.STOCKS.ordinal
                recentSearchAdapter.setData(name)
                mainViewModel.getRequestBySymbol(name)
            }
        })
        recentSearchAdapter.attachListener(object : OnButtonClickListener {
            override fun onClick(name: String) {
                binding.textInputSearch.setText(name)
                binding.viewpager.currentItem = FragmentTypeEnum.STOCKS.ordinal
                mainViewModel.getRequestBySymbol(name)
            }
        })
        with(binding) {
            listPopularBtn.adapter = popularSearchAdapter
            listPopularBtn.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
            listRecentBtn.adapter = recentSearchAdapter
            listRecentBtn.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        }
    }

    fun setAboutDataListener(listener: OnAboutDataReceivedListener) {
        mAboutDataListener = listener
    }

    fun onDataChange(stock: Stock) {
        mAboutDataListener.onDataChange(stock)
    }

    override fun onStop() {
        super.onStop()
        mSettings.edit().apply {
            remove(Constant.RECENT_SEARCH_LIST).apply()
            putStringSet(Constant.RECENT_SEARCH_LIST, recentSearchAdapter.getData()).apply()
        }
    }
}