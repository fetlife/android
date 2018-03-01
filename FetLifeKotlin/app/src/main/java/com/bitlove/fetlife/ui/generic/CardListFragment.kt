package com.bitlove.fetlife.ui.generic

import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.View
import com.bitlove.fetlife.ui.base.BindingFragment

import kotlinx.android.synthetic.main.fragment_card_list.*

abstract class CardListFragment<T: CardViewDataHolder, DataBinding : ViewDataBinding, ViewModel : CardListViewModel<T>> : BindingFragment<DataBinding,ViewModel>() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardList.adapter = CardListAdapter<T>()
        viewModel.cardList.observeForever(Observer {
            newCardList ->
            val cardListAdapter = (cardList.adapter as CardListAdapter<T>)
            cardListAdapter.items = newCardList!!
            cardListAdapter.notifyDataSetChanged()
        })
        viewModel.refresh()
    }
}