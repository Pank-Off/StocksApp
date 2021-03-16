package ru.punkoff.stocksapp.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.punkoff.stocksapp.databinding.ItemPopularSearchBinding

class PopularSearchAdapter : RecyclerView.Adapter<PopularSearchAdapter.PopularSearchViewHolder>() {

    private val data = listOf(
        "Apple",
        "First Solar",
        "Amazon",
        "Alibaba",
        "Google",
        "Facebook",
        "Tesla",
        "MasterCard",
        "Microsoft",
    )

    private lateinit var clickListener: OnButtonClickListener

    fun attachListener(listener: OnButtonClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularSearchViewHolder {
        return PopularSearchViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PopularSearchViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = data.size

    inner class PopularSearchViewHolder(
        parent: ViewGroup,
        private val binding: ItemPopularSearchBinding = ItemPopularSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentPosition = 0
        private val button = binding.popularBtn
        private val buttonClickListener: View.OnClickListener = View.OnClickListener {
            clickListener.onClick(data[currentPosition])
        }

        fun bind(position: Int) {
            currentPosition = position
            with(binding) {
                popularBtn.text = data[position]
            }
        }

        init {
            button.setOnClickListener(buttonClickListener)
        }
    }
}