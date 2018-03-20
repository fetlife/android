package com.bitlove.fetlife.view.generic

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.toUniqueLong
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.CardViewInteractionHandler

//TODO check generic DH + IH?, none?, DH?
class CardListAdapter<DH: CardViewDataHolder> : RecyclerView.Adapter<CardViewHolder<DH>>(){

    init {
        setHasStableIds(true)
    }

    var items : List<DH> = ArrayList()
    var interactionHandlers : HashMap<String?,CardViewInteractionHandler> = HashMap()

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        //TODO safety check
        return items[position].getLocalId()!!.toUniqueLong()
    }

    override fun onBindViewHolder(holder: CardViewHolder<DH>, position: Int) = holder.bindTo(items[position], getInteractionHandler(position))

    private fun getInteractionHandler(position: Int): CardViewInteractionHandler {
        val item = items[position]
        if (!interactionHandlers.containsKey(item.getLocalId())) {
            interactionHandlers[item.getLocalId()] = CardViewInteractionHandler()
        }
        return interactionHandlers[item.getLocalId()]!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder<DH> {
        val binding = ItemDataCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CardViewHolder(binding)
    }

}