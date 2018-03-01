package com.bitlove.fetlife.ui.conversation

import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardListBinding
import com.bitlove.fetlife.datasource.dataobject.Conversation
import com.bitlove.fetlife.ui.generic.CardListFragment

class ConversationsFragment : CardListFragment<Conversation, FragmentCardListBinding, ConversationListViewModel>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_list
    }

    override fun getViewModelClass(): Class<ConversationListViewModel> {
        return ConversationListViewModel::class.java
    }
}