package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder

class CardDetailViewModel() : ViewModel() {

    enum class CardType {
        CONVERSATION,
        CONTENT,
        MEMBER,
        EXPLORE
    }

    lateinit var cardId: String
    lateinit var cardType: CardType
    lateinit var cardDetail : LiveData<CardViewDataHolder>

    fun init(cardId: String, cardType: CardType) {
        this.cardId = cardId
        this.cardType = cardType
        cardDetail = loadCardDetail(cardId, false)
    }

    open fun refresh() {
        loadCardDetail(cardId, true)
    }

    private fun loadCardDetail(cardId: String, forceLoad: Boolean): LiveData<CardViewDataHolder> {
        val dataSource = FetLifeApplication.instance.fetlifeDataSource
        //TODO(cleanup) use resource instead
        val database = FetLifeApplication.instance.fetLifeContentDatabase
        return when (cardType) {
            CardType.CONVERSATION,CardType.CONTENT -> database.contentDao().getContent(cardId) as LiveData<CardViewDataHolder>
            CardType.EXPLORE -> database.exploreStoryDao().getStory(cardId) as LiveData<CardViewDataHolder>
            else -> database.contentDao().getContent(cardId) as LiveData<CardViewDataHolder>
        }

    }

}