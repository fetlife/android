package com.bitlove.fetlife.ui.generic

import android.databinding.ViewDataBinding
import android.os.Bundle
import com.bitlove.fetlife.R
import com.bitlove.fetlife.ui.base.BaseActivity

import kotlinx.android.synthetic.main.include_appbar.*

abstract class CardListActivity<DataBinding : ViewDataBinding> : BaseActivity<DataBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)
        setSupportActionBar(toolbar)
    }

}
