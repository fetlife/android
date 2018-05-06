package com.bitlove.fetlife.view.generic

import android.arch.lifecycle.LifecycleOwner
import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import com.bitlove.fetlife.model.dataobject.wrapper.Content

//TODO check generic DH + IH?, none?, DH?
class CardListAdapter(private val owner: LifecycleOwner, private val navigationCallback: NavigationCallback, private val cardListTitle: String? = null) : PagedListAdapter<CardViewDataHolder,CardViewHolder>(CardViewDataHolder.DiffUtil){

//    init {
//        setHasStableIds(true)
//    }

    var interactionHandlers : HashMap<String?, CardViewInteractionHandler> = HashMap()

    override fun submitList(pagedList: PagedList<CardViewDataHolder>?) {
        for (interactionHandler in interactionHandlers.values) {
            if (pagedList == null ) {
                continue
            }
            interactionHandler.cardList = pagedList
            interactionHandler.cardData = interactionHandler.cardList!![interactionHandler.position]
        }
        super.submitList(pagedList)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) = holder.bindTo(getItem(position), getInteractionHandler(position))

    private fun getInteractionHandler(position: Int): CardViewInteractionHandler? {
        val item = getItem(position) ?: return null
        if (!interactionHandlers.containsKey(item.getLocalId())) {
            interactionHandlers[item.getLocalId()] = if ((item as? Content)?.getType() == Content.TYPE.CONVERSATION.toString())
                CardViewInteractionHandler(owner, currentList!!.toList(), position, false, item.displayComments() == true, navigationCallback, cardListTitle, 1, false)
            else {
                CardViewInteractionHandler(owner, currentList!!.toList(), position, false, item.displayComments() == true, navigationCallback, cardListTitle)
            }
        }
        return interactionHandlers[item.getLocalId()]!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemDataCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CardViewHolder(binding)
    }

}