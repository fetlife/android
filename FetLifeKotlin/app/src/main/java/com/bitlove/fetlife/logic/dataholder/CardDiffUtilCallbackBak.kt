package com.bitlove.fetlife.logic.dataholder

import android.support.v7.util.DiffUtil

class CardDiffUtilCallbackBak(private val oldList : List<CardViewDataHolder>, private val newList: List<CardViewDataHolder>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].isSame(newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hasSameContent(newList[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }
}