package com.bitlove.fetlife.legacy.inbound.onesignal.notification

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.legacy.event.NewGroupMessageEvent
import com.bitlove.fetlife.legacy.inbound.onesignal.NotificationParser
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.GroupPost
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService
import com.bitlove.fetlife.legacy.util.ServerIdUtil
import com.bitlove.fetlife.legacy.view.screen.BaseActivity
import com.bitlove.fetlife.legacy.view.screen.resource.groups.GroupActivity
import com.bitlove.fetlife.legacy.view.screen.resource.groups.GroupMessagesActivity
import com.bitlove.fetlife.legacy.view.screen.resource.groups.GroupsActivity
import org.json.JSONObject

class GroupNotification(notificationType: String, notificationIdRange: Int, title: String, message: String, launchUrl: String, mergeId: String?, collapseId: String?, additionalData: JSONObject, preferenceKey: String?) : OneSignalNotification(notificationType, notificationIdRange, title, message, launchUrl, mergeId, collapseId, additionalData, preferenceKey) {

    private var groupId: String? = null
    private var groupDiscussionId: String? = additionalData.optString(NotificationParser.JSON_FIELD_STRING_GROUPPOSTID)
    private var groupTitle: String? = null
    private var groupDiscussionTitle: String? = null

    init {
        val launchUri = Uri.parse(launchUrl)
        if (launchUri.isHierarchical) {
            val pathSegments = launchUri.pathSegments
            if (pathSegments.size >=4) {
                groupId = ServerIdUtil.prefixServerId(pathSegments[1])
                groupDiscussionId = ServerIdUtil.prefixServerId(pathSegments[3])
                val group = Group.loadGroup(groupId)
                if (group != null) {
                    groupId = group.id
                    groupTitle = group.name
                }
                val groupPost = GroupPost.loadGroupPost(groupDiscussionId)
                if (groupPost != null) {
                    groupDiscussionId = groupPost.id
                    groupDiscussionTitle = groupPost.title
                }
            }
        }
    }

    override fun handle(fetLifeApplication: FetLifeApplication): Boolean {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupDiscussionId)) {
            return false
        }
        if (ServerIdUtil.isServerId(groupId) || ServerIdUtil.isServerId(groupDiscussionId)) {
            return false
        }


        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_GROUP, groupId)
        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES, groupId, groupDiscussionId)

        val groupDiscussionInForeground = false
        val appInForeground = fetLifeApplication.isAppInForeground

        if (appInForeground) {
            fetLifeApplication.eventBus.post(NewGroupMessageEvent(groupId, groupDiscussionId))
            val foregroundActivity = fetLifeApplication.foregroundActivity
            if (foregroundActivity is GroupMessagesActivity) {
                return groupId == foregroundActivity.groupId && groupDiscussionId == foregroundActivity.groupDiscussionId
            }
        }

        if (groupDiscussionInForeground) {
            //otherwise it will be saved in display
            saveNotificationItem(notificationIdRange)
        }

        //TODO: display in app notification if the user is not on the same message screen
        return groupDiscussionInForeground
    }

    override fun getNotificationChannelName(context: Context): String? {
        return context.getString(R.string.settings_title_notification_group_messages_enabled)
    }

    override fun getNotificationChannelDescription(context: Context): String? {
        return context.getString(R.string.settings_summary_notification_group_messages_enabled)
    }

    override fun getSummaryTitle(notificationCount: Int, context: Context): String? {
        return context.resources.getQuantityString(R.plurals.noification_summary_title_groups_new_discussion_with_title, notificationCount, notificationCount, title)
    }

    override fun getSummaryText(notificationCount: Int, context: Context): String? {
        return context.getString(R.string.noification_summary_text_groups_new_discussion)
    }

    override fun getNotificationTitle(oneSignalNotification: OneSignalNotification, notificationCount: Int, context: Context): String? {
        return if (notificationCount == 1) super.getNotificationTitle(oneSignalNotification, notificationCount, context) else {
            context.resources.getQuantityString(R.plurals.noification_summary_title_groups_new_discussion_with_title, notificationCount, notificationCount, title)
        }
    }

    override fun getNotificationText(oneSignalNotification: OneSignalNotification, notificationCount: Int, context: Context): String? {
        return if (notificationCount == 1) super.getNotificationText(oneSignalNotification, notificationCount, context) else context.getString(R.string.noification_text_groups_new_discussion_with_title, title)
    }

    override fun getNotificationIntent(oneSignalNotification: OneSignalNotification, context: Context, order: Int): PendingIntent? {
        val groupId = (oneSignalNotification as? GroupNotification)?.groupId
        val groupDiscussionId = (oneSignalNotification as? GroupNotification)?.groupDiscussionId
        if (groupId != null && groupDiscussionId != null) {
            val groupTitle = (oneSignalNotification as? GroupNotification)?.groupTitle
            val groupDiscussionTitle = (oneSignalNotification as? GroupNotification)?.groupDiscussionTitle
            val baseIntent = GroupsActivity.createIntent(context, true)
            val interimIntent = GroupActivity.createIntent(context, groupId, groupTitle, false)
            val contentIntent = GroupMessagesActivity.createIntent(context, groupId, groupDiscussionId, groupDiscussionTitle, null, false). apply {
                putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, oneSignalNotification.notificationType)
                putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, oneSignalNotification.mergeId)
            }
            return TaskStackBuilder.create(context).addNextIntentWithParentStack(baseIntent).addNextIntent(interimIntent).addNextIntent(contentIntent).getPendingIntent(order, PendingIntent.FLAG_CANCEL_CURRENT)
        } else {
            return PendingIntent.getActivity(context,0,Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_RECEIVER_FOREGROUND)
                data = Uri.parse(launchUrl)
            },0)

        }
    }

    override fun getLegacySummaryIntent(context: Context): PendingIntent? {
        return PendingIntent.getActivity(context,notificationIdRange, GroupsActivity.createIntent(context,true),PendingIntent.FLAG_UPDATE_CURRENT)
    }

//    override fun saveNotificationItem(notificationId: Int) {}

}