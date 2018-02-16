package com.bitlove.fetlife.ui.generic

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.bumptech.glide.load.engine.Resource

abstract class CardListViewModel(cardList : LiveData<Resource<List<CardViewHolder>>>) : ViewModel() {
    val cardList = cardList
}