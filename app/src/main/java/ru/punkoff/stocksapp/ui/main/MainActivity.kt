package ru.punkoff.stocksapp.ui.main

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val pagerAdapter = MyPagerAdapter(this)
        with(binding) {
            viewpager.adapter = pagerAdapter
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                tab.view.gravity = Gravity.START
                when (FragmentTypeEnum.values()[pagerAdapter.getItemViewType(position)]) {
                    FragmentTypeEnum.STOCKS -> tab.text = getString(R.string.stocks)
                    FragmentTypeEnum.FAVOURITE -> tab.text = getString(R.string.favourite)
                }
            }.attach()
        }
    }
}