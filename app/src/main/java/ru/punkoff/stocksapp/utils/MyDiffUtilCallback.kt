package ru.punkoff.stocksapp.utils

import androidx.recyclerview.widget.DiffUtil
import ru.punkoff.stocksapp.model.Stock

class MyDiffUtilCallback(private val oldList: List<Stock>, private val newList: List<Stock>) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].ticker == newList[newItemPosition].ticker

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}