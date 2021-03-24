package ru.punkoff.stocksapp.ui.detail.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ActivityDetailBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.utils.Constant
import ru.punkoff.stocksapp.utils.FragmentTypeEnum

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val stock = intent.getSerializableExtra(Constant.EXTRA_STOCK) as Stock
        val pagerAdapter = DetailPagerAdapter(this)
        pagerAdapter.sendIntent(stock)
        with(binding) {
            ticker.text = stock.ticker
            name.text = stock.name
            homeBtn.setOnClickListener {
                finish()
            }
            viewpager.adapter = pagerAdapter
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                when (FragmentTypeEnum.values()[pagerAdapter.getItemViewType(position)]) {
                    FragmentTypeEnum.CHART -> tab.text = getString(R.string.chart)
                    FragmentTypeEnum.SUMMARY -> tab.text = getString(R.string.company_profile)
                    FragmentTypeEnum.NEWS -> tab.text = getString(R.string.news)
                    FragmentTypeEnum.CATS -> tab.text = getString(R.string.cats)
                }
            }.attach()
        }
    }
}