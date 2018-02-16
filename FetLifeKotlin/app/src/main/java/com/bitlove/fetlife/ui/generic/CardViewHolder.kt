package com.bitlove.fetlife.ui.generic

import android.support.v7.widget.RecyclerView
import com.bitlove.fetlife.databinding.ItemDataCardBinding

class CardViewHolder(itemDataCardBinding: ItemDataCardBinding) : RecyclerView.ViewHolder(
        itemDataCardBinding.root) {

    private var binding: ItemDataCardBinding = itemDataCardBinding

    fun bindTo(cardViewDataHolder: CardViewDataHolder?) {
        binding.cardData = cardViewDataHolder
        binding.executePendingBindings()
    }
}