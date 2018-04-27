package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.ViewModel
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.model.dataobject.wrapper.ProgressTracker

class CardDetailViewModel : ViewModel() {

    enum class CardType {
        CONVERSATION,
        CONTENT,
        MEMBER,
        EXPLORE_STORY,
        EXPLORE_EVENT
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

    fun observeDataForever(cardType: CardType, cardId: String, observer: (CardViewDataHolder?) -> Unit) {
        getViewModelObject(cardType, cardId).cardDetail.observeForever { data -> observer.invoke(data) }
    }

    fun observeProgressForever(cardType: CardType, cardId: String, observer: (ProgressTracker?) -> Unit) {
        getViewModelObject(cardType,cardId).progressTracker.observeForever{ tracker -> observer.invoke(tracker) }
    }

    fun remove(cardType: CardType, cardId: String) {
        //TODO: remove live data observers
        viewModelObjects.remove(cardType.toString()+cardId)
    }

}