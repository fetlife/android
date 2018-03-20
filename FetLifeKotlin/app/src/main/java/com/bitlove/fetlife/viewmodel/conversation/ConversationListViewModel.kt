package com.bitlove.fetlife.viewmodel.conversation

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.viewmodel.generic.CardListViewModel

class ConversationListViewModel : CardListViewModel<Content>() {
    override fun loadCardList(forceLoad: Boolean): LiveData<List<Content>> {
        return FetLifeApplication.instance.fetlifeDataSource.loadConversations(forceLoad,1,1)
    }
}