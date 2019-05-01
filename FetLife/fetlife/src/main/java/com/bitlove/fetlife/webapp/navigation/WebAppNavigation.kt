package com.bitlove.fetlife.webapp.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.browser.customtabs.CustomTabsIntent
import com.bitlove.fetlife.BuildConfig
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.*
import com.bitlove.fetlife.model.service.FetLifeApiIntentService
import com.bitlove.fetlife.util.ColorUtil
import com.bitlove.fetlife.util.PictureUtil
import com.bitlove.fetlife.util.UrlUtil
import com.bitlove.fetlife.view.screen.BaseActivity
import com.bitlove.fetlife.view.screen.resource.EventActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupMessagesActivity
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity
import com.bitlove.fetlife.view.widget.ImageViewerWrapper
import com.bitlove.fetlife.webapp.kotlin.openInBrowser
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

class WebAppNavigation {

    companion object {

        private var totalTimeSpent: Long = 0

        const val WEBAPP_BASE_URL = "https://fetlife.com"

        //Base constants
        internal const val WEB_TITLE_SEPARATOR = " |"
        internal const val WEB_COUNTER_SEPARATOR = ") "
        internal const val WEB_EXTRA_SEPARATOR = " -"

        private const val NATIVE_NAVIGATION_LOGIN = "NATIVE_NAVIGATION_LOGIN"
        private const val NATIVE_NAVIGATION_HOME = "NATIVE_NAVIGATION_HOME"

        //NOTE: Temporarily duplicated with native code area TODO(WEBAPP): decoupling
        private const val QUERY_PARAM_API_IDS = "api_ids"
        private const val SERVER_ID_PREFIX = "SERVER_ID_PREFIX:"

        private const val REGEX_BASE_URL = "https:\\/\\/(staging\\.)?fetlife\\.com"

        // * FetLife Urls
        private const val URL_REGEX_INTERNAL_LINK = "^$REGEX_BASE_URL.*\$"

        // * Download Urls
        private const val URL_REGEX_DOWNLOAD_LINK = "^$REGEX_BASE_URL\\/.*\\/download[^\\/]*\$"

        // * Native Supported Urls
        private const val URL_REGEX_LOGIN = "^$REGEX_BASE_URL\\/(users\\/sign_in|login)\\/?.*\$"

        private const val URL_REGEX_EVENT = "^$REGEX_BASE_URL\\/events\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_GROUP = "^$REGEX_BASE_URL\\/groups\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_GROUP_POST = "^$REGEX_BASE_URL\\/groups\\/(\\w+)\\/group_posts\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_USER_PROFILE = "^$REGEX_BASE_URL\\/users\\/([0-9]+)[^\\/]*\$"
        private const val URL_REGEX_USER_PICTURE = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/pictures\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_USER_VIDEO = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/videos\\/(\\w+)[^\\/]*\$"

        // * New WebView Flow Urls
        //private const val URL_REGEX_TEMPLATE = "^$REGEX_BASE_URL\\/TEMPLATE\\/?[^\\/]*\$"
        private const val URL_REGEX_INBOX_MAIN = "^$REGEX_BASE_URL\\/inbox[^\\/]*\$"
        private const val URL_REGEX_INBOX_ALL = "^$REGEX_BASE_URL\\/inbox\\/all[^\\/]*\$"
        private const val URL_REGEX_INBOX_ARCHIVED = "^$REGEX_BASE_URL\\/inbox\\/archived[^\\/]*\$"
        private const val URL_REGEX_CONVERSATION_MAIN = "^$REGEX_BASE_URL\\/conversations\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_NOTIFICATIONS_MAIN = "^$REGEX_BASE_URL\\/notifications[^\\/]*\$"
        private const val URL_REGEX_REQUESTS_MAIN = "^$REGEX_BASE_URL\\/requests[^\\/]*\$"
        private const val URL_REGEX_TEAM_MAIN = "^$REGEX_BASE_URL\\/team[^\\/]*\$"
        private const val URL_REGEX_SUPPORT_MAIN = "^$REGEX_BASE_URL\\/support[^\\/]*\$"
        private const val URL_REGEX_WALLPAPERS_MAIN = "^$REGEX_BASE_URL\\/wallpapers[^\\/]*\$"
        private const val URL_REGEX_GLOSSARY_MAIN = "^$REGEX_BASE_URL\\/glossary[^\\/]*\$"
        private const val URL_REGEX_ADS_MAIN = "^$REGEX_BASE_URL\\/ads[^\\/]*\$"
        private const val URL_REGEX_CONTACT_MAIN = "^$REGEX_BASE_URL\\/contact[^\\/]*\$"
        private const val URL_REGEX_GUIDELINES_MAIN = "^$REGEX_BASE_URL\\/guidelines[^\\/]*\$"
        private const val URL_REGEX_HELP_MAIN = "^$REGEX_BASE_URL\\/help[^\\/]*\$"
        private const val URL_REGEX_ANDROID_MAIN = "^$REGEX_BASE_URL\\/android[^\\/]*\$"
        private const val URL_REGEX_PRIVACY_MAIN = "^$REGEX_BASE_URL\\/(privacy)[^\\/]*\$"
        //places
        private const val URL_REGEX_PLACES_MAIN = "^$REGEX_BASE_URL\\/(p|places)[^\\/]*\$" //WARNING: Also matches with privacy TODO(WEBAPP): find better
        //user content
        private const val URL_REGEX_USER_POST = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/posts\\/(\\w+)[^\\/]*\$"
        //qna
        private const val URL_REGEX_QNA_REVIEW = "^$REGEX_BASE_URL\\/q\\/review\\/?[^\\/]*\$"

        // *  Open with No History Urls
        private const val URL_REGEX_PASSWORD_NEW_EMAIL = "^$REGEX_BASE_URL\\/users\\/password\\/new\$"
        private const val URL_REGEX_PASSWORD_NEW_MOBILE = "^$REGEX_BASE_URL\\/users\\/password\\/new_.*\$"

        private const val URL_REGEX_SETTINGS_ACCOUNT = "^$REGEX_BASE_URL\\/settings\\/account\\/?.*\$"
        private const val URL_REGEX_SETTINGS_PRIVACY = "^$REGEX_BASE_URL\\/settings\\/privacy\\/?.*\$"
        private const val URL_REGEX_SETTINGS_NOTIFICATIONS = "^$REGEX_BASE_URL\\/settings\\/notifications\\/?.*\$"
        private const val URL_REGEX_SETTINGS_BLOCKED = "^$REGEX_BASE_URL\\/settings\\/blocked\\/?.*\$"
        //search
        private const val URL_REGEX_SEARCH_MAIN = "^$REGEX_BASE_URL\\/search[^\\/]*\$"
        private const val URL_REGEX_SEARCH_KINKSTERS = "^$REGEX_BASE_URL\\/search\\/kinksters\\/?.*\$"
        private const val URL_REGEX_SEARCH_PICTURES = "^$REGEX_BASE_URL\\/search\\/pictures\\/?.*\$"
        private const val URL_REGEX_SEARCH_WRITINGS = "^$REGEX_BASE_URL\\/search\\/writings\\/?.*\$"
        private const val URL_REGEX_SEARCH_VIDEOS = "^$REGEX_BASE_URL\\/search\\/videos\\/?.*\$"
        private const val URL_REGEX_SEARCH_GROUPS = "^$REGEX_BASE_URL\\/search\\/groups\\/?.*\$"
        private const val URL_REGEX_SEARCH_DISCUSSIONS = "^$REGEX_BASE_URL\\/search\\/discussions\\/?.*\$"
        private const val URL_REGEX_SEARCH_EVENTS = "^$REGEX_BASE_URL\\/search\\/events\\/?.*\$"
        private const val URL_REGEX_SEARCH_FETISHES = "^$REGEX_BASE_URL\\/search\\/fetishes\\/?.*\$"
        private const val URL_REGEX_SEARCH_PLACES = "^$REGEX_BASE_URL\\/search\\/places\\/?.*\$"
        //qna
        private const val URL_REGEX_QNA_MAIN = "^$REGEX_BASE_URL\\/q[^\\/\\?]*\$"
        private const val URL_REGEX_QNA_FETLIFE = "^$REGEX_BASE_URL\\/q\\/fetlife.*\$"
        private const val URL_REGEX_QNA_TAGS = "^$REGEX_BASE_URL\\/q\\/tags.*\$"
        private const val URL_REGEX_QNA_POPULAR = "^$REGEX_BASE_URL\\/q(\\/fetlife)?\\?filter=popular.*\$"
        private const val URL_REGEX_QNA_UNANSWERED = "^$REGEX_BASE_URL\\/q(\\/fetlife)?\\?filter=unanswered.*\$"
        private const val URL_REGEX_QNA_MINE = "^$REGEX_BASE_URL\\/q(\\/fetlife)?\\?filter=mine.*\$"

        // * Other WebView Supported Urls
        private const val URL_REGEX_PASSWORD_INCORRECT_EMAIL = "^$REGEX_BASE_URL\\/users\\/password[^\\/]*\$"
        private const val URL_REGEX_PASSWORD_INCORRECT_PHONE = "^$REGEX_BASE_URL\\/users\\/password\\/via_phone[^\\/]*\$"
        private const val URL_REGEX_PASSWORD_VERIFY_PHONE = "^$REGEX_BASE_URL\\/users\\/password\\/verify_phone[^\\/]*\$"
        private const val URL_REGEX_PASSWORD_EDIT = "^$REGEX_BASE_URL\\/users\\/password\\/edit[^\\/]*\$"

        //private const val URL_REGEX_TEMPLATE = "^$REGEX_BASE_URL\\/TEMPLATE\\/?.*\$"
        private const val URL_REGEX_TEAM = "^$REGEX_BASE_URL\\/team\\/?.*\$"
        private const val URL_REGEX_SUPPORT = "^$REGEX_BASE_URL\\/support\\/?.*\$"

        private const val URL_REGEX_WALLPAPERS = "^$REGEX_BASE_URL\\/wallpapers\\/?.*\$"
        private const val URL_REGEX_GLOSSARY = "^$REGEX_BASE_URL\\/glossary\\/?.*\$"
        private const val URL_REGEX_ADS = "^$REGEX_BASE_URL\\/ads\\/?.*\$"
        private const val URL_REGEX_CONTACT = "^$REGEX_BASE_URL\\/contact\\/?.*\$"
        private const val URL_REGEX_GUIDELINES = "^$REGEX_BASE_URL\\/guidelines\\/?.*\$"
        private const val URL_REGEX_HELP = "^$REGEX_BASE_URL\\/help\\/?.*\$"
        private const val URL_REGEX_ANDROID = "^$REGEX_BASE_URL\\/android\\/?.*\$"
        private const val URL_REGEX_PRIVACY = "^$REGEX_BASE_URL\\/privacy\\/?.*\$"
        private const val URL_REGEX_LEGALESE = "^$REGEX_BASE_URL\\/legalese\\/?.*\$"
        //places
        private const val URL_REGEX_PLACES = "^$REGEX_BASE_URL\\/(p|places)\\/?.*\$"
        private const val URL_REGEX_CITIES = "^$REGEX_BASE_URL\\/cities\\/?.*\$"
        private const val URL_REGEX_COUNTRIES = "^$REGEX_BASE_URL\\/countries\\/?.*\$"
        private const val URL_REGEX_ADMINISTRATIVE_AREAS = "^$REGEX_BASE_URL\\/administrative_areas\\/?.*\$"
        //settings
        private const val URL_REGEX_SETTINGS = "^$REGEX_BASE_URL\\/settings\\/?.*\$"
        //search
        private const val URL_REGEX_SEARCH = "^$REGEX_BASE_URL\\/search\\/?.*\$"
        //qna
        private const val URL_REGEX_QNA = "^$REGEX_BASE_URL\\/q\\/?.*\$"
        private const val URL_REGEX_QNA_QUESTION = "^$REGEX_BASE_URL\\/q\\/.*\\/.*\$"

        private const val URL_REGEX_NOTIFICATIONS = "^$REGEX_BASE_URL\\/notifications\\/?.*\$"
        private const val URL_REGEX_REQUESTS = "^$REGEX_BASE_URL\\/requests\\/?.*\$"
        private const val URL_REGEX_INBOX = "^$REGEX_BASE_URL\\/inbox\\/?.*\$"
        private const val URL_REGEX_CONVERSATION = "^$REGEX_BASE_URL\\/conversations\\/(\\w+).*\$"
        private const val URL_REGEX_CONVERSATION_NEW = "^$REGEX_BASE_URL\\/conversations\\/new\\?with=[0-9]+.*\$"

        private const val URL_REGEX_LOGIN_PASSWORD_SENT = "^$REGEX_BASE_URL\\/sent_login_information[^\\/]*\$"

        private const val URL_REGEX_HOME = "^$REGEX_BASE_URL\\/home[^\\/]*\$"

        //* Full Urls
        const val URL_LOGIN = "$WEBAPP_BASE_URL/users/sign_in"

        private const val URL_QNA_NEW = "$WEBAPP_BASE_URL/q/new"
        private const val URL_INBOX_MAIN = "$WEBAPP_BASE_URL/inbox"
        private const val URL_INBOX_ALL = "$WEBAPP_BASE_URL/inbox/all"
        private const val URL_INBOX_ARCHIVED = "$WEBAPP_BASE_URL/inbox/archived"

    }

