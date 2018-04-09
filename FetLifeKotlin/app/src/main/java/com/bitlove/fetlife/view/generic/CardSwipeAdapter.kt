package com.bitlove.fetlife.view.generic

import android.app.Fragment
import android.app.FragmentManager
import android.support.v13.app.FragmentStatePagerAdapter
import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel

class CardSwipeAdapter(private val cardType: CardDetailViewModel.CardType, var cardIds: List<String>, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = cardIds.size

    override fun getItem(position: Int): Fragment {
        return CardDetailFragment.newInstance(cardIds[position], cardType)
    }

}