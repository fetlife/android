package com.bitlove.fetlife.webapp.navigation

//import android.content.pm.PackageManager
//import android.util.Base64
//import android.webkit.CookieManager
//import com.bitlove.fetlife.BuildConfig
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.browser.customtabs.CustomTabsIntent
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.inbound.onesignal.notification.OneSignalNotification
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.*
import com.bitlove.fetlife.model.service.FetLifeApiIntentService
import com.bitlove.fetlife.util.ColorUtil
import com.bitlove.fetlife.view.screen.BaseActivity
import com.bitlove.fetlife.view.screen.resource.EventActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupMessagesActivity
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity
import com.bitlove.fetlife.view.screen.standalone.LoginActivity
import com.bitlove.fetlife.view.widget.ImageViewerWrapper
import com.bitlove.fetlife.webapp.kotlin.openInBrowser
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

//import org.json.JSONObject
//import okhttp3.*
//import java.io.ByteArrayInputStream
//import java.io.IOException
//import java.security.cert.CertificateFactory
//import java.security.cert.X509Certificate
//import javax.crypto.Cipher
//import javax.crypto.spec.IvParameterSpec
//import javax.crypto.spec.SecretKeySpec


class WebAppNavigation {

    companion object {

        const val WEBAPP_BASE_URL = "https://fetlife.com"

        //Base constants
        internal const val WEB_TITLE_SEPARATOR = " |"
        internal const val WEB_COUNTER_SEPARATOR = ") "
        internal const val WEB_EXTRA_SEPARATOR = " -"

        //Navigation selector constants
        private const val NATIVE_NAVIGATION_LOGIN = "NATIVE_NAVIGATION_LOGIN"
        private const val NATIVE_NAVIGATION_HOME = "NATIVE_NAVIGATION_HOME"

        //NOTE: Temporarily duplicated with native code area TODO(WEBAPP): decoupling
        private const val QUERY_PARAM_API_IDS = "api_ids"
        private const val SERVER_ID_PREFIX = "SERVER_ID_PREFIX:"

        //*** Full Urls ***

//        const val URL_OAUTH_LOGIN = "$WEBAPP_BASE_URL/api/oauth/authorize?client_id=${BuildConfig.CLIENT_ID}&amp;redirect_uri=${BuildConfig.REDIRECT_URL}&amp;response_type=code"
//        const val URL_OAUTH_TOKEN = "https://fetlife.com/api/oauth/token"
//        const val URL_OAUTH_CALLBACK = "https://fetlife.com/api/oauth/authorize/native"

        private const val URL_QNA_NEW = "$WEBAPP_BASE_URL/q/new"
        private const val URL_INBOX_MAIN = "$WEBAPP_BASE_URL/inbox"
        private const val URL_INBOX_ALL = "$WEBAPP_BASE_URL/inbox/all"
        private const val URL_INBOX_ARCHIVED = "$WEBAPP_BASE_URL/inbox/archived"


        //*** Url Params & Values ***
//        private const val PARAM_POST_LOGIN_CODE = "code"
//        private const val PARAM_POST_LOGIN_CLIENT_ID = "client_id"
//        private const val PARAM_POST_LOGIN_CLIENT_SECRET = "client_secret"
//        private const val PARAM_POST_LOGIN_REDIRECT_URI = "redirect_uri"
//        private const val PARAM_POST_LOGIN_GRANT_TYPE = "grant_type"
//        private const val VALUE_POST_LOGIN_GRANT_TYPE = "authorization_code"
//
//        private const val PARAM_QUERY_LOGIN_CODE = "code"
//
//        private const val PARAM_LOGIN_RESPONSE_ACCESS_TOKEN = "access_token"
//        private const val PARAM_LOGIN_RESPONSE_REFRESH_TOKEN = "refresh_token"
//
//        private const val COOKIE_REMEMBER_USER_TOKEN = "remember_user_token"

        //*** Url Regular Expressions ***

        //** Generic Url Regexps **
        private const val REGEX_BASE_URL = "https:\\/\\/(staging\\.)?fetlife\\.com"
        private const val URL_REGEX_INTERNAL_URL = "^$REGEX_BASE_URL.*\$"
        private const val URL_REGEX_DOWNLOAD_URL = "^$REGEX_BASE_URL\\/.*\\/download[^\\/]*\$"

        //** Top level Url Regexps **

        //* Strict Url Regexps - No '/' allowed after main object reference

        private const val URL_REGEX_HOME = "^$REGEX_BASE_URL\\/home[^\\/]*\$"
        private const val URL_REGEX_SEARCH_MAIN = "^$REGEX_BASE_URL\\/search[^\\/]*\$"
        private const val URL_REGEX_EVENT_MAIN = "^$REGEX_BASE_URL\\/events\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_GROUP_MAIN = "^$REGEX_BASE_URL\\/groups\\/(\\w+)[^\\/]*\$"
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
        //New password Url Regexps
        private const val URL_REGEX_PASSWORD_INCORRECT_EMAIL = "^$REGEX_BASE_URL\\/users\\/password[^\\/]*\$"
        private const val URL_REGEX_PASSWORD_INCORRECT_PHONE = "^$REGEX_BASE_URL\\/users\\/password\\/via_phone[^\\/]*\$"
        private const val URL_REGEX_PASSWORD_VERIFY_PHONE = "^$REGEX_BASE_URL\\/users\\/password\\/verify_phone[^\\/]*\$"
        private const val URL_REGEX_PASSWORD_EDIT = "^$REGEX_BASE_URL\\/users\\/password\\/edit[^\\/]*\$"
        private const val URL_REGEX_LOGIN_PASSWORD_SENT = "^$REGEX_BASE_URL\\/sent_login_information[^\\/]*\$"
        //QnA Url Regexps
        private const val URL_REGEX_QNA_MAIN = "^$REGEX_BASE_URL\\/q[^\\/\\?]*\$"
        private const val URL_REGEX_QNA_REVIEW = "^$REGEX_BASE_URL\\/q\\/review\\/?[^\\/]*\$"
        //Inbox Url Regexps
        private const val URL_REGEX_INBOX_MAIN = "^$REGEX_BASE_URL\\/inbox[^\\/]*\$"
        private const val URL_REGEX_INBOX_ALL = "^$REGEX_BASE_URL\\/inbox\\/all[^\\/]*\$"
        private const val URL_REGEX_INBOX_ARCHIVED = "^$REGEX_BASE_URL\\/inbox\\/archived[^\\/]*\$"
        //Places Url Regexps
        //WARNING: Also matches with privacy (exception added on code level)
        // TODO(WEBAPP): find better
        private const val URL_REGEX_PLACES_MAIN = "^$REGEX_BASE_URL\\/(p|places)[^\\/]*\$"
        //Login Url Regexps
        private const val URL_REGEX_LOGIN_MAIN = "^$REGEX_BASE_URL\\/(users\\/sign_in|login)\\/?[^\\/_]*\$"

        //* Group Url Regexps - '/' is allowed after main object reference *

        private const val URL_REGEX_QNA = "^$REGEX_BASE_URL\\/q\\/?.*\$"
        private const val URL_REGEX_SETTINGS = "^$REGEX_BASE_URL\\/settings\\/?.*\$"
        private const val URL_REGEX_SEARCH = "^$REGEX_BASE_URL\\/search\\/?.*\$"
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
        private const val URL_REGEX_NOTIFICATIONS = "^$REGEX_BASE_URL\\/notifications\\/?.*\$"
        private const val URL_REGEX_REQUESTS = "^$REGEX_BASE_URL\\/requests\\/?.*\$"
        private const val URL_REGEX_INBOX = "^$REGEX_BASE_URL\\/inbox\\/?.*\$"
        private const val URL_REGEX_CONVERSATION = "^$REGEX_BASE_URL\\/conversations\\/(\\w+).*\$"
        private const val URL_REGEX_CONVERSATION_NEW = "^$REGEX_BASE_URL\\/conversations\\/new\\?with=[0-9]+.*\$"
        private const val URL_REGEX_PASSWORD_NEW_EMAIL = "^$REGEX_BASE_URL\\/users\\/password\\/new\$"
        private const val URL_REGEX_PASSWORD_NEW_MOBILE = "^$REGEX_BASE_URL\\/users\\/password\\/new_.*\$"


        //** Sub level Url Regexps **

        private const val URL_REGEX_CONVERSATION_MAIN = "^$REGEX_BASE_URL\\/conversations\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_GROUP_POST_MAIN = "^$REGEX_BASE_URL\\/groups\\/(\\w+)\\/group_posts\\/(\\w+)[^\\/]*\$"
        //Profile Url regexps
        private const val URL_REGEX_USER_PROFILE_MAIN = "^$REGEX_BASE_URL\\/users\\/([0-9]+)[^\\/]*\$"
        private const val URL_REGEX_USER_PICTURE_MAIN = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/pictures\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_USER_VIDEO_MAIN = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/videos\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_USER_POST_MAIN = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/posts\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_USER_POST = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/posts\\/(\\w+).]*\$"
        private const val URL_REGEX_USER_STATUS_MAIN = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/statuses\\/(\\w+)[^\\/]*\$"
        private const val URL_REGEX_USER_STATUS = "^$REGEX_BASE_URL\\/users\\/(\\w+)\\/statuses\\/(\\w+).]*\$"
        //Search Url Regexps
        private const val URL_REGEX_SEARCH_KINKSTERS = "^$REGEX_BASE_URL\\/search\\/kinksters\\/?.*\$"
        private const val URL_REGEX_SEARCH_PICTURES = "^$REGEX_BASE_URL\\/search\\/pictures\\/?.*\$"
        private const val URL_REGEX_SEARCH_WRITINGS = "^$REGEX_BASE_URL\\/search\\/writings\\/?.*\$"
        private const val URL_REGEX_SEARCH_VIDEOS = "^$REGEX_BASE_URL\\/search\\/videos\\/?.*\$"
        private const val URL_REGEX_SEARCH_GROUPS = "^$REGEX_BASE_URL\\/search\\/groups\\/?.*\$"
        private const val URL_REGEX_SEARCH_DISCUSSIONS = "^$REGEX_BASE_URL\\/search\\/discussions\\/?.*\$"
        private const val URL_REGEX_SEARCH_EVENTS = "^$REGEX_BASE_URL\\/search\\/events\\/?.*\$"
        private const val URL_REGEX_SEARCH_FETISHES = "^$REGEX_BASE_URL\\/search\\/fetishes\\/?.*\$"
        private const val URL_REGEX_SEARCH_PLACES = "^$REGEX_BASE_URL\\/search\\/places\\/?.*\$"
        //Settings Url Regexps
        private const val URL_REGEX_SETTINGS_ACCOUNT = "^$REGEX_BASE_URL\\/settings\\/account\\/?.*\$"
        private const val URL_REGEX_SETTINGS_PRIVACY = "^$REGEX_BASE_URL\\/settings\\/privacy\\/?.*\$"
        private const val URL_REGEX_SETTINGS_BLOCKED = "^$REGEX_BASE_URL\\/settings\\/blocked\\/?.*\$"
        private const val URL_REGEX_SETTINGS_NOTIFICATIONS = "^$REGEX_BASE_URL\\/settings\\/notifications\\/?.*\$"
        //QnA Url Regexps
        private const val URL_REGEX_QNA_FETLIFE = "^$REGEX_BASE_URL\\/q\\/fetlife.*\$"
        private const val URL_REGEX_QNA_TAGS = "^$REGEX_BASE_URL\\/q\\/tags.*\$"
        private const val URL_REGEX_QNA_POPULAR = "^$REGEX_BASE_URL\\/q(\\/fetlife)?\\?filter=popular.*\$"
        private const val URL_REGEX_QNA_UNANSWERED = "^$REGEX_BASE_URL\\/q(\\/fetlife)?\\?filter=unanswered.*\$"
        private const val URL_REGEX_QNA_MINE = "^$REGEX_BASE_URL\\/q(\\/fetlife)?\\?filter=mine.*\$"
        //Places Url Regexps
        private const val URL_REGEX_PLACES = "^$REGEX_BASE_URL\\/(p|places)\\/?.*\$"
        private const val URL_REGEX_CITIES = "^$REGEX_BASE_URL\\/cities\\/?.*\$"
        private const val URL_REGEX_COUNTRIES = "^$REGEX_BASE_URL\\/countries\\/?.*\$"
        private const val URL_REGEX_ADMINISTRATIVE_AREAS = "^$REGEX_BASE_URL\\/administrative_areas\\/?.*\$"
        //QnA Url Regexps
        private const val URL_REGEX_QNA_QUESTION = "^$REGEX_BASE_URL\\/q\\/.*\\/.*\$"
        private const val URL_REGEX_LOGIN = "^$REGEX_BASE_URL\\/(users\\/sign_in|login|sign_in_five_strike)\\/?.*\$"
//        private const val URL_REGEX_LOGIN_OAUTH = "^$REGEX_BASE_URL\\/api\\/oauth\\/?.*\$"

    }

    //Native page title override
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

    //To describe logical parent connection between the current Url and the target Url
    private val parentMap = LinkedHashMap<String, String>().apply {
        put(URL_QNA_NEW, URL_REGEX_QNA_MAIN)
        put(URL_REGEX_CONVERSATION, URL_REGEX_INBOX)
    }

    //Option menu Choices for the given Url
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

    //Option menu Choices target Urls the given option menu choice
    private val optionsMenuUrlMap = LinkedHashMap<Int, String>().apply {
        put(R.string.menu_options_inbox_default, URL_INBOX_MAIN)
        put(R.string.menu_options_inbox_all, URL_INBOX_ALL)
        put(R.string.menu_options_inbox_archived, URL_INBOX_ARCHIVED)
    }

    //Action links to Floating Action Buttons on the screen with the current Url
    private val fabUrlMap = LinkedHashMap<String, String>().apply {
        put(URL_REGEX_QNA_MAIN, URL_QNA_NEW)
    }

    //Urls that are accessible without having the user signed in
    private val notResourceUrlSet = LinkedHashSet<String>().apply {
        add(URL_REGEX_LOGIN_MAIN)
//        add(URL_REGEX_LOGIN_OAUTH)
        add(URL_REGEX_LOGIN_PASSWORD_SENT)
        add(URL_REGEX_PASSWORD_NEW_EMAIL)
        add(URL_REGEX_PASSWORD_NEW_MOBILE)
        add(URL_REGEX_PASSWORD_INCORRECT_EMAIL)
        add(URL_REGEX_PASSWORD_INCORRECT_PHONE)
        add(URL_REGEX_PASSWORD_VERIFY_PHONE)
        add(URL_REGEX_PASSWORD_EDIT)
    }

    //Urls that can be handled as launchUrl directly from notifications
    private val openFromNotificationUrlSet = LinkedHashSet<String>().apply {
        add(URL_REGEX_USER_STATUS)
        add(URL_REGEX_USER_POST)
    }

    //Target Urls to be opened in as a new webview flow
    private val newWebViewFlowUrlSet = LinkedHashSet<String>().apply {
        add(URL_REGEX_USER_STATUS)
        add(URL_REGEX_USER_POST)
        add(URL_REGEX_USER_STATUS)
        add(URL_REGEX_QNA_REVIEW)
        add(URL_REGEX_QNA_QUESTION)
        add(URL_REGEX_CONVERSATION_MAIN)
    }

    //Urls to be opened naturally (in place)
    private val inPlaceOpenUrlSet = LinkedHashSet<String>().apply {
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

        add(URL_REGEX_LOGIN)
    }

    //Urls to be opened in the current flow, but with clearning backward navigation
    private val inPlaceOpenWithNoHistoryUrlSet = LinkedHashSet<String>().apply {

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

    //Urls to be handled with a native screen
    private val nativeNavigationMap = LinkedHashMap<String, String>().apply {
        put(URL_REGEX_USER_PROFILE_MAIN, ProfileActivity::class.java.simpleName)
        put(URL_REGEX_EVENT_MAIN, EventActivity::class.java.simpleName)
        put(URL_REGEX_GROUP_MAIN, GroupActivity::class.java.simpleName)
        put(URL_REGEX_GROUP_POST_MAIN, GroupMessagesActivity::class.java.simpleName)
        put(URL_REGEX_USER_PICTURE_MAIN, ImageViewerWrapper::class.java.simpleName)
        put(URL_REGEX_USER_VIDEO_MAIN, Video::class.java.simpleName)
        put(URL_REGEX_LOGIN_MAIN, NATIVE_NAVIGATION_LOGIN)
        put(URL_REGEX_HOME, NATIVE_NAVIGATION_HOME)
    }

    fun getTitle(url: String?): Int? {
        return checkRegexpMap(url, titleMap)
    }

    fun isResourceUrl(url: String?): Boolean {
        url ?: return false
        return !checkRegexpSet(url, notResourceUrlSet)
    }

    fun openAsWebviewFromNotification(url: String?): Boolean {
        url ?: return false
        return !checkRegexpSet(url, openFromNotificationUrlSet)
    }

    fun getOptionsMenuNavigationList(url: String?): List<Int>? {
        return checkRegexpMap(url, optionsMenuMap)
    }

    fun getOptionMenuNavigationUrl(itemId: Int): String? {
        return optionsMenuUrlMap[itemId]
    }

    fun getFabLink(url: String?): String? {
        return checkRegexpMap(url, fabUrlMap)
    }

    fun showPicture(context: Context?, mediaId: String) {
        context ?: return
        val picture = Picture.loadPicture(mediaId)
        FetLifeApplication.getInstance().imageViewerWrapper.show(context, ArrayList<Picture>().apply { add(picture) }, 0)
    }

    fun showVideo(context: Context?, mediaId: String) {
        if (context == null) {
            return
        }
        val video = Video.loadVideo(mediaId) ?: return
        val uri = Uri.parse(video.videoUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setDataAndType(uri, "video/*")
        context.startActivity(intent)
    }

    fun getOpenFromNotificationIntent(oneSignalNotification: OneSignalNotification, notificationId: Int): PendingIntent? {
        val launchUrl = oneSignalNotification.launchUrl
        launchUrl ?: return null
        val launchUri = Uri.parse(launchUrl)
        val fetLifeApplication = FetLifeApplication.getInstance()

        val baseIntent = FetLifeWebViewActivity.createIntent(fetLifeApplication, WebAppNavigation.WEBAPP_BASE_URL + "/inbox", true, R.id.navigation_bottom_inbox, true)

        if (openAsWebviewFromNotification(launchUrl)) {
            val contentIntent = FetLifeWebViewActivity.createIntent(fetLifeApplication, oneSignalNotification.launchUrl!!, false, null, false).apply {
                putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, oneSignalNotification.notificationType)
                putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, oneSignalNotification.mergeId)
            }
            return PendingIntent.getActivities(fetLifeApplication, notificationId, arrayOf(baseIntent, contentIntent), PendingIntent.FLAG_CANCEL_CURRENT)
        }

        var nativeClassIdentifier: String? = null
        for ((uriRegex, classIdentifier) in nativeNavigationMap) {
            if (uriRegex.toRegex().containsMatchIn(launchUrl)) {
                nativeClassIdentifier = classIdentifier
                break
            }
        }

        if (nativeClassIdentifier == null) {
            return null
        }

        val apiIdsParam = launchUri.getQueryParameter(QUERY_PARAM_API_IDS)
        val apiIds = apiIdsParam?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                ?: arrayOfNulls<String>(0)

        return when (nativeClassIdentifier) {
            ProfileActivity::class.java.simpleName -> {
                val memberId = apiIds.getOrNull(0) ?: SERVER_ID_PREFIX + launchUri.lastPathSegment
                val contentIntent = ProfileActivity.createIntent(fetLifeApplication, memberId, false, null, false).apply {
                    putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, oneSignalNotification.notificationType)
                    putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, oneSignalNotification.mergeId)
                }
                return PendingIntent.getActivities(fetLifeApplication, notificationId, arrayOf(baseIntent, contentIntent), PendingIntent.FLAG_CANCEL_CURRENT)
            }
            else -> null
        }
    }

    fun navigate(targetUri: Uri?, webView: WebView?, activity: Activity?, request: WebResourceRequest? = null): Boolean {

        targetUri ?: return false
        val context = webView?.context ?: return false
        val currentUrl = webView.url

        webView.tag = false

        if (!isFetLifeUrl(targetUri)) {
            targetUri.openInBrowser()
            return true
        }

//        if (isLoginRedirect(targetUri)) {
//            val code = targetUri.getQueryParameter(PARAM_QUERY_LOGIN_CODE) ?: return false
//            getAccessToken(activity, code)
//            return true
//        }

//        if (isOauthUrl(targetUri)) {
//            return false
//        }

        if (targetUri.toString() == currentUrl) {
            return true
        }

        if (isDownloadUrl(targetUri)) {
            openInCustomTab(targetUri, context)
            return true
        }

        if (handleNativeSupportedUrl(targetUri, currentUrl, activity, request)) {
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
            FetLifeWebViewActivity.startActivity(webView.context, targetUri.toString(), false, null, false, null)
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

//    private fun isOauthUrl(targetUri: Uri): Boolean {
//        return URL_REGEX_LOGIN_OAUTH.toRegex().matches(targetUri.toString())
//    }

//    private fun getAccessToken(activity: BaseActivity?, code: String) {
//
//        activity?.showProgress()
//
//        doAsync {
//            val client = OkHttpClient().newBuilder()
//                    .followRedirects(true)
//                    .followSslRedirects(true)
//                    .build()
//            val parameters = HashMap<String, String>().apply {
//                put(PARAM_POST_LOGIN_CODE, code)
//                put(PARAM_POST_LOGIN_CLIENT_ID, BuildConfig.CLIENT_ID)
//                put(PARAM_POST_LOGIN_CLIENT_SECRET, getClientSecret())
//                put(PARAM_POST_LOGIN_REDIRECT_URI, BuildConfig.REDIRECT_URL)
//                put(PARAM_POST_LOGIN_GRANT_TYPE, VALUE_POST_LOGIN_GRANT_TYPE)
//            }
//
//            val builder = FormBody.Builder()
//            val it = parameters.entries.iterator()
//            while (it.hasNext()) {
//                val pair = it.next() as Map.Entry<*, *>
//                builder.add(pair.key.toString(), pair.value.toString())
//            }
//
//            val formBody = builder.build()
//            val request = Request.Builder()
//                    .header("CONTENT_TYPE", "application/x-www-form-urlencoded")
//                    .url(URL_OAUTH_TOKEN)
//                    .post(formBody)
//                    .build()
//
//
//            val call = client.newCall(request)
//            call.enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val responseJson = JSONObject(response.body()?.string())
//                    val accessToken = responseJson.getString(PARAM_LOGIN_RESPONSE_ACCESS_TOKEN)
//                    val refreshToken = responseJson.getString(PARAM_LOGIN_RESPONSE_REFRESH_TOKEN)
//
//                    val cookies = CookieManager.getInstance().getCookie(URL_OAUTH_TOKEN)
//                    val userTokenIndex = cookies.indexOf(COOKIE_REMEMBER_USER_TOKEN)
//                    val rememberMe = userTokenIndex >= 0
//                    FetLifeApiIntentService.startApiCall(activity, FetLifeApiIntentService.ACTION_APICALL_FINALIZE_LOGIN, accessToken, refreshToken, rememberMe.toString())
//                }
//            })
//        }
//    }
//
//    private fun isLoginRedirect(targetUri: Uri): Boolean {
//        return targetUri.toString().startsWith(URL_OAUTH_CALLBACK)
//    }

    private fun isParent(targetUri: Uri, webView: WebView): Boolean {
        var currentUrl = webView.url

        //Workaround to deal with synonyms - Starts Here
        var targetUrl = targetUri.toString()
        currentUrl = currentUrl.replace("places", "p")
        targetUrl = targetUrl.replace("places", "p")
        //Workaround to deal with synonyms - Ends Here

        for ((uriRegex, parentRegex) in parentMap) {
            if (uriRegex.toRegex().matches(currentUrl) && parentRegex.toRegex().matches(targetUrl)) {
                return true
            }
        }

        //Workaround to deal with Inbox as prefix - Starts Here
        if (URL_REGEX_INBOX_MAIN.toRegex().matches(currentUrl) || URL_REGEX_INBOX_MAIN.toRegex().matches(targetUrl)) {
            return false
        }
        //Workaround to deal with Inbox as prefix - Ends Here

        if (currentUrl.startsWith(targetUrl)) {
            return webView.canGoBack()
        }

        return false
    }

    private fun isFetLifeUrl(uri: Uri): Boolean {
        return URL_REGEX_INTERNAL_URL.toRegex().matches(uri.toString())
    }

    private fun isDownloadUrl(uri: Uri): Boolean {
        return URL_REGEX_DOWNLOAD_URL.toRegex().containsMatchIn(uri.toString())
    }

    private fun handleNativeSupportedUrl(uri: Uri, currentUrl: String, activity: Activity?, request: WebResourceRequest?): Boolean {
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
                    if (memberName != null) activity.getString(R.string.toast_message_sent_successfully_to_user, memberName) else activity.getString(R.string.toast_message_sent_successfully)
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
//                if (URL_REGEX_LOGIN_OAUTH.toRegex().matches(currentUrl) || URL_REGEX_LOGIN_MAIN.toRegex().matches(currentUrl)) {
//                    return false
//                }
                val toastMessage = if (URL_REGEX_PASSWORD_EDIT.toRegex().matches(currentUrl)) {
                    activity.getString(R.string.toast_password_reset_successfull)
                } else null
//                FetLifeWebViewActivity.startLogin(FetLifeApplication.getInstance(), toastMessage)
                LoginActivity.startLogin(FetLifeApplication.getInstance(), toastMessage)
                true
            }
            else -> false
        }
    }

    private fun openAsNewWebViewFlow(uri: Uri, currentUrl: String): Boolean {
//        if (URL_REGEX_LOGIN.toRegex().matches(currentUrl) && !URL_REGEX_LOGIN.toRegex().matches(uri.toString())) {
//            return true
//        }
        for (uriRegex in newWebViewFlowUrlSet) {
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
        for (uriRegex in inPlaceOpenWithNoHistoryUrlSet) if (uriRegex.toRegex().matches(uri.toString()) && !uriRegex.toRegex().matches(currentUrl)) {
            return true
        }
        return false
    }

    private fun openInPlace(uri: Uri): Boolean {
        for (uriRegex in inPlaceOpenUrlSet) {
            if (uriRegex.toRegex().matches(uri.toString())) {
                return true
            }
        }
        return false
    }

    private fun openInCustomTab(uri: Uri, context: Context) {
        val builder = CustomTabsIntent.Builder(FetLifeApplication.getInstance().customTabsSession).setToolbarColor(ColorUtil.retrieverColor(context, R.color.toolbar_chrome_custom_tab))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, uri)
    }

    private fun <T> checkRegexpMap(string: String?, map: Map<String, T>): T? {
        string ?: return null
        for ((key, value) in map) {
            if (key.toRegex().matches(string)) {
                return value
            }
        }
        return null
    }
    
    private fun checkRegexpSet(string: String?, set: Set<String>): Boolean {
        string ?: return false
        for (key in set) {
            if (key.toRegex().matches(string)) {
                return true
            }
        }
        return false
    }

