package com.bitlove.fetlife.view.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bitlove.fetlife.R
import com.bitlove.fetlife.inTransaction
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.Member
import com.bitlove.fetlife.view.generic.CardSwipeFragment
import com.bitlove.fetlife.view.generic.ResourceActivity

class PhoneCardActivity : ResourceActivity() {

    companion object {
        private const val EXTRA_CARD_TYPE = "EXTRA_CARD_TYPE"
        private const val EXTRA_CARD_IDS = "EXTRA_CARD_IDS"
        private const val EXTRA_SELECTED_POSITION = "EXTRA_SELECTED_POSITION"

        fun start(context: Context, cardList: List<CardViewDataHolder>, position: Int) {
            val cardTemplate = cardList.firstOrNull()?: return
            val intent = Intent(context, PhoneCardActivity::class.java)
            val cardType = when (cardTemplate) {
                is ExploreStory -> CardDetailViewModel.CardType.EXPLORE
                is Content -> CardDetailViewModel.CardType.CONTENT
                else -> throw IllegalArgumentException()
            }
            intent.putExtra(EXTRA_CARD_TYPE, cardType)

            val cardIds = ArrayList<String>()
            for (card in cardList) {
                cardIds.add(card.getLocalId()!!)
            }
            intent.putExtra(EXTRA_CARD_IDS,cardIds)
            intent.putExtra(EXTRA_SELECTED_POSITION,position)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private lateinit var cardType : CardDetailViewModel.CardType
    private var cardIds: ArrayList<String> = ArrayList()
    private var selectedPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardType = intent.getSerializableExtra(EXTRA_CARD_TYPE) as CardDetailViewModel.CardType
        cardIds = intent.getStringArrayListExtra(EXTRA_CARD_IDS)
        selectedPosition = intent.getIntExtra(EXTRA_SELECTED_POSITION,-1)

        setContentView(R.layout.activity_phone_fragment)

        if (savedInstanceState == null) {
            fragmentManager.inTransaction { replace(R.id.fragment_container, CardSwipeFragment.newInstance(cardType, cardIds,selectedPosition)) }
        }
    }




}