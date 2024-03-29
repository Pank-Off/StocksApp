package ru.punkoff.stocksapp.ui.main.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.punkoff.stocksapp.ui.main.fragments.favourite.FavouriteFragment
import ru.punkoff.stocksapp.ui.main.fragments.stocks.StocksFragment
import ru.punkoff.stocksapp.utils.FragmentTypeEnum

class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (FragmentTypeEnum.values()[getItemViewType(position)]) {
            FragmentTypeEnum.STOCKS -> StocksFragment()
            FragmentTypeEnum.FAVOURITE -> FavouriteFragment()
            else -> Fragment()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            FragmentTypeEnum.STOCKS.ordinal
        } else {
            FragmentTypeEnum.FAVOURITE.ordinal
        }
    }

    override fun getItemCount(): Int = CARD_ITEM_SIZE

    companion object {
        private const val CARD_ITEM_SIZE = 2
    }
}