package com.bitlove.fetlife.view.navigation

interface NavigationCallback {

    enum class Layout {
        CARD,
        BASIC,
        WEB;

        fun next(): Layout? {
            val values = Layout.values()
            return values[(ordinal+1)%values.size]
        }
    }

    fun onNavigate(actionId: Int?): Boolean
    fun onLayoutChange(layout: Layout?)

}