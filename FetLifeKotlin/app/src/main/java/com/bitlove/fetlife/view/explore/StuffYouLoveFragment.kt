package com.bitlove.fetlife.view.explore

import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardListBinding
import com.bitlove.fetlife.model.dataobject.joined.ConversationWithMessages
import com.bitlove.fetlife.model.dataobject.temp.ExploreStory
import com.bitlove.fetlife.view.generic.CardListFragment
import com.bitlove.fetlife.viewmodel.conversation.ConversationListViewModel
import com.bitlove.fetlife.viewmodel.explore.StuffYouLoveViewModel

class StuffYouLoveFragment : CardListFragment<ExploreStory, FragmentCardListBinding, StuffYouLoveViewModel>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_list
    }

    override fun getViewModelClass(): Class<StuffYouLoveViewModel> {
        return StuffYouLoveViewModel::class.java
    }
}