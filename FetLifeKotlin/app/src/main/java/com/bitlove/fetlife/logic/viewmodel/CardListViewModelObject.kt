package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.paging.PagedList
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker
import com.bitlove.fetlife.model.resource.ResourceResult

class CardListViewModelObject(private var cardListType : CardListViewModel.CardListType)  {

    private var resourceResult : ResourceResult<PagedList<CardViewDataHolder>>? = null
    var cardList = MediatorLiveData<PagedList<CardViewDataHolder>>()
    var progressTracker = MediatorLiveData<ProgressTracker>()
    var currentCardSource : LiveData<PagedList<CardViewDataHolder>>? = null
    var currentProgressTrackerSource: LiveData<ProgressTracker>? = null

    open fun refresh(forceLoad: Boolean = false, limit: Int) {
        loadCardList(forceLoad, limit)
    }

    open fun loadMore() {
        resourceResult?.loadMore()
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

    private fun loadCardList(forceLoad: Boolean, limit: Int) {
        if (currentCardSource != null) {
            cardList.removeSource(currentCardSource!!)
        }
        if (currentProgressTrackerSource != null) {
            progressTracker.removeSource(currentProgressTrackerSource!!)
        }

        val dataSource = FetLifeApplication.instance.fetlifeDataSource
        resourceResult?.cancel()
        resourceResult = when (cardListType) {
            CardListViewModel.CardListType.CONVERSATIONS_ALL -> dataSource.getConversationsLoader(forceLoad,limit) as ResourceResult<PagedList<CardViewDataHolder>>
            CardListViewModel.CardListType.CONVERSATIONS_INBOX -> dataSource.getConversationsLoader(forceLoad,limit) as ResourceResult<PagedList<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_FRESH_AND_PERVY -> dataSource.getFreshAndPervyLoader(forceLoad,limit) as ResourceResult<PagedList<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_KINKY_AND_POPULAR -> dataSource.getKinkyAndPopularLoader(forceLoad,limit) as ResourceResult<PagedList<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_STUFF_YOU_LOVE -> dataSource.getStuffYouLoveLoader(forceLoad,limit) as ResourceResult<PagedList<CardViewDataHolder>>
            CardListViewModel.CardListType.EXPLORE_FRIENDS_FEED -> dataSource.getFriendsFeedLoader(forceLoad,limit) as ResourceResult<PagedList<CardViewDataHolder>>
            CardListViewModel.CardListType.FAVORITES -> dataSource.getFavoritesLoader(limit) as ResourceResult<PagedList<CardViewDataHolder>>
        }

        cardList.addSource(resourceResult!!.liveData, {data -> cardList.value = data})
        currentCardSource = resourceResult!!.liveData

        progressTracker.addSource(resourceResult!!.progressTracker, {data -> progressTracker.value = data})
        currentProgressTrackerSource = resourceResult!!.progressTracker

        resourceResult!!.execute()
    }

}