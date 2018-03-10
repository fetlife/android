package com.bitlove.fetlife.view.conversation

import android.os.Bundle
import com.bitlove.fetlife.R
import com.bitlove.fetlife.inTransaction
import com.bitlove.fetlife.view.generic.CardListActivity

class ConversationsActivity : CardListActivity() {
    override fun getLayoutRes(): Int {
        return R.layout.activity_base
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            fragmentManager.inTransaction { add(R.id.baseFragmentContainer, ConversationsFragment()) }
        }
    }
}