    private val parentMap = LinkedHashMap<String, String>().apply {
        put(URL_QNA_NEW, URL_REGEX_QNA_MAIN)
        put(URL_REGEX_CONVERSATION, URL_REGEX_INBOX)
    }

    private val optionsMenuMap = LinkedHashMap<String, List<Int>>().apply {
        put(URL_REGEX_INBOX_MAIN, ArrayList<Int>().apply {
            add(R.string.menu_options_inbox_all)
            add(R.string.menu_options_inbox_archived)
        })
        put(URL_REGEX_INBOX_ALL, ArrayList<Int>().apply {
            add(R.string.menu_options_inbox_default)
            add(R.string.menu_options_inbox_archived)
        })
        put(URL_REGEX_INBOX_ARCHIVED, ArrayList<Int>().apply {
            add(R.string.menu_options_inbox_default)
            add(R.string.menu_options_inbox_all)
        })
    }

    private val optionsMenuUrlMap = LinkedHashMap<Int, String>().apply {
        put(R.string.menu_options_inbox_default, URL_INBOX_MAIN)
        put(R.string.menu_options_inbox_all, URL_INBOX_ALL)
        put(R.string.menu_options_inbox_archived, URL_INBOX_ARCHIVED)
    }

    private val titleMap = LinkedHashMap<String, Int>().apply {
        put(URL_REGEX_INBOX_MAIN, R.string.url_title_inbox)
        put(URL_REGEX_INBOX_ARCHIVED, R.string.url_title_inbox_archived)
        put(URL_REGEX_INBOX_ALL, R.string.url_title_inbox_all)
        put(URL_REGEX_TEAM_MAIN, R.string.url_title_team)
        put(URL_REGEX_SUPPORT_MAIN, R.string.url_title_support)
        put(URL_REGEX_WALLPAPERS_MAIN, R.string.url_title_wallpapers)
        put(URL_REGEX_GLOSSARY_MAIN, R.string.url_title_glossary)
        put(URL_REGEX_ADS_MAIN, R.string.url_title_ads)
        put(URL_REGEX_CONTACT_MAIN, R.string.url_title_contact)
        put(URL_REGEX_GUIDELINES_MAIN, R.string.url_title_guidelines)
        put(URL_REGEX_HELP_MAIN, R.string.url_title_help)
        put(URL_REGEX_ANDROID_MAIN, R.string.url_title_android)
        put(URL_REGEX_PRIVACY_MAIN, R.string.url_title_privacy)
        put(URL_REGEX_PLACES_MAIN, R.string.url_title_places)
        put(URL_REGEX_SEARCH_MAIN, R.string.url_title_search)
        put(URL_REGEX_QNA_MAIN, R.string.url_title_questions)
        put(URL_REGEX_NOTIFICATIONS_MAIN, R.string.url_title_notifications)
        put(URL_REGEX_REQUESTS_MAIN, R.string.url_title_requests)
    }

