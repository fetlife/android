package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

//TODO: merge with CardDetailViewModel into super class
//TODO: cleanup?: add observers att call and simplify?
class CardListViewModel : ViewModel() {

    enum class CardListType {
        CONVERSATIONS_INBOX,
        CONVERSATIONS_ALL,
        EXPLORE_STUFF_YOU_LOVE,
        EXPLORE_FRESH_AND_PERVY,
        EXPLORE_KINKY_AND_POPULAR,
        EXPLORE_FRIENDS_FEED,
        GROUPS,
        FAVORITES
    }

    var viewModelObjects = HashMap<CardListType, CardListViewModelObject>()

//    fun observerDataForever(cardListType: CardListType, observer: (List<CardViewDataHolder>?) -> Unit) {
//        getViewModelObject(cardListType).cardList.observeForever { data -> observer.invoke(data) }
//    }
//
//    fun observerProgressForever(cardListType: CardListType, observer: (ProgressTracker?) -> Unit) {
//        getViewModelObject(cardListType).progressTracker.observeForever{ tracker -> observer.invoke(tracker) }
//    }

    fun observerData(cardListType: CardListType, owner: LifecycleOwner, observer: (PagedList<CardViewDataHolder>?) -> Unit) {
        getViewModelObject(cardListType).cardList.observe(owner, Observer {data -> observer.invoke(data)})
    }

    fun observerProgress(cardListType: CardListType, owner: LifecycleOwner, observer: (ProgressTracker?) -> Unit) {
        getViewModelObject(cardListType).progressTracker.observe(owner, Observer { tracker -> observer.invoke(tracker) })
    }

    private fun getViewModelObject(cardListType: CardListType) : CardListViewModelObject {
        if (!viewModelObjects.containsKey(cardListType)) {
            viewModelObjects[cardListType] = CardListViewModelObject(cardListType)
        }
        return viewModelObjects[cardListType]!!
    }

    open fun refresh(cardListType: CardListType, forceLoad: Boolean = false, limit: Int) {
        getViewModelObject(cardListType).refresh(forceLoad,limit)
    }

    open fun loadMore(cardListType: CardListType) {
        getViewModelObject(cardListType).loadMore()
    }

    fun fade(cardListType: CardListType) {
        getViewModelObject(cardListType).fade()
    }

    fun unfade(cardListType: CardListType) {
        getViewModelObject(cardListType).unfade()
    }

    fun remove(cardListType: CardListType) {
        //TODO: remove live data observers
        val viewModelObject = viewModelObjects.remove(cardListType)
        viewModelObject?.release()
    }

}