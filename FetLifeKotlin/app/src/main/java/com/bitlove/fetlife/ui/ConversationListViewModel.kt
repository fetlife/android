package com.bitlove.fetlife.ui

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.datasource.dataobject.Conversation
import com.bitlove.fetlife.ui.generic.CardListViewModel

class ConversationListViewModel : CardListViewModel<Conversation>() {
    override fun loadCardList(): LiveData<List<Conversation>> {
        return FetLifeApplication.instance.fetlifeDataSource.loadConversations(false,1,1)
    }
}