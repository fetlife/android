package com.bitlove.fetlife.ui.generic

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.inflateBinding

class CardListAdapter : RecyclerView.Adapter<CardViewHolder>() {

    var items : List<CardViewDataHolder> = ArrayList()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) = holder.bindTo(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = parent.inflateBinding(ItemDataCardBinding::class) as ItemDataCardBinding
        return CardViewHolder(binding)
    }

}