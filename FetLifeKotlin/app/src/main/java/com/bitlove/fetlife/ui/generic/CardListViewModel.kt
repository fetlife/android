package com.bitlove.fetlife.ui.generic

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

abstract class CardListViewModel<T : CardViewDataHolder> : ViewModel() {
    val cardList = loadCardList()

    abstract fun loadCardList(): LiveData<List<T>>
}