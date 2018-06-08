package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.resource.ResourceResult

class CardDetailViewModelObject(private var cardType: CardDetailViewModel.CardType, private var cardId: String) {

    private var resourceResult : ResourceResult<CardViewDataHolder>? = null
    var cardDetail = MediatorLiveData<CardViewDataHolder>()
    var progressTracker = MediatorLiveData<ProgressTracker>()
    var currentCardSource : LiveData<CardViewDataHolder>? = null
    var currentProgressTrackerSource: LiveData<ProgressTracker>? = null

    open fun refresh(forceLoad: Boolean = false) {
        loadCardDetail(cardId, forceLoad)
    }

    open fun release() {
        resourceResult?.cancel()
        resourceResult = null
    }

    open fun fade() {
        resourceResult?.reducedPriority()
    }

    open fun unfade() {
        resourceResult?.normalPriority()
    }

    private fun loadCardDetail(cardId: String, forceLoad: Boolean) {
        if (currentCardSource != null) {
            cardDetail.removeSource(currentCardSource!!)
        }
        if (currentProgressTrackerSource != null) {
            progressTracker.removeSource(currentProgressTrackerSource!!)
        }

        val dataSource = FetLifeApplication.instance.fetlifeDataSource

        resourceResult?.cancel()
        //TODO(cleanup) use resource instead
        resourceResult = when (cardType) {
            CardDetailViewModel.CardType.CONTENT -> dataSource.getContentDetailLoader(cardId) as ResourceResult<CardViewDataHolder>
            CardDetailViewModel.CardType.EXPLORE_STORY -> dataSource.getExploreStoryDetailLoader(cardId) as ResourceResult<CardViewDataHolder>
            CardDetailViewModel.CardType.EXPLORE_EVENT -> dataSource.getExploreEventDetailLoader(cardId) as ResourceResult<CardViewDataHolder>
            CardDetailViewModel.CardType.GROUP -> dataSource.getGroupDetailLoader(cardId) as ResourceResult<CardViewDataHolder>
            CardDetailViewModel.CardType.FAVORITE -> dataSource.getFavoriteDetailLoader(cardId) as ResourceResult<CardViewDataHolder>
            else -> {throw IllegalArgumentException()}
        }

        cardDetail.addSource(resourceResult!!.liveData, {data -> cardDetail.value = data})
        currentCardSource = resourceResult!!.liveData

        progressTracker.addSource(resourceResult!!.progressTracker, {data -> progressTracker.value = data})
        currentProgressTrackerSource = resourceResult!!.progressTracker

        resourceResult!!.execute()
    }

}