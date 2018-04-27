package com.bitlove.fetlife.view.generic

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardDetailBinding
import com.bitlove.fetlife.databinding.ItemDataCardBinding
import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import com.bitlove.fetlife.view.navigation.NavigationCallback

class CardDetailFragment : BindingFragment<FragmentCardDetailBinding, CardDetailViewModel>() {

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

    private var cardViewInteractionHandler: CardViewInteractionHandler? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_detail
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

        //TODO remove forever and use state based
        viewModel!!.observeDataForever(cardType, cardId, {
            cardData ->
            if (cardData != null && cardViewInteractionHandler == null) {
                cardViewInteractionHandler = CardViewInteractionHandler(cardData, true, true, activity as? NavigationCallback, cardData.getChildrenScreenTitle())
            }
            binding.cardView!!.cardData = cardData
            binding.cardView!!.cardInteractionHandler = cardViewInteractionHandler
        })

        viewModel!!.refresh(cardType, cardId, savedInstanceState == null)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        viewModel!!.remove(cardType,cardId)
    }

}