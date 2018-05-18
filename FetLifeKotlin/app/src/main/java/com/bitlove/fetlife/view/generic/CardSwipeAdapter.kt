package com.bitlove.fetlife.view.generic

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel

class CardSwipeAdapter(private val cardType: CardDetailViewModel.CardType, var cardIds: List<String>, fragmentManager: FragmentManager?, private val scrollToBottom: Boolean = false) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = cardIds.size

    override fun getItem(position: Int): Fragment {
        return CardDetailFragment.newInstance(cardIds[position], cardType, scrollToBottom)
    }

}