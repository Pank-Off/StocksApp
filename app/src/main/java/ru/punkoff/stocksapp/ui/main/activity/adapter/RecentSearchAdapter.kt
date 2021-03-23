package ru.punkoff.stocksapp.ui.main.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.punkoff.stocksapp.databinding.ItemPopularSearchBinding

class RecentSearchAdapter : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    private var mData = mutableListOf<String>()

    private lateinit var clickListener: OnButtonClickListener

    fun attachListener(listener: OnButtonClickListener) {
        clickListener = listener
    }

    fun setData(name: String) {
        if (!mData.contains(name)) {
            mData.add(name)
            notifyItemInserted(mData.lastIndexOf(name))
        }
    }

    fun initialSetData(data: Set<String>) {
        mData = data.toMutableList()
    }

    fun getData() = mData.toSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        return RecentSearchViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = mData.size

    inner class RecentSearchViewHolder(
        parent: ViewGroup,
        private val binding: ItemPopularSearchBinding = ItemPopularSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentPosition = 0
        private val button = binding.popularBtn
        private val buttonClickListener: View.OnClickListener = View.OnClickListener {
            clickListener.onClick(mData[currentPosition])
        }

        fun bind(position: Int) {
            currentPosition = position
            with(binding) {
                popularBtn.text = mData[currentPosition]
            }
        }

        init {
            button.setOnClickListener(buttonClickListener)
        }
    }
}