//    private fun getClientSecret(): String {
//
//        try {
//            val application = FetLifeApplication.getInstance()
//
//            val iv = BuildConfig.SECURE_API_IV
//            if (iv.isEmpty()) {
//                return BuildConfig.CLIENT_SECRET
//            }
//
//            val cert = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                val pInfo = application.packageManager.getPackageInfo(application.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//                pInfo.signingInfo.signingCertificateHistory[0].toByteArray()
//            } else {
//                val pInfo = application.packageManager.getPackageInfo(application.packageName, PackageManager.GET_SIGNATURES)
//                pInfo.signatures[0].toByteArray()
//            }
//
//            val input = ByteArrayInputStream(cert)
//            val cf = CertificateFactory.getInstance("X509")
//            val c = cf.generateCertificate(input) as X509Certificate
//
//            val key = ByteArray(16)
//            System.arraycopy(c.publicKey.encoded, 0, key, 0, 16)
//
//            val secret = SecretKeySpec(key, "AES")
//
//            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//            cipher.init(Cipher.DECRYPT_MODE, secret, IvParameterSpec(Base64.decode(iv, Base64.NO_WRAP)))
//
//            input.close()
//
//            return String(cipher.doFinal(Base64.decode(BuildConfig.CLIENT_SECRET, Base64.NO_WRAP)))
//
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//
//    }

}