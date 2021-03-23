package ru.punkoff.stocksapp.ui.detail.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.ui.detail.fragments.cats.CatsFragment
import ru.punkoff.stocksapp.ui.detail.fragments.chart.ChartFragment
import ru.punkoff.stocksapp.ui.detail.fragments.news.NewsFragment
import ru.punkoff.stocksapp.utils.FragmentTypeEnum
import ru.punkoff.stocksapp.utils.Constant

class DetailPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private lateinit var intent: Stock
    fun sendIntent(stock: Stock) {
        intent = stock
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putSerializable(Constant.EXTRA_STOCK, intent)
        return when (FragmentTypeEnum.values()[getItemViewType(position)]) {
            FragmentTypeEnum.CHART -> {
                val chartFragment = ChartFragment()
                chartFragment.arguments = bundle
                chartFragment
            }
            FragmentTypeEnum.NEWS -> {
                val newsFragment = NewsFragment()
                newsFragment.arguments = bundle
                newsFragment
            }
            else -> CatsFragment()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> FragmentTypeEnum.CHART.ordinal
            1 -> FragmentTypeEnum.NEWS.ordinal
            else -> FragmentTypeEnum.CATS.ordinal
        }
    }

    override fun getItemCount(): Int = CARD_ITEM_SIZE

    companion object {
        private const val CARD_ITEM_SIZE = 3
    }
}