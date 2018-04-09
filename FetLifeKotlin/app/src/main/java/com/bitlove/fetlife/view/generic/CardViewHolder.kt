package com.bitlove.fetlife.view.generic

import android.support.v7.widget.RecyclerView
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler

class CardViewHolder(itemDataCardBinding: ItemDataCardBinding) : RecyclerView.ViewHolder(
        itemDataCardBinding.root) {

    private var binding: ItemDataCardBinding = itemDataCardBinding

    fun bindTo(cardViewDataHolder: CardViewDataHolder, interactionHandler: CardViewInteractionHandler) {
        binding.cardData = cardViewDataHolder
        binding.cardInteractionHandler = interactionHandler
//        binding.executePendingBindings()
    }
}