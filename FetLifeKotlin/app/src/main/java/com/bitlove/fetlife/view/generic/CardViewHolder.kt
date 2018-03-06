package com.bitlove.fetlife.view.generic

import android.support.v7.widget.RecyclerView
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder

class CardViewHolder<in T: CardViewDataHolder>(itemDataCardBinding: ItemDataCardBinding) : RecyclerView.ViewHolder(
        itemDataCardBinding.root) {

    private var binding: ItemDataCardBinding = itemDataCardBinding

    fun bindTo(cardViewDataHolder: T) {
        binding.cardData = cardViewDataHolder
        binding.executePendingBindings()
    }
}