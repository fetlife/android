package com.bitlove.fetlife.view.explore

import android.os.Bundle
import com.bitlove.fetlife.R
import com.bitlove.fetlife.inTransaction
import com.bitlove.fetlife.view.generic.CardListActivity

class ExploreActivity : CardListActivity() {
    override fun getLayoutRes(): Int {
        return R.layout.activity_base
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            fragmentManager.inTransaction { add(R.id.baseFragmentContainer, StuffYouLoveFragment()) }
        }
    }
}