package ru.punkoff.stocksapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import ru.punkoff.stocksapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        with(binding) {
            viewpager.adapter = fragmentAdapter
            tabLayout.setupWithViewPager(viewpager)
        }
    }
}