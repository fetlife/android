//package com.bitlove.fetlife.view.temp
//
//import com.bitlove.fetlife.databinding.FragmentCardSwipeBinding
//import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
//import com.bitlove.fetlife.view.generic.CardSwipeFragment
//import com.bitlove.fetlife.logic.viewmodel.CardListViewModel
//
//class ExploreStorySwipeFragment : CardSwipeFragment<ExploreStory,FragmentCardSwipeBinding, CardListViewModel<ExploreStory>>() {
//
//    override fun getViewModelClass(): Class<CardListViewModel<ExploreStory>>? {
//        return null
//    }
//
//    companion object {
//        fun newInstance(cardIds: List<String>, selectedPosition: Int) : ExploreStorySwipeFragment {
//            val contentFragment = ExploreStorySwipeFragment()
//            contentFragment.setArguments(cardIds,selectedPosition)
//            return contentFragment
//        }
//    }
//
//}