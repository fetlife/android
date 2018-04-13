package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.resource.ResourceResult

class CardListViewModelObject(private var cardListType : CardListViewModel.CardListType)  {

    var cardList = MediatorLiveData<List<CardViewDataHolder>>()
    var progressTracker = MediatorLiveData<ProgressTracker>()
    var currentCardSource : LiveData<List<CardViewDataHolder>>? = null
    var currentProgressTrackerSource: LiveData<ProgressTracker>? = null

    open fun refresh(forceLoad: Boolean = false, limit: Int, page: Int) {
        loadCardList(forceLoad, limit, page)
    }

    private fun loadCardList(forceLoad: Boolean, limit: Int, page: Int) {
        if (currentCardSource != null) {
            cardList.removeSource(currentCardSource!!)
        }
        if (currentProgressTrackerSource != null) {
            progressTracker.removeSource(currentProgressTrackerSource!!)
        }

        val dataSource = FetLifeApplication.instance.fetlifeDataSource
        val resourceResult = when (cardListType) {
            CardListViewModel.CardListType.CONVERSATIONS_ALL -> dataSource.getConversationsLoader(forceLoad,page,limit) as ResourceResult<List<CardViewDataHolder>>
            CardListViewModel.CardListType.CONVERSATIONS_INBOX -> dataSource.getConversationsLoader(forceLoad,page,limit) as ResourceResult<List<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_FRESH_AND_PERVY -> dataSource.getFreshAndPervyLoader(forceLoad,page,limit) as ResourceResult<List<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_KINKY_AND_POPULAR -> dataSource.getKinkyAndPopularLoader(forceLoad,page,limit) as ResourceResult<List<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_STUFF_YOU_LOVE -> dataSource.getStuffYouLoveLoader(forceLoad,page,limit) as ResourceResult<List<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_FRIENDS_FEED -> dataSource.getFriendsFeedLoader(forceLoad,page,limit) as ResourceResult<List<CardViewDataHolder>>
        }

        cardList.addSource(resourceResult.liveData, {data -> cardList.value = data})
        currentCardSource = resourceResult.liveData

        progressTracker.addSource(resourceResult.progressTracker, {data -> progressTracker.value = data})
        currentProgressTrackerSource = resourceResult.progressTracker

        resourceResult.fetch()
    }

}