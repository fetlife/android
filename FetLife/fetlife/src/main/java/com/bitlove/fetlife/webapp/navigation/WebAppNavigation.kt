package com.bitlove.fetlife.webapp.navigation

import com.bitlove.fetlife.R

class WebAppNavigation {

    companion object {
        private const val WEB_APP_BASE_URL = "https://staging.fetlife.com"
        private const val URL_REGEX_TEAM = "^$WEB_APP_BASE_URL/team/?"
    }


    private val titleMap = LinkedHashMap<String,Int>().apply {
        put(URL_REGEX_TEAM, R.string.url_title_team)
    }

    fun getTitle(url: String) : Int? {
        for ((key,value) in titleMap) {
            if (url.matches(key.toRegex())) {
                return value
            }
        }
        return null
    }

}