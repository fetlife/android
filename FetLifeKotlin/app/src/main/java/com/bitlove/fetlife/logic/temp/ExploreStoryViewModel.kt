//package com.bitlove.fetlife.logic.viewmodel
//
//import android.arch.lifecycle.LiveData
//import com.bitlove.fetlife.FetLifeApplication
//import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
//import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel
//
//class ExploreStoryViewModel : CardDetailViewModel<ExploreStory>() {
//
//    override fun loadCardDetail(cardId: String, forceLoad: Boolean): LiveData<ExploreStory> {
//        //TODO: use resource
//        return FetLifeApplication.instance.fetLifeContentDatabase.exploreStoryDao().getStory(cardId)
//    }
//}