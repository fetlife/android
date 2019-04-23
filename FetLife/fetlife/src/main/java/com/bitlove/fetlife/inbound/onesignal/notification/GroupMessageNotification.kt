package com.bitlove.fetlife.inbound.onesignal.notification

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.text.TextUtils
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.R
import com.bitlove.fetlife.event.NewGroupMessageEvent
import com.bitlove.fetlife.inbound.onesignal.NotificationParser
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Group
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupPost
import com.bitlove.fetlife.model.service.FetLifeApiIntentService
import com.bitlove.fetlife.util.LogUtil
import com.bitlove.fetlife.view.screen.BaseActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupMessagesActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupsActivity
import org.json.JSONObject

class GroupMessageNotification(notificationType: String, notificationIdRange: Int, title: String, message: String, launchUrl: String, mergeId: String?, collapseId: String?, additionalData: JSONObject, preferenceKey: String?) : OneSignalNotification(notificationType, notificationIdRange, title, message, launchUrl, mergeId, collapseId, additionalData, preferenceKey) {

    private var groupId: String?
    private var groupDiscussionId: String? = additionalData.optString(NotificationParser.JSON_FIELD_STRING_GROUPPOSTID)
    private var groupTitle: String?
    private var groupDiscussionTitle: String?

    init {
        val apiContainer = additionalData.optJSONObject(NotificationParser.JSON_FIELD_OBJECT_API)
        groupId = apiContainer?.optString(NotificationParser.JSON_FIELD_STRING_GROUPID)
        groupDiscussionId = apiContainer?.optString(NotificationParser.JSON_FIELD_STRING_GROUPPOSTID)
        groupTitle = Group.loadGroup(groupId)?.name
        groupDiscussionTitle = GroupPost.loadGroupPost(groupDiscussionId)?.title  ?: apiContainer.optString(NotificationParser.JSON_FIELD_STRING_GROUP_POST_TITLE)
        if (TextUtils.isEmpty(groupDiscussionTitle)) {
            groupDiscussionTitle = apiContainer.optString(NotificationParser.JSON_FIELD_STRING_GROUP_NAME)
        }
    }

    override fun handle(fetLifeApplication: FetLifeApplication): Boolean {
        LogUtil.writeLog("GroupMessageNotification.handle()")
        LogUtil.writeLog("GroupMessageNotification.handle():groupId:"+groupId+";groupDiscussionId:" + groupDiscussionId);

        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(groupDiscussionId)) {
            return false
        }

        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_GROUP, groupId)
        FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES, groupId, groupDiscussionId)

        val groupDiscussionInForeground = false
        val appInForeground = fetLifeApplication.isAppInForeground
        LogUtil.writeLog("GroupMessageNotification.handle():appInForeground:" + appInForeground)

        if (appInForeground) {
            fetLifeApplication.eventBus.post(NewGroupMessageEvent(groupId, groupDiscussionId))
            val foregroundActivity = fetLifeApplication.foregroundActivity
            if (foregroundActivity is GroupMessagesActivity) {
                return groupId == foregroundActivity.groupId && groupDiscussionId == foregroundActivity.groupDiscussionId
            }
        }

        LogUtil.writeLog("GroupMessageNotification.handle():groupDiscussionInForeground:" + groupDiscussionInForeground.toString())

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
        return context.resources.getQuantityString(R.plurals.noification_summary_title_groups_new_message, notificationCount, notificationCount)
    }

    override fun getSummaryText(notificationCount: Int, context: Context): String? {
        return context.getString(R.string.noification_summary_text_groups_new_message)
    }

    override fun getNotificationTitle(oneSignalNotification: OneSignalNotification, notificationCount: Int, context: Context): String? {
        return if (notificationCount == 1) super.getNotificationTitle(oneSignalNotification, notificationCount, context) else {
            context.resources.getQuantityString(R.plurals.noification_title_groups_new_message, notificationCount, notificationCount)
        }
    }

    override fun getNotificationText(oneSignalNotification: OneSignalNotification, notificationCount: Int, context: Context): String? {
        return if (notificationCount == 1) super.getNotificationText(oneSignalNotification, notificationCount, context) else context.getString(R.string.noification_text_groups_new_message)
    }

    override fun getNotificationIntent(oneSignalNotification: OneSignalNotification, context: Context, order: Int): PendingIntent? {
        val groupId = (oneSignalNotification as? GroupMessageNotification)?.groupId
        val groupDiscussionId = (oneSignalNotification as? GroupMessageNotification)?.groupDiscussionId
        val groupTitle = (oneSignalNotification as? GroupMessageNotification)?.groupTitle
        val groupDiscussionTitle = (oneSignalNotification as? GroupMessageNotification)?.groupDiscussionTitle
        val baseIntent = GroupsActivity.createIntent(context, true)
        val interimIntent = GroupActivity.createIntent(context, groupId, groupTitle, false)
        val contentIntent = GroupMessagesActivity.createIntent(context, groupId, groupDiscussionId, groupDiscussionTitle, null, false). apply {
            putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE, oneSignalNotification.notificationType)
            putExtra(BaseActivity.EXTRA_NOTIFICATION_MERGE_ID, oneSignalNotification.mergeId)
        }
        return TaskStackBuilder.create(context).addNextIntentWithParentStack(baseIntent).addNextIntent(interimIntent).addNextIntent(contentIntent).getPendingIntent(order, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    override fun getLegacySummaryIntent(context: Context): PendingIntent? {
        return PendingIntent.getActivity(context,notificationIdRange, GroupsActivity.createIntent(context,true),PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun getNotificationItemLaunchUrl(): String? {
        //Temporary legacy support
        return LAUNCH_URL_PREFIX + "com.bitlove.fetlife.notification.GroupMessageNotification" + LAUNCH_URL_PARAM_SEPARATOR + groupId + LAUNCH_URL_PARAM_SEPARATOR + groupDiscussionId + LAUNCH_URL_PARAM_SEPARATOR + groupDiscussionTitle
    }

}