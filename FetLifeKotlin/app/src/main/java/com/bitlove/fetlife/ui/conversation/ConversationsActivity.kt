package com.bitlove.fetlife.ui.conversation

import android.os.Bundle
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.ActivityCardListBinding
import com.bitlove.fetlife.inTransaction
import com.bitlove.fetlife.ui.generic.CardListActivity

class ConversationsActivity : CardListActivity<ActivityCardListBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.activity_card_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            fragmentManager.inTransaction { add(R.id.cardListFragmentContainer, ConversationsFragment()) }
        }
    }
}