    private val newWebViewFlowLinkSetSet = LinkedHashSet<String>().apply {
        add(URL_REGEX_USER_POST)
        add(URL_REGEX_QNA_REVIEW)
        add(URL_REGEX_QNA_QUESTION)
        add(URL_REGEX_CONVERSATION_MAIN)
    }

    private val inPlaceOpenLinkSet = LinkedHashSet<String>().apply {
        add(URL_REGEX_TEAM)
        add(URL_REGEX_SUPPORT)
        add(URL_REGEX_WALLPAPERS)
        add(URL_REGEX_GLOSSARY)
        add(URL_REGEX_ADS)
        add(URL_REGEX_CONTACT)
        add(URL_REGEX_GUIDELINES)
        add(URL_REGEX_HELP)
        add(URL_REGEX_ANDROID)
        add(URL_REGEX_PRIVACY)

        add(URL_REGEX_PLACES)
        add(URL_REGEX_COUNTRIES)
        add(URL_REGEX_CITIES)
        add(URL_REGEX_ADMINISTRATIVE_AREAS)

        add(URL_REGEX_SETTINGS)
        add(URL_REGEX_SEARCH)
        add(URL_REGEX_QNA)
        add(URL_REGEX_NOTIFICATIONS)
        add(URL_REGEX_REQUESTS)
        add(URL_REGEX_CONVERSATION)

        add(URL_REGEX_LEGALESE)

        add(URL_REGEX_LOGIN_PASSWORD_SENT)
        add(URL_REGEX_PASSWORD_INCORRECT_EMAIL)
        add(URL_REGEX_PASSWORD_INCORRECT_PHONE)
        add(URL_REGEX_PASSWORD_VERIFY_PHONE)
        add(URL_REGEX_PASSWORD_EDIT)
    }

