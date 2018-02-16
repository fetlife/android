package com.bitlove.fetlife.ui.generic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bitlove.fetlife.R
import io.reactivex.Flowable

import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_card_list.*

abstract class CardListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)
        setSupportActionBar(toolbar)

        cardList.adapter = CardListAdapter()
        getCardFlowable().subscribe {
            list ->
            val cardListAdapter = (cardList.adapter as CardListAdapter)
            cardListAdapter.items = list
            cardListAdapter.notifyDataSetChanged()
        }
    }

    abstract fun getCardFlowable() : Flowable<List<CardViewDataHolder>>

}
