package com.bitlove.fetlife.view.generic

import android.support.v7.widget.RecyclerView
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.CardViewInteractionHandler

class CardViewHolder<in DH: CardViewDataHolder>(itemDataCardBinding: ItemDataCardBinding) : RecyclerView.ViewHolder(
        itemDataCardBinding.root) {

    private var binding: ItemDataCardBinding = itemDataCardBinding

    fun bindTo(cardViewDataHolder: DH, interactionHandler: CardViewInteractionHandler) {
        binding.cardData = cardViewDataHolder
        binding.cardInteractionHandler = interactionHandler
//        binding.executePendingBindings()
    }
}