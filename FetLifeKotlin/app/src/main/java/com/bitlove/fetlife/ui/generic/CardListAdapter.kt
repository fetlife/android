package com.bitlove.fetlife.ui.generic

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.inflateBinding

class CardListAdapter<T: CardViewDataHolder> : RecyclerView.Adapter<CardViewHolder<T>>() {

    var items : List<T> = ArrayList()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CardViewHolder<T>, position: Int) = holder.bindTo(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder<T> {
        val binding = ItemDataCardBinding.inflate(LayoutInflater.from(parent.context))
        return CardViewHolder(binding)
    }

}