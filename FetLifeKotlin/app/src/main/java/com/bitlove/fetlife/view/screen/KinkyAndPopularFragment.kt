package com.bitlove.fetlife.view.screen

import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardListBinding
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.view.generic.CardListFragment
import com.bitlove.fetlife.viewmodel.explore.KinkyAndPopularViewModel
import com.bitlove.fetlife.viewmodel.explore.StuffYouLoveViewModel

class KinkyAndPopularFragment : CardListFragment<ExploreStory, FragmentCardListBinding, KinkyAndPopularViewModel>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_list
    }

    override fun getViewModelClass(): Class<KinkyAndPopularViewModel> {
        return KinkyAndPopularViewModel::class.java
    }
}