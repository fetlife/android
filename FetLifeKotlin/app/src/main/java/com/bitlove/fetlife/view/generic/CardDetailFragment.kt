package com.bitlove.fetlife.view.generic

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardDetailBinding
import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel
import com.bitlove.fetlife.logic.interactionhandler.CardViewInteractionHandler
import com.bitlove.fetlife.view.navigation.NavigationCallback
import kotlinx.android.synthetic.main.fragment_card_detail.*

class CardDetailFragment : BindingFragment<FragmentCardDetailBinding, CardDetailViewModel>() {

    companion object {
        private const val ARG_CARD_TYPE = "ARG_CARD_TYPE"
        private const val ARG_CARD_ID = "ARG_CARD_ID"
        private const val ARG_SCROLL2BOTTOM = "ARG_SCROLL2BOTTOM"

        fun newInstance(cardId: String, cardType: CardDetailViewModel.CardType, scrollToBottom: Boolean = false) : CardDetailFragment {
            val fragment = CardDetailFragment()
            val bundle = Bundle()
            bundle.putString(ARG_CARD_ID, cardId)
            bundle.putSerializable(ARG_CARD_TYPE, cardType)
            bundle.putBoolean(ARG_SCROLL2BOTTOM,scrollToBottom)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var cardId: String
    private lateinit var cardType: CardDetailViewModel.CardType
    private var scrollToBottom: Boolean = false

    private var cardViewInteractionHandler: CardViewInteractionHandler? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_detail
    }

    override fun getViewModelClass(): Class<CardDetailViewModel>? {
        return CardDetailViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardId = arguments!!.getString(ARG_CARD_ID)
        cardType = arguments!!.getSerializable(ARG_CARD_TYPE) as CardDetailViewModel.CardType
        scrollToBottom = arguments!!.getBoolean(ARG_SCROLL2BOTTOM)

        if (viewModel == null) {
            return
        }

        //TODO remove forever and use state based
        viewModel!!.observeData(cardType, cardId, this, {
            cardData ->
            var childCardData = cardData?.getChild()?:cardData
            if (childCardData != null && cardViewInteractionHandler == null) {
                cardViewInteractionHandler = CardViewInteractionHandler(this, childCardData, true, true, activity as? NavigationCallback, childCardData?.getChildrenScreenTitle())
            }
            binding.cardData = childCardData
            binding.cardInteractionHandler = cardViewInteractionHandler
            binding.cardView!!.cardData = childCardData
            binding.cardView!!.cardInteractionHandler = cardViewInteractionHandler
            //TODO: check if there is a less flickering option
            if (cardData?.getComments()?.isEmpty() == false && scrollToBottom) {
                scrollToBottom = false
                card_scroll.isSmoothScrollingEnabled = false
                card_scroll.postDelayed({card_scroll.fullScroll(View.FOCUS_DOWN);card_scroll.isSmoothScrollingEnabled = true},100)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (scrollToBottom) {
            card_scroll.fullScroll(View.FOCUS_DOWN)
        }

        swipe_refresh.setOnRefreshListener {
            viewModel!!.refresh(cardType, cardId,true)
            swipe_refresh.isRefreshing = false
        }

        viewModel!!.refresh(cardType, cardId, savedInstanceState == null)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        viewModel!!.unfade(cardType,cardId)
    }

    override fun onStop() {
        super.onStop()
        viewModel!!.fade(cardType,cardId)
    }

    override fun onDetach() {
        super.onDetach()
        viewModel!!.remove(cardType,cardId)
    }

}