    private val inPlaceOpenWithNoHistoryLinkSet = LinkedHashSet<String>().apply {
        add(URL_REGEX_PASSWORD_NEW_EMAIL)
        add(URL_REGEX_PASSWORD_NEW_MOBILE)

        add(URL_REGEX_TEAM_MAIN)
        add(URL_REGEX_SUPPORT_MAIN)
        add(URL_REGEX_WALLPAPERS_MAIN)
        add(URL_REGEX_GLOSSARY_MAIN)
        add(URL_REGEX_ADS_MAIN)
        add(URL_REGEX_CONTACT_MAIN)
        add(URL_REGEX_GUIDELINES_MAIN)
        add(URL_REGEX_HELP_MAIN)
        add(URL_REGEX_ANDROID_MAIN)
        add(URL_REGEX_PLACES_MAIN)
        add(URL_REGEX_NOTIFICATIONS_MAIN)
        add(URL_REGEX_REQUESTS_MAIN)
        add(URL_REGEX_INBOX_MAIN)
        add(URL_REGEX_INBOX_ALL)
        add(URL_REGEX_INBOX_ARCHIVED)

        add(URL_REGEX_SETTINGS_ACCOUNT)
        add(URL_REGEX_SETTINGS_PRIVACY)
        add(URL_REGEX_SETTINGS_NOTIFICATIONS)
        add(URL_REGEX_SETTINGS_BLOCKED)

        add(URL_REGEX_SEARCH_MAIN)
        add(URL_REGEX_SEARCH_KINKSTERS)
        add(URL_REGEX_SEARCH_PICTURES)
        add(URL_REGEX_SEARCH_WRITINGS)
        add(URL_REGEX_SEARCH_VIDEOS)
        add(URL_REGEX_SEARCH_GROUPS)
        add(URL_REGEX_SEARCH_DISCUSSIONS)
        add(URL_REGEX_SEARCH_EVENTS)
        add(URL_REGEX_SEARCH_FETISHES)
        add(URL_REGEX_SEARCH_PLACES)

        add(URL_REGEX_QNA_MAIN)
        add(URL_REGEX_QNA_FETLIFE)
        add(URL_REGEX_QNA_TAGS)
        add(URL_REGEX_QNA_POPULAR)
        add(URL_REGEX_QNA_UNANSWERED)
        add(URL_REGEX_QNA_MINE)
    }

