package com.bitlove.fetlife.viewmodel.explore

import android.arch.lifecycle.LiveData
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.dataobject.entity.ExploreStoryEntity
import com.bitlove.fetlife.viewmodel.generic.CardListViewModel

//class StuffYouLoveViewModel : CardListViewModel<ExploreStory>() {
//    override fun loadCardList(forceLoad: Boolean): LiveData<List<ExploreStory>> {
//        return FetLifeApplication.instance.fetlifeDataSource.loadStuffYouLove(forceLoad,1,1)
//    }
//}