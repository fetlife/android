package com.bitlove.fetlife.view.navigation

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

}