    private val nativeNavigationMap = LinkedHashMap<String, String>().apply {
        put(URL_REGEX_USER_PROFILE, ProfileActivity::class.java.simpleName)
        put(URL_REGEX_EVENT, EventActivity::class.java.simpleName)
        put(URL_REGEX_GROUP, GroupActivity::class.java.simpleName)
        put(URL_REGEX_GROUP_POST, GroupMessagesActivity::class.java.simpleName)
        put(URL_REGEX_USER_PICTURE, ImageViewerWrapper::class.java.simpleName)
        put(URL_REGEX_USER_VIDEO, Video::class.java.simpleName)
        put(URL_REGEX_LOGIN, NATIVE_NAVIGATION_LOGIN)
        put(URL_REGEX_HOME, NATIVE_NAVIGATION_HOME)
    }

    private val fabLinkMap = LinkedHashMap<String, String>().apply {
        put(URL_REGEX_QNA_MAIN, URL_QNA_NEW)
    }

    fun getOptionsMenuNavigationList(url: String?): List<Int>? {
        if (url == null) {
            return null
        }
        for ((key, value) in optionsMenuMap) {
            if (key.toRegex().matches(url)) {
                return value
            }
        }
        return null
    }

    fun getOptionMenuNavigationUrl(itemId: Int): String? {
        return optionsMenuUrlMap[itemId]
    }

