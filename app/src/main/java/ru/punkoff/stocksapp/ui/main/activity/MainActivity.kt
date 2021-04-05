package ru.punkoff.stocksapp.ui.main.activity

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
import ru.punkoff.stocksapp.ui.main.activity.adapter.OnButtonClickListener
import ru.punkoff.stocksapp.ui.main.activity.adapter.PopularSearchAdapter
import ru.punkoff.stocksapp.ui.main.activity.adapter.RecentSearchAdapter
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksViewState
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.utils.FragmentTypeEnum
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

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            with(binding) {
                viewpager.visibility = View.VISIBLE
                popularSearchLay.container.visibility = View.GONE
                if (s?.isEmpty() == true) {
                    tabLayout.visibility = View.VISIBLE
                    hideKeyboard(this@MainActivity)
                    textFieldSearch.setStartIconDrawable(android.R.drawable.ic_menu_search)
                    textFieldSearch.clearFocus()
                } else {
                    textFieldSearch.setStartIconDrawable(R.drawable.ic_arrow_arrows_back)
                    tabLayout.visibility = View.GONE
                }
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
        setOptionsForSearchView()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val pagerAdapter = MainPagerAdapter(this)
        setAdapter()
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                setEnabledView(false)
                mAboutDataListener.onDataLoading()
                mainViewModel.updatePrice()
            }
            viewpager.adapter = pagerAdapter
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                tab.view.gravity = Gravity.START
                when (FragmentTypeEnum.values()[pagerAdapter.getItemViewType(position)]) {
                    FragmentTypeEnum.STOCKS -> tab.text = getString(R.string.stocks)
                    FragmentTypeEnum.FAVOURITE -> tab.text = getString(R.string.favourite)
                }
            }.attach()
        }
        mainViewModel.observeViewState().observe(this@MainActivity) {
            when (it) {
                StocksViewState.EMPTY -> Unit
                is StocksViewState.Loading -> {
                    setEnabledView(false)
                    with(binding) {
                        textFieldSearch.setStartIconDrawable(R.drawable.ic_arrow_arrows_back)
                        tabLayout.visibility = View.GONE
                        swipeRefreshLayout.isEnabled = false
                        loadingBar.visibility = View.VISIBLE
                        popularSearchLay.container.visibility = View.GONE
                    }
                    mAboutDataListener.onDataLoading()
                }
                is StocksViewState.StockValue -> {
                    setEnabledView(true)
                    with(binding) {
                        swipeRefreshLayout.isEnabled = true
                        swipeRefreshLayout.isRefreshing = false
                        loadingBar.visibility = View.GONE
                        viewpager.visibility = View.VISIBLE
                    }
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
                    with(binding) {
                        swipeRefreshLayout.isEnabled = true
                        swipeRefreshLayout.isRefreshing = false
                        loadingBar.visibility = View.GONE
                        viewpager.visibility = View.VISIBLE
                    }
                    mAboutDataListener.onDataReceived(emptyList())
                }
            }
        }
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
            textInputSearch.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    textFieldSearch.setStartIconDrawable(android.R.drawable.ic_menu_search)
                    tabLayout.visibility = View.VISIBLE
                    viewpager.visibility = View.VISIBLE
                    popularSearchLay.container.visibility = View.GONE
                    swipeRefreshLayout.isEnabled = true
                } else {
                    textFieldSearch.setStartIconDrawable(R.drawable.ic_arrow_arrows_back)
                    tabLayout.visibility = View.GONE
                    viewpager.visibility = View.GONE
                    popularSearchLay.container.visibility = View.VISIBLE
                    swipeRefreshLayout.isEnabled = false
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
                with(binding) {
                    textInputSearch.setText(name)
                    viewpager.currentItem = FragmentTypeEnum.STOCKS.ordinal
                }
                recentSearchAdapter.setData(name)
                mainViewModel.getRequestBySymbol(name)
            }
        })
        recentSearchAdapter.attachListener(object : OnButtonClickListener {
            override fun onClick(name: String) {
                with(binding) {
                    textInputSearch.setText(name)
                    viewpager.currentItem = FragmentTypeEnum.STOCKS.ordinal
                }
                mainViewModel.getRequestBySymbol(name)
            }
        })
        with(binding) {
            popularSearchLay.listPopularBtn.adapter = popularSearchAdapter
            popularSearchLay.listPopularBtn.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
            popularSearchLay.listRecentBtn.adapter = recentSearchAdapter
            popularSearchLay.listRecentBtn.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        }
    }

    fun setAboutDataListener(listener: OnAboutDataReceivedListener) {
        mAboutDataListener = listener
    }

    fun onDataChange(stock: Stock) {
        mAboutDataListener.onDataChange(stock)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mainViewModel.observeViewState().observe(this) {
            when (it) {
                is StocksViewState.Loading -> binding.popularSearchLay.container.visibility =
                    View.GONE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mSettings.edit().apply {
            remove(Constant.RECENT_SEARCH_LIST).apply()
            putStringSet(Constant.RECENT_SEARCH_LIST, recentSearchAdapter.getData()).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.swipeRefreshLayout.isRefreshing) {
            setEnabledView(false)
            mAboutDataListener.onDataLoading()
        }
    }
}