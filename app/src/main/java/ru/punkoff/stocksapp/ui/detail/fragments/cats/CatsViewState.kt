package ru.punkoff.stocksapp.ui.detail.fragments.cats

import ru.punkoff.stocksapp.model.Cat

sealed class CatsViewState {
    object Loading : CatsViewState()
    object EMPTY : CatsViewState()
    data class CatValue(val cats: List<Cat>) : CatsViewState()
}
