package ru.punkoff.stocksapp.ui.stocks.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ItemStockBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.utils.GlideLoader
import java.util.*
import kotlin.collections.ArrayList

val DIFF_UTIL: DiffUtil.ItemCallback<Stock> = object : DiffUtil.ItemCallback<Stock>() {
    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return true
    }
}

class StocksAdapter : ListAdapter<Stock, StocksAdapter.StocksViewHolder>(DIFF_UTIL), Filterable {

    private var firstStart = true
    private lateinit var listener: OnStockClickListener
    private lateinit var stockListFiltered: List<Stock>
    fun attachListener(listener: OnStockClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {
        return StocksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        holder.bind(stockListFiltered[position], position)
    }

    override fun getItemCount(): Int {
        stockListFiltered = if (firstStart) {
            currentList
        } else
            stockListFiltered.filter { currentList.contains(it) }
        return stockListFiltered.size
    }

    inner class StocksViewHolder(
        parent: ViewGroup,
        private val binding: ItemStockBinding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentStock: Stock
        private var currentPosition: Int = 0
        private val clickListener: View.OnClickListener = View.OnClickListener {
            listener.onClick(currentStock, currentPosition)
        }

        fun bind(currentItem: Stock, position: Int) {
            currentPosition = position
            currentStock = currentItem
            with(binding) {
                ticket.text = currentItem.ticket
                name.text = currentItem.name
                price.text = currentItem.price.toString()
                stock.text = currentItem.stock.toString()
                if (currentItem.stock < 0) {
                    stock.setTextColor(Color.RED)
                }
                if (position % 2 == 0) {
                    root.backgroundTintList =
                        root.resources.getColorStateList(R.color.white, null)
                }
                GlideLoader.loadImage(imageView = logo, url = currentItem.logo)
                root.setOnClickListener(clickListener)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString: String = constraint.toString()
                Log.d(javaClass.simpleName + " charString", charString)
                Log.d(javaClass.simpleName, currentList.toString())
                stockListFiltered = if (charString.isEmpty()) {
                    currentList
                } else {
                    val filteredList: ArrayList<Stock> = ArrayList()
                    for (stock in currentList) {
                        if (stock.ticket.toLowerCase(Locale.ROOT)
                                .contains(charString.toLowerCase(Locale.ROOT))
                            || stock.name.toLowerCase(Locale.ROOT).contains(
                                charString.toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        ) {
                            filteredList.add(stock)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = stockListFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                stockListFiltered = results.values as List<Stock>
                firstStart = false
                notifyDataSetChanged()
            }
        }
    }
}