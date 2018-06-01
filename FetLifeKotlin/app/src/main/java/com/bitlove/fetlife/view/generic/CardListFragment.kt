package com.bitlove.fetlife.view.generic

import android.arch.paging.PagedList
import android.os.Bundle
import android.view.View
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardListBinding
import com.bitlove.fetlife.view.navigation.NavigationCallback
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.viewmodel.CardListViewModel
import com.bitlove.fetlife.view.dialog.InformationDialog
import com.bitlove.fetlife.workaroundItemFlickeringOnChange
import kotlinx.android.synthetic.main.fragment_card_list.*

class CardListFragment : BindingFragment<FragmentCardListBinding, CardListViewModel>(), NavigationCallback {

    companion object {
        private const val ARG_CARD_LIST_TYPE = "ARG_CARD_LIST_TYPE"
        private const val ARG_SCREEN_TITLE = "ARG_SCREEN_TITLE"
        fun newInstance(cardListType: CardListViewModel.CardListType, screenTitle: String?) : CardListFragment {
            val fragment = CardListFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_CARD_LIST_TYPE, cardListType)
            bundle.putString(ARG_SCREEN_TITLE, screenTitle)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var cardListType: CardListViewModel.CardListType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardListType = arguments!!.getSerializable(ARG_CARD_LIST_TYPE) as CardListViewModel.CardListType

        if (viewModel == null) {
            return
        }

        //TODO remove forever and use state based
        //TODO check why is it called several times
        viewModel!!.observerData(cardListType,this,{
            newCardList ->
            val cardListAdapter = (card_list?.adapter as? CardListAdapter)
            cardListAdapter?.submitList(newCardList!!)
        })

        viewModel!!.observerProgress(cardListType,this,{
            tracker -> if (view != null) binding.progressTracker = tracker
        })
    }

    override fun getViewModelClass(): Class<CardListViewModel>? {
        return CardListViewModel::class.java
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_list
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
        card_list.adapter = CardListAdapter(this, this, arguments!!.getString(ARG_SCREEN_TITLE))

        swipe_refresh.setOnRefreshListener {
            val adapter = (card_list.adapter as CardListAdapter)
//            val pagedList = adapter.currentList
//            while (pagedList!!.size > 10) {
//                pagedList.remove(pagedList!!.last())
//            }
            adapter.currentList?.dataSource?.invalidate()

            viewModel!!.refresh(cardListType,true)
            swipe_refresh.isRefreshing = false
        }

        viewModel!!.refresh(cardListType,savedInstanceState == null)
    }

    override fun onStart() {
        super.onStart()
        viewModel!!.unfade(cardListType)
    }

    override fun onStop() {
        super.onStop()
        viewModel!!.fade(cardListType)
    }

    override fun onDetach() {
        super.onDetach()
        viewModel!!.remove(cardListType)
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