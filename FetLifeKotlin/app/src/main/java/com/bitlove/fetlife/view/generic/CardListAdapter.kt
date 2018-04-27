package com.bitlove.fetlife.view.generic

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.toUniqueLong
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import org.apache.commons.lang3.Conversion

//TODO check generic DH + IH?, none?, DH?
class CardListAdapter(private val navigationCallback: NavigationCallback, private val cardListTitle: String? = null) : RecyclerView.Adapter<CardViewHolder>(){

    init {
        setHasStableIds(true)
    }

    var items : List<CardViewDataHolder> = ArrayList()
        set(value) {
            field = value
            for (interactionHandler in interactionHandlers.values) {
                interactionHandler.cardList = value
            }
        }
    var interactionHandlers : HashMap<String?, CardViewInteractionHandler> = HashMap()

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        //TODO safety check
        return items[position].getLocalId()!!.toUniqueLong()
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) = holder.bindTo(items[position], getInteractionHandler(position))

    //TODO: clean up need of CardData from two sources
    private fun getInteractionHandler(position: Int): CardViewInteractionHandler {
//        return CardViewInteractionHandler(items[position], items, position, navigationCallback)
        val item = items[position]
        if (!interactionHandlers.containsKey(item.getLocalId())) {
            interactionHandlers[item.getLocalId()] = if ((item as? Content)?.getType() == Content.TYPE.CONVERSATION.toString())
                CardViewInteractionHandler(items, position, false, items[position].displayComments() == true, navigationCallback, cardListTitle, 1, false)
            else {
                CardViewInteractionHandler(items, position, false, items[position].displayComments() == true, navigationCallback, cardListTitle)
            }
        }
        return interactionHandlers[item.getLocalId()]!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemDataCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CardViewHolder(binding)
    }

}