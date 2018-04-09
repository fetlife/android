package com.bitlove.fetlife.logic.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder

class CardListViewModel : ViewModel() {

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    enum class CardListType {
        CONVERSATIONS,
        EXPLORE_STUFF_YOU_LOVE,
        EXPLORE_FRESH_AND_PERVY,
        EXPLORE_KINKY_AND_POPULAR,
        EXPLORE_FRIENDS_FEED
    }

    lateinit var cardListType : CardListType
    lateinit var cardList : LiveData<List<CardViewDataHolder>>

    fun init(cardListType: CardListType) {
        this.cardListType = cardListType
        cardList = loadCardList(false)
    }

    open fun refresh(limit: Int = DEFAULT_PAGE_SIZE, page: Int = 1) {
        loadCardList(true, limit, page)
    }

    private fun loadCardList(forceLoad: Boolean, limit: Int = DEFAULT_PAGE_SIZE, page: Int = 1): LiveData<List<CardViewDataHolder>> {
        val dataSource = FetLifeApplication.instance.fetlifeDataSource
        return when (cardListType) {
            CardListType.EXPLORE_FRESH_AND_PERVY -> dataSource.loadFreshAndPervy(forceLoad,page,limit) as LiveData<List<CardViewDataHolder>>
            CardListType.EXPLORE_KINKY_AND_POPULAR -> dataSource.loadKinkyAndPopular(forceLoad,page,limit) as LiveData<List<CardViewDataHolder>>
            CardListType.EXPLORE_STUFF_YOU_LOVE -> dataSource.loadStuffYouLove(forceLoad,page,limit) as LiveData<List<CardViewDataHolder>>
            else -> dataSource.loadConversations(forceLoad,page,limit) as LiveData<List<CardViewDataHolder>>
        }
    }

}