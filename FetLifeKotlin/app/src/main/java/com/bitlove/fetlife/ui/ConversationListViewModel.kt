package com.bitlove.fetlife.ui

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.datasource.dataobject.Conversation
import com.bitlove.fetlife.ui.generic.CardListViewModel

class ConversationListViewModel(conversationList: List<Conversation>) : CardListViewModel(FetLifeApplication.instance.fetlifeDataSource.conversations)