package com.bitlove.fetlife.viewmodel.conversation

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.Conversation
import com.bitlove.fetlife.model.dataobject.ConversationWithMessages
import com.bitlove.fetlife.viewmodel.generic.CardListViewModel

class ConversationListViewModel : CardListViewModel<ConversationWithMessages>() {
    override fun loadCardList(forceLoad: Boolean): LiveData<List<ConversationWithMessages>> {
        return FetLifeApplication.instance.fetlifeDataSource.loadConversations(forceLoad,1,1)
    }
}