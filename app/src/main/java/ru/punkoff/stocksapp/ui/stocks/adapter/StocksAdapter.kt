package ru.punkoff.stocksapp.ui.stocks.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ItemStockBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.utils.PicassoLoader
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
    private lateinit var onStockListener: OnStockClickListener
    private lateinit var onStarListener: OnStarClickListener
    private lateinit var stockListFiltered: List<Stock>
    fun attachListener(listener: OnStockClickListener) {
        onStockListener = listener
    }

    fun attachSaveListener(listener: OnStarClickListener) {
        onStarListener = listener
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
        private val stockClickListener: View.OnClickListener = View.OnClickListener {
            onStockListener.onClick(currentStock, currentPosition)
        }

        private val starClickListener: View.OnClickListener = View.OnClickListener {
            if (!isEnabledStar) {
                isEnabledStar = true
                favourite.background =
                    ContextCompat.getDrawable(favourite.context, android.R.drawable.star_on)
            } else {
                isEnabledStar = false
                favourite.background =
                    ContextCompat.getDrawable(favourite.context, android.R.drawable.star_off)
            }
            onStarListener.onClick(currentStock)
        }
        private var isEnabledStar = false

        private val favourite = binding.root.findViewById<ImageView>(R.id.favorite)
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
                        ContextCompat.getColorStateList(root.context, R.color.white)
                }

                PicassoLoader.loadImage(imageView = logo, url = currentItem.logo)
                root.setOnClickListener(stockClickListener)
            }
        }

        init {
            favourite.setOnClickListener(starClickListener)
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