package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.resource.ResourceResult

class CardDetailViewModel : ViewModel() {

    enum class CardType {
        CONVERSATION,
        CONTENT,
        MEMBER,
        EXPLORE
    }

    lateinit var cardId: String
    lateinit var cardType: CardType
    lateinit var cardDetail : LiveData<CardViewDataHolder>
    lateinit var progressTracker: LiveData<ProgressTracker>

    fun init(cardId: String, cardType: CardType) {
        this.cardId = cardId
        this.cardType = cardType
        loadCardDetail(cardId, false)
    }

    open fun refresh() {
        loadCardDetail(cardId, true)
    }

    private fun loadCardDetail(cardId: String, forceLoad: Boolean) {
        val dataSource = FetLifeApplication.instance.fetlifeDataSource
        //TODO(cleanup) use resource instead
        val database = FetLifeApplication.instance.fetLifeContentDatabase
        val resourceResult = when (cardType) {
            CardType.CONVERSATION,CardType.CONTENT -> dataSource.loadContentDetail(cardId) as ResourceResult<CardViewDataHolder>
            CardType.EXPLORE -> dataSource.loadExploreStoryDetail(cardId) as ResourceResult<CardViewDataHolder>
            else -> {throw IllegalArgumentException()}
        }
        cardDetail = resourceResult.liveData
        progressTracker = resourceResult.progressTracker
    }

}