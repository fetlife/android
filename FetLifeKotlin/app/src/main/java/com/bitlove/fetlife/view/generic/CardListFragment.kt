package com.bitlove.fetlife.view.generic

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.view.View
import com.bitlove.fetlife.view.base.BindingFragment
import com.bitlove.fetlife.viewmodel.generic.CardDiffUtilCallback
import com.bitlove.fetlife.viewmodel.generic.CardListViewModel
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.bitlove.fetlife.workaroundItemFlickeringOnChange

import kotlinx.android.synthetic.main.fragment_card_list.*

abstract class CardListFragment<DH: CardViewDataHolder, DataBinding : ViewDataBinding, ViewModel : CardListViewModel<DH>> : BindingFragment<DataBinding,ViewModel>() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardList.workaroundItemFlickeringOnChange()
        cardList.adapter = CardListAdapter<DH>()
        //TODO remove forever and use state based
        viewModel.cardList.observeForever({
            newCardList ->
            if (cardList != null) {
                val cardListAdapter = (cardList.adapter as CardListAdapter<DH>)
                val diffResult = DiffUtil.calculateDiff(CardDiffUtilCallback(cardListAdapter.items, newCardList!!))
                cardListAdapter.items = newCardList!!
                diffResult.dispatchUpdatesTo(cardList.adapter)
            }
        })
        if (savedInstanceState == null) {
            viewModel.refresh()
        }
    }
}