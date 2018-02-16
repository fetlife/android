package com.bitlove.fetlife.ui

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.ui.generic.CardListActivity
import com.bitlove.fetlife.ui.generic.CardViewDataHolder
import io.reactivex.Flowable

class ConversationsActivity : CardListActivity() {
    override fun getCardFlowable(): Flowable<List<CardViewDataHolder>> {
        return FetLifeApplication.instance!!.fetlifeDatabase.conversationDao().getAllOrderedByLastUpdated() as Flowable<List<CardViewDataHolder>>;
    }
}