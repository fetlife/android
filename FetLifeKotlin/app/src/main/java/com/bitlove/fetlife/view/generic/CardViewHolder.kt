package com.bitlove.fetlife.view.generic

import android.app.ActionBar
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.getSafeColor
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import org.jetbrains.anko.backgroundColor

class CardViewHolder(itemDataCardBinding: ItemDataCardBinding?, rootView: View? = null) : RecyclerView.ViewHolder(
        itemDataCardBinding?.root ?: rootView) {

    private var binding: ItemDataCardBinding? = itemDataCardBinding

    private var defaultLayoutParams: ViewGroup.LayoutParams? = null

    fun bindTo(cardViewDataHolder: CardViewDataHolder?, interactionHandler: CardViewInteractionHandler?) {
        if (binding == null) {
            return
        }
        binding!!.cardData = cardViewDataHolder
        binding!!.cardInteractionHandler = interactionHandler
//        //TODO: remove workaround
//        if (defaultLayoutParams == null) {
//            defaultLayoutParams = itemView.layoutParams
//        }
//        if (cardViewDataHolder?.isPlaceholder() == true) {
////            itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1)
//            itemView.visibility = View.GONE
//        } else {
////            itemView.layoutParams = defaultLayoutParams
//            itemView.visibility = View.VISIBLE
//        }
//        binding.executePendingBindings()
    }
}