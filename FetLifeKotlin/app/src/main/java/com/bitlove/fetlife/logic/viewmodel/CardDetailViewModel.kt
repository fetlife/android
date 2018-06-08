package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

//TODO: merge with CardDetailViewModel into super class
//TODO: cleanup?: add observers att call and simplify?
class CardDetailViewModel : ViewModel() {

    enum class CardType {
        CONTENT,
        MEMBER,
        GROUP,
        EXPLORE_STORY,
        EXPLORE_EVENT,
        FAVORITE
    }

    var viewModelObjects = HashMap<String, CardDetailViewModelObject>()

    private fun getViewModelObject(cardType: CardType, cardId: String) : CardDetailViewModelObject {
        if (!viewModelObjects.containsKey(cardType.toString()+cardId)) {
            viewModelObjects[cardType.toString()+cardId] = CardDetailViewModelObject(cardType, cardId)
        }
        return viewModelObjects[cardType.toString()+cardId]!!
    }


    open fun refresh(cardType: CardType, cardId: String, forceLoad: Boolean = false) {
        getViewModelObject(cardType, cardId).refresh(forceLoad)
    }

    fun observeData(cardType: CardType, cardId: String, owner: LifecycleOwner, observer: (CardViewDataHolder?) -> Unit) {
        getViewModelObject(cardType, cardId).cardDetail.observe(owner, Observer{ data -> observer.invoke(data) })
    }

    fun observeProgress(cardType: CardType, cardId: String, owner: LifecycleOwner, observer: (ProgressTracker?) -> Unit) {
        getViewModelObject(cardType,cardId).progressTracker.observe(owner, Observer{ tracker -> observer.invoke(tracker) })
    }

//    fun observeDataForever(cardType: CardType, cardId: String, observer: (CardViewDataHolder?) -> Unit) {
//        getViewModelObject(cardType, cardId).cardDetail.observeForever { data -> observer.invoke(data) }
//    }
//
//    fun observeProgressForever(cardType: CardType, cardId: String, observer: (ProgressTracker?) -> Unit) {
//        getViewModelObject(cardType,cardId).progressTracker.observeForever{ tracker -> observer.invoke(tracker) }
//    }

    fun fade(cardType: CardType, cardId: String) {
        getViewModelObject(cardType,cardId).fade()
    }

    fun unfade(cardType: CardType, cardId: String) {
        getViewModelObject(cardType,cardId).unfade()
    }

    fun remove(cardType: CardType, cardId: String) {
        //TODO: remove live data observers
        val viewModelObject = viewModelObjects.remove(cardType.toString()+cardId)
        viewModelObject?.release()
    }

}