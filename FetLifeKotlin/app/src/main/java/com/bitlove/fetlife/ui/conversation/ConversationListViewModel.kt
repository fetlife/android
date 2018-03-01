package com.bitlove.fetlife.ui.conversation

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.datasource.dataobject.Conversation
import com.bitlove.fetlife.ui.generic.CardListViewModel

class ConversationListViewModel : CardListViewModel<Conversation>() {
    override fun loadCardList(forceLoad: Boolean): LiveData<List<Conversation>> {
        return FetLifeApplication.instance.fetlifeDataSource.loadConversations(forceLoad,1,1)
    }
}