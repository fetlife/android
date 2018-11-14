package com.bitlove.fetlife.inbound.onesignal

import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.inbound.onesignal.notification.OneSignalNotification
import com.bitlove.fetlife.inbound.onesignal.notification.QuestionAnsweredNotification
import com.bitlove.fetlife.inbound.onesignal.notification.UnknownNotification
import com.onesignal.OSNotificationReceivedResult
import org.json.JSONObject

class NotificationParser {

    fun parseNotification(fetLifeApplication: FetLifeApplication, osNotificationReceivedResult: OSNotificationReceivedResult): OneSignalNotification {
        val osNotificationPayload = osNotificationReceivedResult.payload

        val additionalData = osNotificationPayload.additionalData
        val id = osNotificationPayload.notificationID
        val title = osNotificationPayload.title
        val message = osNotificationPayload.body
        val launchUrl = osNotificationPayload.launchURL
        val group = osNotificationPayload.groupKey

        checkMinVersion(additionalData, fetLifeApplication) || return UnknownNotification(id,title,message,launchUrl,group,additionalData)
        checkMaxVersion(additionalData, fetLifeApplication) || return UnknownNotification(id,title,message,launchUrl,group,additionalData)

        val notificationType = additionalData?.optString(JSON_FIELD_STRING_TYPE)?.toLowerCase() ?: return UnknownNotification(id,title,message,launchUrl,group,additionalData)
        return when {
            notificationType.startsWith(JSON_VALUE_TYPE_PREFIX_QUESTION) -> QuestionAnsweredNotification(id,title,message,launchUrl,group,additionalData)
            else -> {
                if (title != null && message != null) {
                    InfoNotification(id,title,message,launchUrl,group,additionalData)
                } else {
                    UnknownNotification(id,title,message,launchUrl,group,additionalData)
                }
            }
        }
    }

    fun clearNotification(notificationType: String, groupingField: String) {
        when {
            notificationType == JSON_VALUE_TYPE_INFO -> InfoNotification.clearNotifications()
            notificationType.startsWith(JSON_VALUE_TYPE_PREFIX_QUESTION) -> QuestionAnsweredNotification.clearNotifications(groupingField)
            else -> {}
        }//Legacy, skip
    }


    private fun checkMinVersion(additionalData: JSONObject?, fetLifeApplication: FetLifeApplication): Boolean {
        val minVersion = additionalData?.optString(JSON_FIELD_INT_MIN_VERSION) ?: return true

        return try {
            Integer.parseInt(minVersion) <= fetLifeApplication.versionNumber
        } catch (nfe: java.lang.NumberFormatException) {
            true
        }
    }

    private fun checkMaxVersion(additionalData: JSONObject?, fetLifeApplication: FetLifeApplication): Boolean {
        val maxVersion = additionalData?.optString(JSON_FIELD_INT_MAX_VERSION) ?: return true

        return try {
            Integer.parseInt(maxVersion) >= fetLifeApplication.versionNumber
        } catch (nfe: java.lang.NumberFormatException) {
            true
        }
    }

    companion object {
        const val JSON_FIELD_STRING_TYPE = "type"
        const val JSON_FIELD_STRING_COLLAPSE_ID = "collapse_id"

        const val JSON_FIELD_INT_MIN_VERSION = "min_version"
        const val JSON_FIELD_INT_MAX_VERSION = "max_version"

        const val JSON_FIELD_STRING_CONVERSATION_ID = "conversation_id"
        const val JSON_FIELD_STRING_NICKNAME = "nickname"

        const val JSON_VALUE_TYPE_INFO = "info"

        const val JSON_VALUE_TYPE_FRIEND_REQUEST = "friend_request"

        const val JSON_VALUE_TYPE_PREFIX_CONVERSATION = "conversation"
        const val JSON_VALUE_TYPE_CONVERSATION_NEW = "conversation_new"
        const val JSON_VALUE_TYPE_CONVERSATION_RESPONSE = "conversation_response"

        const val JSON_VALUE_TYPE_PREFIX_QUESTION = "question"
        const val JSON_VALUE_TYPE_QUESTION_ANSWERED = "question_answered"

        const val JSON_VALUE_TYPE_PREFIX_COMMENT = "comment"
        const val JSON_VALUE_TYPE_COMMENT_PICTURE = "comment_picture"
        const val JSON_VALUE_TYPE_COMMENT_VIDEO = "comment_video"
        const val JSON_VALUE_TYPE_COMMENT_WRITING = "comment_writing"
        const val JSON_VALUE_TYPE_COMMENT_STATUS_UPDATE = "comment_status_update"
        const val JSON_VALUE_TYPE_COMMENT_SUGGESTION = "comment_suggestion"
        const val JSON_VALUE_TYPE_COMMENT_GROUP = "comment_group"

        const val JSON_VALUE_TYPE_PREFIX_LOVE = "love"
        const val JSON_VALUE_TYPE_LOVE_PICTURE = "love_picture"
        const val JSON_VALUE_TYPE_LOVE_WRITING = "love_writing"
        const val JSON_VALUE_TYPE_LOVE_VIDEO = "love_video"
        const val JSON_VALUE_TYPE_LOVE_STATUS_UPDATE = "love_status_update"
        const val JSON_VALUE_TYPE_LOVE_SUGGESTION = "love_suggestion"

        const val JSON_VALUE_TYPE_PREFIX_MENTION = "mention"
        const val JSON_VALUE_TYPE_MENTION = "mention"
        const val JSON_VALUE_TYPE_MENTION_PICTURE_CAPTION = "mention_picture_caption"
        const val JSON_VALUE_TYPE_MENTION_PICTURE_COMMENT = "mention_picture_comment"
        const val JSON_VALUE_TYPE_MENTION_VIDEO_CAPTION = "mention_video_caption"
        const val JSON_VALUE_TYPE_MENTION_VIDEO_COMMENT = "mention_video_comment"
        const val JSON_VALUE_TYPE_MENTION_WRITING = "mention_writing"
        const val JSON_VALUE_TYPE_MENTION_WRITING_COMMENT = "mention_writing_comment"
        const val JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION = "mention_group_discussion"
        const val JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION_COMMENT = "mention_group_discussion_comment"
        const val JSON_VALUE_TYPE_MENTION_GROUP_DESCRIPTION = "mention_group_desciption"
        const val JSON_VALUE_TYPE_MENTION_STATUS_UPDATE = "mention_status_update"
        const val JSON_VALUE_TYPE_MENTION_STATUS_UPDATE_COMMENT = "mention_status_update_comment"
        const val JSON_VALUE_TYPE_MENTION_FETISH = "mention_fetish"
        const val JSON_VALUE_TYPE_MENTION_SUGGESTION = "mention_suggestion"
        const val JSON_VALUE_TYPE_MENTION_SUGGESTION_COMMENT = "mention_suggestion_comment"
        const val JSON_VALUE_TYPE_MENTION_WALL_POST = "mention_wall_post"
        const val JSON_VALUE_TYPE_MENTION_EVENT_LISTING = "mention_event_listing"
        const val JSON_VALUE_TYPE_MENTION_ABOUT_ME = "mention_about_me"
    }
    
}