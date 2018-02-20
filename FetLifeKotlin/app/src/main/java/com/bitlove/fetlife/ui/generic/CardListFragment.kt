package com.bitlove.fetlife.ui.generic

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitlove.fetlife.ui.base.BaseFragment

import kotlinx.android.synthetic.main.fragment_card_list.*

abstract class CardListFragment<T: CardViewDataHolder, DataBinding : ViewDataBinding, ViewModel : CardListViewModel<T>> : BaseFragment<DataBinding,ViewModel>() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardList.adapter = CardListAdapter<T>()
        viewModel.cardList.observeForever(Observer {
            newCardList ->
            val cardListAdapter = (cardList.adapter as CardListAdapter<T>)
            cardListAdapter.items = newCardList!!
            cardListAdapter.notifyDataSetChanged()
        })
    }
}