package com.bitlove.fetlife.view.screen

import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardListBinding
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.view.generic.CardListFragment
import com.bitlove.fetlife.viewmodel.explore.FreshAndPervyViewModel
import com.bitlove.fetlife.viewmodel.explore.StuffYouLoveViewModel

class FreshAndPervyFragment : CardListFragment<ExploreStory, FragmentCardListBinding, FreshAndPervyViewModel>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_list
    }

    override fun getViewModelClass(): Class<FreshAndPervyViewModel> {
        return FreshAndPervyViewModel::class.java
    }
}