package com.bitlove.fetlife.ui.generic

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

abstract class CardListViewModel<T : CardViewDataHolder> : ViewModel() {
    val cardList = loadCardList(false)

    abstract fun loadCardList(forceLoad: Boolean): LiveData<List<T>>

    open fun refresh() {
        loadCardList(true)
    }
}