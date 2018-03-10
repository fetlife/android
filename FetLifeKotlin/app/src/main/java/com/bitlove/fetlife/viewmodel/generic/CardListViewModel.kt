package com.bitlove.fetlife.viewmodel.generic

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.view.View

abstract class CardListViewModel<T : CardViewDataHolder> : ViewModel() {

    val cardList = loadCardList(false)

    abstract fun loadCardList(forceLoad: Boolean): LiveData<List<T>>

    open fun refresh() {
        loadCardList(true)
    }

}