package com.bitlove.fetlife.view.generic

import android.os.Bundle
import android.view.View
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler

class CardDetailFragment : BindingFragment<ItemDataCardBinding, CardDetailViewModel>() {

    companion object {
        private const val ARG_CARD_TYPE = "ARG_CARD_TYPE"
        private const val ARG_CARD_ID = "ARG_CARD_ID"
        fun newInstance(cardId: String, cardType: CardDetailViewModel.CardType) : CardDetailFragment {
            val fragment = CardDetailFragment()
            val bundle = Bundle()
            bundle.putString(ARG_CARD_ID, cardId)
            bundle.putSerializable(ARG_CARD_TYPE, cardType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var cardId: String
    private lateinit var cardType: CardDetailViewModel.CardType

    override fun getLayoutRes(): Int {
        return R.layout.item_data_card
    }

    override fun getViewModelClass(): Class<CardDetailViewModel>? {
        return CardDetailViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardId = arguments.getString(ARG_CARD_ID)
        cardType = arguments.getSerializable(ARG_CARD_TYPE) as CardDetailViewModel.CardType
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel == null) {
            return
        }
        viewModel!!.init(cardId, cardType)

        //TODO remove forever and use state based
        viewModel!!.cardDetail.observeForever({
            cardData ->
            binding.cardData = cardData
            binding.cardInteractionHandler = CardViewInteractionHandler(cardData as CardViewDataHolder)
        })
    }
}