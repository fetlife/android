package com.bitlove.fetlife.view.generic

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleRegistry
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
import com.bitlove.fetlife.view.widget.ImageActivity
import kotlinx.android.synthetic.main.fragment_card_list.*
import kotlinx.android.synthetic.main.item_data_card.*
import org.jetbrains.anko.imageURI

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

        if (viewModel == null) {
            return
        }

        //TODO remove forever and use state based
        viewModel!!.observeData(cardType, cardId, this, {
            cardData ->
            if (cardData != null && cardViewInteractionHandler == null) {
                cardViewInteractionHandler = CardViewInteractionHandler(this, cardData, true, true, activity as? NavigationCallback, cardData.getChildrenScreenTitle())
            }
            binding.cardData = cardData
            binding.cardInteractionHandler = cardViewInteractionHandler
            binding.cardView!!.cardData = cardData
            binding.cardView!!.cardInteractionHandler = cardViewInteractionHandler
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh.setOnRefreshListener {
            viewModel!!.refresh(cardType, cardId,true)
            swipe_refresh.isRefreshing = false
        }

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