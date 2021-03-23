package ru.punkoff.stocksapp.ui.web

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import ru.punkoff.stocksapp.databinding.ActivityWebBinding
import ru.punkoff.stocksapp.utils.Constant

class WebActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        with(binding) {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = MyWebViewClient()
            intent.getStringExtra(Constant.EXTRA_NEWS_URL)?.let { webView.loadUrl(it) }
        }
    }
}