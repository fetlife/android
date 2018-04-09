package com.bitlove.fetlife.view.navigation

import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder

interface NavigationCallback {

    enum class Layout {
        CARD,
        WEB;

        fun next(): Layout? {
            val values = Layout.values()
            return values[(ordinal+1)%values.size]
        }
    }

    fun onOpenUrl(url: String): Boolean
    fun onNavigate(actionId: Int?): Boolean
    fun onLayoutChange(layout: Layout?)
    fun onCardNavigate(cardList: List<CardViewDataHolder>, position: Int)

}