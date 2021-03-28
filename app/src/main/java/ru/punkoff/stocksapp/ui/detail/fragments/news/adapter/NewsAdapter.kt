package ru.punkoff.stocksapp.ui.detail.fragments.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.punkoff.stocksapp.databinding.ItemNewsBinding
import ru.punkoff.stocksapp.model.News
import ru.punkoff.stocksapp.utils.PicassoLoader
import java.text.SimpleDateFormat
import java.util.*

val NEWS_COMPARATOR = object : DiffUtil.ItemCallback<News>() {
    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
        oldItem == newItem
}

class NewsAdapter : ListAdapter<News, NewsAdapter.NewsViewHolder>(NEWS_COMPARATOR) {

    private val dayFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private lateinit var onNewsClickListener: OnNewsClickListener
    fun attachListener(listener: OnNewsClickListener) {
        onNewsClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(
        parent: ViewGroup,
        private val binding: ItemNewsBinding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentNews: News
        private val newsClickListener: View.OnClickListener = View.OnClickListener {
            onNewsClickListener.onClick(currentNews.url)
        }

        fun bind(news: News) {
            currentNews = news
            val date = dayFormat.format(currentNews.datetime * 1000)
            with(binding) {
                headerTitle.text = news.headline
                summary.text = news.summary
                data.text = date
                PicassoLoader.loadImage(news.image, imageNews)
                root.setOnClickListener(newsClickListener)
            }
        }
    }
}