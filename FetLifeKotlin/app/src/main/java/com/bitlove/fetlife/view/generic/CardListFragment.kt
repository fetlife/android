package com.bitlove.fetlife.view.generic

import android.os.Bundle
import android.view.View
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardListBinding
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.viewmodel.CardListViewModel
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.view.dialog.InformationDialog
import com.bitlove.fetlife.workaroundItemFlickeringOnChange
import kotlinx.android.synthetic.main.fragment_card_list.*

class CardListFragment : BindingFragment<FragmentCardListBinding, CardListViewModel>(), NavigationCallback {

    private var pageSynced = 0

    companion object {
        const val DEFAULT_PAGE_SIZE = 15
        private const val STATE_PAGE_REQUESTED = "STATE_PAGE_REQUESTED"

        private const val ARG_CARD_LIST_TYPE = "ARG_CARD_LIST_TYPE"
        private const val ARG_PARENT_CARD_ID = "ARG_PARENT_CARD_ID"
        private const val ARG_SCREEN_TITLE = "ARG_SCREEN_TITLE"

        fun newInstance(cardListType: CardListViewModel.CardListType, parentCardId : String?, screenTitle: String?) : CardListFragment {
            val fragment = CardListFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_CARD_LIST_TYPE, cardListType)
            bundle.putString(ARG_PARENT_CARD_ID, parentCardId)
            bundle.putString(ARG_SCREEN_TITLE, screenTitle)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var cardListType: CardListViewModel.CardListType
    private var parentCardId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardListType = arguments!!.getSerializable(ARG_CARD_LIST_TYPE) as CardListViewModel.CardListType
        parentCardId = arguments!!.getString(ARG_PARENT_CARD_ID)

        if (viewModel == null) {
            return
        }

        //TODO remove forever and use state based
        //TODO check why is it called several times
        viewModel!!.observerData(cardListType,this,{
            newCardList ->
            val cardListAdapter = (card_list?.adapter as? CardListAdapter)
            cardListAdapter?.submitList(newCardList!!)
        },parentCardId)

        viewModel!!.observerProgress(cardListType,this,{
            tracker -> if (view != null) binding.progressTracker = tracker
        },parentCardId)
    }

    override fun getViewModelClass(): Class<CardListViewModel>? {
        return CardListViewModel::class.java
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_list
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_PAGE_REQUESTED,pageSynced)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(cardListType) {
            CardListViewModel.CardListType.FAVORITES -> InformationDialog.show(activity!!,InformationDialog.InfoType.FAVORITES,"InformationDialog.InfoType.FAVORITES",true)
            CardListViewModel.CardListType.EXPLORE_STUFF_YOU_LOVE,
            CardListViewModel.CardListType.EXPLORE_FRIENDS_FEED,
            CardListViewModel.CardListType.EXPLORE_KINKY_AND_POPULAR,
            CardListViewModel.CardListType.EXPLORE_STUFF_YOU_LOVE -> InformationDialog.show(activity!!,InformationDialog.InfoType.EXPLORE,"InformationDialog.InfoType.EXPLORE",true)
        }

        card_list.workaroundItemFlickeringOnChange()
        //TODO: check for memoryLeak owner/callback
        val cardListAdapter = CardListAdapter(this, this, arguments!!.getString(ARG_SCREEN_TITLE))
        cardListAdapter.cardDisplayListener = object : CardListAdapter.CardDisplayListener {
            override fun onCardDisplayed(position: Int, card: CardViewDataHolder?) {
                if (card?.getRemoteOrder()?:position >= (pageSynced-1) * DEFAULT_PAGE_SIZE -1) {
                    viewModel!!.loadMore(cardListType, parentCardId)
                    pageSynced++
                }
            }
        }
        card_list.adapter = cardListAdapter

        swipe_refresh.setOnRefreshListener {
            val adapter = (card_list.adapter as CardListAdapter)
//            val pagedList = adapter.currentList
//            while (pagedList!!.size > 10) {
//                pagedList.remove(pagedList!!.last())
//            }
            adapter.currentList?.dataSource?.invalidate()

            pageSynced = 2
            viewModel!!.refresh(cardListType, parentCardId,true, DEFAULT_PAGE_SIZE)
            swipe_refresh.isRefreshing = false
        }

        pageSynced = savedInstanceState?.getInt(STATE_PAGE_REQUESTED)?:2
        viewModel!!.refresh(cardListType, parentCardId,savedInstanceState == null, DEFAULT_PAGE_SIZE)
    }

    override fun onStart() {
        super.onStart()
        viewModel!!.unfade(cardListType, parentCardId)
    }

    override fun onStop() {
        super.onStop()
        viewModel!!.fade(cardListType, parentCardId)
    }

    override fun onDetach() {
        super.onDetach()
        viewModel!!.remove(cardListType, parentCardId)
    }

    override fun onOpenUrl(url: String): Boolean {
        return (activity as? NavigationCallback)?.onOpenUrl(url)?:false
    }

    override fun onNavigate(actionId: Int?): Boolean {
        return (activity as? NavigationCallback)?.onNavigate(actionId)?:false
    }

    override fun onChangeView(navigation: Int?) {
        (activity as? NavigationCallback)?.onChangeView(navigation)
    }

    override fun onLayoutChange(layout: NavigationCallback.Layout?) {
        (activity as? NavigationCallback)?.onLayoutChange(layout)
    }

    override fun onCardNavigate(cardList: List<CardViewDataHolder>, position: Int, screenTitle: String?, scrollToBottom: Boolean) {
        (activity as? NavigationCallback)?.onCardNavigate(cardList, position, screenTitle, scrollToBottom)
    }

}