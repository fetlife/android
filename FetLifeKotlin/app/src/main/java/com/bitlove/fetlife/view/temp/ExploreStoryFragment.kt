//package com.bitlove.fetlife.view.temp
//
//import com.bitlove.fetlife.R
//import com.bitlove.fetlife.databinding.ItemDataCardBinding
//import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
//import com.bitlove.fetlife.view.generic.CardDetailFragment
//import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel
//
//class ExploreStoryFragment : CardDetailFragment<ExploreStory, ItemDataCardBinding, CardDetailViewModel>() {
//
//    companion object {
//        fun newInstance(cardId: String) : ExploreStoryFragment {
//            val contentFragment = ExploreStoryFragment()
//            contentFragment.setArguments(cardId)
//            return contentFragment
//        }
//    }
//
//    override fun getViewModelClass(): Class<CardDetailViewModel<ExploreStory>>? {
//        return ExploreStoryViewModel::class.java as Class<CardDetailViewModel<ExploreStory>>
//    }
//
//    override fun getLayoutRes(): Int {
//        return R.layout.item_data_card
//    }
//
//}