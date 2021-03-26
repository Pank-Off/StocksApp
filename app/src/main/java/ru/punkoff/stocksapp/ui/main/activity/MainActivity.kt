package ru.punkoff.stocksapp.ui.main.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.punkoff.stocksapp.R
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewpager: ViewPager2
    private lateinit var container: ConstraintLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var textInputSearch: TextInputEditText
    private lateinit var textFieldSearch: TextInputLayout
    private lateinit var listPopularBtn: RecyclerView
    private lateinit var listRecentBtn: RecyclerView
    private lateinit var loadingBar: ProgressBar

    // private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModel<ActivityViewModel>()
    private val popularSearchAdapter = PopularSearchAdapter()
    private val recentSearchAdapter = RecentSearchAdapter()
    private lateinit var mSettings: SharedPreferences
    private val searchViewTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewpager.visibility = View.VISIBLE
            container.visibility = View.GONE
            if (s?.isEmpty() == true) {
                hideKeyboard(this@MainActivity)
                textFieldSearch.setStartIconDrawable(android.R.drawable.ic_menu_search)
                textFieldSearch.clearFocus()
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(R.layout.activity_main)
        initViews()
        initSharedPreferences()

        setOptionsForSearchView()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val pagerAdapter = MainPagerAdapter(this)
        setAdapter()
        //  with(binding) {
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
                    container.visibility = View.GONE
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

    private fun initViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        viewpager = findViewById(R.id.viewpager)
        container = findViewById(R.id.container)
        tabLayout = findViewById(R.id.tab_layout)
        textInputSearch = findViewById(R.id.textInputSearch)
        textFieldSearch = findViewById(R.id.textFieldSearch)
        listPopularBtn = findViewById(R.id.list_popular_btn)
        listRecentBtn = findViewById(R.id.list_recent_btn)
        loadingBar = findViewById(R.id.loading_bar)

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
        //   with(binding) {
        textFieldSearch.isEnabled = isEnabled
        textFieldSearch.setStartIconDrawable(R.drawable.ic_arrow_arrows_back)
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
        // }
    }

    private fun setOptionsForSearchView() {
        textInputSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                Toast.makeText(this, "Focus - NO", Toast.LENGTH_SHORT).show()
                textFieldSearch.setStartIconDrawable(android.R.drawable.ic_menu_search)
                tabLayout.visibility = View.VISIBLE
                viewpager.visibility = View.VISIBLE
                container.visibility = View.GONE
            } else {
                Toast.makeText(this, "Focus YES", Toast.LENGTH_SHORT).show()
                textFieldSearch.setStartIconDrawable(R.drawable.ic_arrow_arrows_back)
                tabLayout.visibility = View.GONE
                viewpager.visibility = View.GONE
                container.visibility = View.VISIBLE
            }
        }
        //   with(binding) {
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

    private fun setAdapter() {
        popularSearchAdapter.attachListener(object : OnButtonClickListener {
            override fun onClick(name: String) {
                textInputSearch.setText(name)
                viewpager.currentItem = FragmentTypeEnum.STOCKS.ordinal
                recentSearchAdapter.setData(name)
                mainViewModel.getRequestBySymbol(name)
            }
        })
        recentSearchAdapter.attachListener(object : OnButtonClickListener {
            override fun onClick(name: String) {
                textInputSearch.setText(name)
                viewpager.currentItem = FragmentTypeEnum.STOCKS.ordinal
                mainViewModel.getRequestBySymbol(name)
            }
        })
        // with(binding) {
        listPopularBtn.adapter = popularSearchAdapter
        listPopularBtn.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        listRecentBtn.adapter = recentSearchAdapter
        listRecentBtn.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        //  }
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