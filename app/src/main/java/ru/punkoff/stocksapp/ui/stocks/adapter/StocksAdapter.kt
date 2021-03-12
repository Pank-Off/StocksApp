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
import androidx.recyclerview.widget.RecyclerView
import ru.punkoff.stocksapp.R
import ru.punkoff.stocksapp.databinding.ItemStockBinding
import ru.punkoff.stocksapp.model.Stock
import ru.punkoff.stocksapp.utils.MyDiffUtilCallback
import ru.punkoff.stocksapp.utils.PicassoLoader
import java.util.*
import kotlin.collections.ArrayList

class StocksAdapter : RecyclerView.Adapter<StocksAdapter.StocksViewHolder>(), Filterable {

    private lateinit var onStockListener: OnStockClickListener
    private lateinit var onStarListener: OnStarClickListener
    private var stockList = mutableListOf<Stock>()
    private var stockListFiltered = stockList

    private var isEnabled = true
    fun attachListener(listener: OnStockClickListener) {
        onStockListener = listener
    }

    fun attachStarListener(listener: OnStarClickListener) {
        onStarListener = listener
    }

    fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
        notifyDataSetChanged()
    }

    fun setData(newStockList: List<Stock>) {
        val oldList = stockList
        stockList.clear()
        stockList.addAll(newStockList)
        val diffUtilCallback = MyDiffUtilCallback(oldList, newStockList)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {
        return StocksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        holder.bind(stockListFiltered[position], position)
    }

    override fun getItemCount(): Int {
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
            if (currentStock.isFavourite) {
                onStarListener.deleteStock(currentStock)
            } else {
                onStarListener.saveStock(currentStock)
            }
            notifyItemChanged(currentPosition)
        }

        private val favourite = binding.root.findViewById<ImageView>(R.id.favorite)
        fun bind(currentItem: Stock, position: Int) {
            currentPosition = position
            currentStock = currentItem
            with(binding) {
                root.isEnabled = isEnabled
                favorite.isEnabled = isEnabled
                ticker.text = currentItem.ticker
                name.text = currentItem.name
                price.text = currentItem.price.toString()
                stock.text = currentItem.stock.toString()
                if (currentItem.isFavourite) {
                    favourite.background =
                        ContextCompat.getDrawable(favourite.context, android.R.drawable.star_on)
                } else {
                    favourite.background =
                        ContextCompat.getDrawable(favourite.context, android.R.drawable.star_off)
                }
                if (currentItem.stock < 0) {
                    stock.setTextColor(Color.RED)
                }
                if (position % 2 == 0) {
                    Log.d(javaClass.simpleName, "Position: $position")
                    Log.d(javaClass.simpleName, "Item: ${currentItem.ticker}")
                    root.backgroundTintList =
                        ContextCompat.getColorStateList(root.context, R.color.white)
                } else {
                    root.backgroundTintList =
                        ContextCompat.getColorStateList(root.context, R.color.card_background)
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
                stockListFiltered = if (charString.isEmpty()) {
                    stockList
                } else {
                    val filteredList: ArrayList<Stock> = ArrayList()
                    for (stock in stockList) {
                        if (stock.ticker.toLowerCase(Locale.ROOT)
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
                stockListFiltered = results.values as MutableList<Stock>
                notifyDataSetChanged()
            }
        }
    }
}