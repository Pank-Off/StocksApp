package ru.punkoff.stocksapp.ui.stocks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.punkoff.stocksapp.databinding.ItemStockBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.utils.GlideLoader

val DIFF_UTIL: DiffUtil.ItemCallback<Stock> = object : DiffUtil.ItemCallback<Stock>() {
    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return true
    }
}

class StocksAdapter : ListAdapter<Stock, StocksAdapter.StocksViewHolder>(DIFF_UTIL) {

    private lateinit var listener: OnStockClickListener

    fun attachListener(listener: OnStockClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {
        return StocksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StocksViewHolder(
        parent: ViewGroup,
        private val binding: ItemStockBinding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentStock: Stock
        private val clickListener: View.OnClickListener = View.OnClickListener {
            listener.onClick(currentStock)
        }

        fun bind(currentItem: Stock) {
            currentStock = currentItem
            with(binding) {
                ticket.text = currentItem.ticket
                name.text = currentItem.name
                price.text = currentItem.price
                stock.text = currentItem.stock
                GlideLoader.loadImage(imageView = logo, url = currentItem.logo)
                root.setOnClickListener(clickListener)
            }
        }
    }
}