    fun getTitle(url: String?): Int? {
        if (url == null) {
            return null
        }
        for ((key, value) in titleMap) {
            if (key.toRegex().matches(url)) {
                return value
            }
        }
        return null
    }

    fun navigate(request: WebResourceRequest, webView: WebView?, activity: BaseActivity?): Boolean {
        return navigate(request.url, webView, activity, request)
    }

    fun navigate(targetUri: Uri?, webView: WebView?, activity: BaseActivity?, request: WebResourceRequest? = null): Boolean {

        targetUri ?: return false
        val context = webView?.context ?: return false
        val currentUrl = webView.url

        webView.tag = false

        if (targetUri.toString() == currentUrl) {
            return true
        }

        if (!isFetLifeLink(targetUri)) {
            targetUri.openInBrowser()
            return true
        }

        if (isDownloadLink(targetUri)) {
            openInCustomTab(targetUri, context)
            return true
        }

        if (handleNativeSupportedLink(targetUri, currentUrl, activity, request)) {
            return true
        }

        if (isParent(targetUri, webView)) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                activity?.finish()
            }
            return true
        }

        if (openAsNewWebViewFlow(targetUri, currentUrl)) {
            FetLifeWebViewActivity.startActivity(webView.context, targetUri.toString(), false, null, false, null, null)
            return true
        }

        if (openInPlaceWithNoHistory(targetUri, currentUrl)) {
            //TODO(WEBAPP): keep track own back track list, add or not to history
            webView.clearHistory()
            webView.tag = true
            return false
        }

        if (openInPlace(targetUri)) {
            return false
        }

        //if (not supported as webview yet)
        openInCustomTab(targetUri, context)
        return true
    }

    private fun isParent(targetUri: Uri, webView: WebView): Boolean {
        var currentUrl = webView.url
        //workaround to deal with synonyms
        var targetUrl = targetUri.toString()
        currentUrl = currentUrl.replace("places", "p")
        targetUrl = targetUrl.replace("places", "p")

        for ((uriRegex, parentRegex) in parentMap) {
            if (uriRegex.toRegex().matches(currentUrl) && parentRegex.toRegex().matches(targetUrl)) {
                return true
            }
        }

        //workaround to deal with inbox
        if (URL_REGEX_INBOX_MAIN.toRegex().matches(currentUrl) || URL_REGEX_INBOX_MAIN.toRegex().matches(targetUrl)) {
            return false
        }

        if (currentUrl.startsWith(targetUrl)) {
            return webView.canGoBack()
        }

        return false
    }

    private fun openInCustomTab(uri: Uri, context: Context) {
        val builder = CustomTabsIntent.Builder(FetLifeApplication.getInstance().customTabsSession).setToolbarColor(ColorUtil.retrieverColor(context, R.color.toolbar_chrome_custom_tab))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, uri)
    }


    fun getFabLink(url: String?): String? {
        url ?: return null
        for ((uriRegex, fabLink) in fabLinkMap) {
            if (uriRegex.toRegex().matches(url)) {
                return fabLink
            }
        }
        return null
    }

    private fun isFetLifeLink(uri: Uri): Boolean {
        return URL_REGEX_INTERNAL_LINK.toRegex().matches(uri.toString())
    }

    private fun isDownloadLink(uri: Uri): Boolean {
        return URL_REGEX_DOWNLOAD_LINK.toRegex().containsMatchIn(uri.toString())
    }

    private fun handleNativeSupportedLink(uri: Uri, currentUrl: String, activity: BaseActivity?, request: WebResourceRequest?): Boolean {
        activity ?: return false

        var nativeClassIdentifier: String? = null
        for ((uriRegex, classIdentifier) in nativeNavigationMap) {
            if (uriRegex.toRegex().containsMatchIn(uri.toString())) {
                nativeClassIdentifier = classIdentifier
                break
            }
        }
        if (nativeClassIdentifier == null) {
            return false
        }

        val apiIdsParam = uri.getQueryParameter(QUERY_PARAM_API_IDS)
        val apiIds = apiIdsParam?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                ?: arrayOfNulls<String>(0)

        return when (nativeClassIdentifier) {
            ProfileActivity::class.java.simpleName -> {
                val memberId = apiIds.getOrNull(0) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                val fromNewConversation = URL_REGEX_CONVERSATION_NEW.toRegex().matches(currentUrl)
                val toastMessage = if (fromNewConversation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && request?.isRedirect == true) {
                    val memberName = Member.loadMember(memberId)?.nickname
                    if (memberName != null) activity?.getString(R.string.toast_message_sent_successfully_to_user, memberName) else activity?.getString(R.string.toast_message_sent_successfully)
                } else null
                ProfileActivity.startActivity(activity, memberId, fromNewConversation, toastMessage)
                true
            }
            EventActivity::class.java.simpleName -> {
                val eventId = apiIds.getOrNull(0) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                EventActivity.startActivity(activity, eventId)
                true
            }
            GroupActivity::class.java.simpleName -> {
                val groupId = apiIds.getOrNull(0) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                val group = Group.loadGroup(groupId)
                GroupActivity.startActivity(activity, groupId, group?.name, false)
                true
            }
            GroupMessagesActivity::class.java.simpleName -> {
                val groupId = apiIds.getOrNull(0) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                val groupDiscussionId = apiIds.getOrNull(1) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                val groupPost = GroupPost.loadGroupPost(groupDiscussionId)
                GroupMessagesActivity.startActivity(activity, groupId, groupDiscussionId, groupPost?.title, groupPost?.avatarLink, false)
                true
            }
            ImageViewerWrapper::class.java.simpleName -> {
                val memberId = apiIds.getOrNull(0) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                val pictureId = apiIds.getOrNull(1) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                FetLifeApiIntentService.startApiCall(activity, FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURE, memberId, pictureId, currentUrl)
                true
            }
            Video::class.java.simpleName -> {
                val memberId = apiIds.getOrNull(0) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                val videoId = apiIds.getOrNull(1) ?: SERVER_ID_PREFIX + uri.lastPathSegment
                FetLifeApiIntentService.startApiCall(activity, FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEO, memberId, videoId, currentUrl)
                true
            }
            NATIVE_NAVIGATION_LOGIN -> {
                val toastMessage = if (URL_REGEX_PASSWORD_EDIT.toRegex().matches(currentUrl)) {
                    activity?.getString(R.string.toast_password_reset_successfull);
                } else null
                FetLifeWebViewActivity.startLogin(FetLifeApplication.getInstance(), toastMessage)
                true
            }
            NATIVE_NAVIGATION_HOME -> {
                val cookies = CookieManager.getInstance().getCookie(uri.toString())
                val accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJmZXRsaWZlIiwiaWF0IjoxNTU2NzIzNzQyLCJybmQiOiIzMWNiYzQwODRhZjQiLCJ1c2VyIjp7ImlkIjoiMTA2ZDNhYjQiLCJuaWNrIjoiRHJlYW1saXRlIn19.fctcn_h03jLrEB3D7d_R5q1jcOVEh2dCLtjcRAnnnmU"
                val refreshToken = ""
                val rememberMe = false
                FetLifeApiIntentService.startApiCall(activity,FetLifeApiIntentService.ACTION_APICALL_FINALIZE_LOGIN, accessToken, refreshToken, rememberMe.toString())
                if (BuildConfig.DEBUG) {
                    Log.d("Cookies",cookies.toString())
                }
                activity.showProgress()
                true
            }
            else -> false
        }
    }

    private fun openAsNewWebViewFlow(uri: Uri, currentUrl: String): Boolean {
        for (uriRegex in newWebViewFlowLinkSetSet) {
            if (uriRegex.toRegex().matches(uri.toString()) && !uriRegex.toRegex().matches(currentUrl)) {
                return true
            }
        }
        return false
    }

    private fun openInPlaceWithNoHistory(uri: Uri, currentUrl: String): Boolean {
        //TODO(WEBAPP): FIND BETTER THAN THIS WORKAROUND
        if (URL_REGEX_PRIVACY_MAIN.toRegex().matches(uri.toString())) {
            return false
        }
        for (uriRegex in inPlaceOpenWithNoHistoryLinkSet) {
            if (uriRegex.toRegex().matches(uri.toString()) && !uriRegex.toRegex().matches(currentUrl)) {
                return true
            }
        }
        return false
    }

    private fun openInPlace(uri: Uri): Boolean {
        for (uriRegex in inPlaceOpenLinkSet) {
            if (uriRegex.toRegex().matches(uri.toString())) {
                return true
            }
        }
        return false
    }

    fun showPicture(context: Context?, mediaId: String) {
        if (context == null) return
        val picture = Picture.loadPicture(mediaId)
        val inflater = LayoutInflater.from(context)
        val overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null)
        val onPictureOverlayClickListener = object : PictureUtil.OnPictureOverlayClickListener {
            override fun onMemberClick(member: Member) {
                member.mergeSave()
                ProfileActivity.startActivity(context, member.id)
            }

            override fun onVisitPicture(picture: Picture, url: String) {
                UrlUtil.openUrl(context, url, true, false)
            }

            override fun onSharePicture(picture: Picture, url: String) {
                if (picture.isOnShareList) {
                    Picture.unsharePicture(picture)
                } else {
                    Picture.sharePicture(picture)
                }
            }
        }
        val pictures = ArrayList<Picture>()
        pictures.add(picture)
        FetLifeApplication.getInstance().imageViewerWrapper.show(context, pictures, 0)
    }

    fun showVideo(context: Context?, mediaId: String) {
        if (context == null) return
        val video = Video.loadVideo(mediaId)
        val uri = Uri.parse(video!!.videoUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setDataAndType(uri, "video/*")
        context.startActivity(intent)
    }


}