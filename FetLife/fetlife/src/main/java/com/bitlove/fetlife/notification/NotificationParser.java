package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationParser {

    public static final String JSON_FIELD_STRING_LAUNCHURL = "launchURL";
    public static final String JSON_FIELD_STRING_TITLE = "title";
    public static final String JSON_FIELD_STRING_GROUP = "grp";

    public static final String JSON_FIELD_STRING_CONVERSATIONID = "conversation_id";
    public static final String JSON_FIELD_STRING_NICKNAME = "nickname";

    public static final String JSON_FIELD_STRING_COLLAPSE_ID = "collapse_id";
    public static final java.lang.String JSON_FIELD_STRING_VERSION = "version";

    private static final String JSON_FIELD_STRING_TYPE = "type";
    private static final java.lang.String JSON_FIELD_INT_MIN_VERSION = "min_version";
    private static final java.lang.String JSON_FIELD_INT_MAX_VERSION = "max_version";

    public static final String JSON_VALUE_GROUP_INFO = "info";
    public static final String JSON_VALUE_GROUP_FETLIFE_MESSAGE = "fetlife_messages";

    public static final String JSON_VALUE_TYPE_INFO = "info";

    public static final String JSON_VALUE_TYPE_FRIEND_REQUEST = "friend_request";
    public static final String JSON_VALUE_TYPE_CONVERSATION_NEW = "conversation_new";
    public static final String JSON_VALUE_TYPE_CONVERSATION_RESPONSE = "conversation_response";

    public static final String JSON_VALUE_TYPE_QUESTIONS_NEW_ANSWER = "question_answered";

    public static final String JSON_VALUE_TYPE_SIGNUP_EMAIL_ADDRESS_VERIFY = "signup_email_address_verify";
    public static final String JSON_VALUE_TYPE_SIGNUP_EMAIL_ADDRESS_USED = "signup_email_address_used";
    public static final String JSON_VALUE_TYPE_PASSWORD_RESET = "password_reset";
    public static final String JSON_VALUE_TYPE_PASSWORD_CHANGE_NOTIFICATION = "password_change_notification";
    public static final String JSON_VALUE_TYPE_FRIEND_REQUEST_ACCEPTED = "friend_request_accepted";

    public static final String JSON_VALUE_TYPE_COMMENT_PICTURE = "comment_picture";
    public static final String JSON_VALUE_TYPE_COMMENT_VIDEO = "comment_video";
    public static final String JSON_VALUE_TYPE_COMMENT_WRITING = "comment_writing";
    public static final String JSON_VALUE_TYPE_COMMENT_STATUS_UPDATE = "comment_status_update";
    public static final String JSON_VALUE_TYPE_COMMENT_SUGGESTION = "comment_suggestion";
    public static final String JSON_VALUE_TYPE_COMMENT_GROUP = "comment_group";

    public static final String JSON_VALUE_TYPE_LOVE_PICTURE = "love_picture";
    public static final String JSON_VALUE_TYPE_LOVE_WRITING = "love_writing";
    public static final String JSON_VALUE_TYPE_LOVE_VIDEO = "love_video";
    public static final String JSON_VALUE_TYPE_LOVE_STATUS_UPDATE = "love_status_update";
    public static final String JSON_VALUE_TYPE_LOVE_SUGGESTION = "love_suggestion";

    public static final String JSON_VALUE_TYPE_MENTION = "mention";
    public static final String JSON_VALUE_TYPE_MENTION_PICTURE_CAPTION = "mention_picture_caption";
    public static final String JSON_VALUE_TYPE_MENTION_PICTURE_COMMENT = "mention_picture_comment";
    public static final String JSON_VALUE_TYPE_MENTION_VIDEO_CAPTION = "mention_video_caption";
    public static final String JSON_VALUE_TYPE_MENTION_VIDEO_COMMENT = "mention_video_comment";
    public static final String JSON_VALUE_TYPE_MENTION_WRITING = "mention_writing";
    public static final String JSON_VALUE_TYPE_MENTION_WRITING_COMMENT = "mention_writing_comment";
    public static final String JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION = "mention_group_discussion";
    public static final String JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION_COMMENT = "mention_group_discussion_comment";
    public static final String JSON_VALUE_TYPE_MENTION_GROUP_DESCRIPTION = "mention_group_desciption";
    public static final String JSON_VALUE_TYPE_MENTION_STATUS_UPDATE = "mention_status_update";
    public static final String JSON_VALUE_TYPE_MENTION_STATUS_UPDATE_COMMENT = "mention_status_update_comment";
    public static final String JSON_VALUE_TYPE_MENTION_FETISH = "mention_fetish";
    public static final String JSON_VALUE_TYPE_MENTION_SUGGESTION = "mention_suggestion";
    public static final String JSON_VALUE_TYPE_MENTION_SUGGESTION_COMMENT = "mention_suggestion_comment";
    public static final String JSON_VALUE_TYPE_MENTION_WALL_POST = "mention_wall_post";
    public static final String JSON_VALUE_TYPE_MENTION_EVENT_LISTING = "mention_event_listing";
    public static final String JSON_VALUE_TYPE_MENTION_ABOUT_ME = "mention_about_me";

    public static final String JSON_VALUE_TYPE_GROUP_POST = "group_post";
    public static final String JSON_VALUE_TYPE_GROUP_POST_RESPONSE = "comment_group";
    public static final String JSON_VALUE_TYPE_WALL_POST = "wall_post";
    public static final String JSON_VALUE_TYPE_FLAGGING = "flagging";
    public static final String JSON_VALUE_TYPE_INVITATION_INSTRUCTIONS = "invitation_instructions";
    public static final String JSON_VALUE_TYPE_SUPPORT_GIFTEE_NOTIFICATION = "support_giftee_notification";
    public static final String JSON_VALUE_TYPE_SUPPORT_GIFTER_NOTIFICATION = "support_gifter_notification";
    public static final String JSON_VALUE_TYPE_ACH_FAILURE = "ach_failure";
    public static final String JSON_VALUE_TYPE_SEPA_FAILURE = "sepa_failure";
    public static final String JSON_VALUE_TYPE_PAYGARDEN_FAILURE = "paygarden_failure";
    public static final String JSON_VALUE_TYPE_INTERAC_SUCCESS = "interac_success";
    public static final String JSON_VALUE_TYPE_MAIL_INFO = "mail_info";
    public static final String JSON_VALUE_TYPE_EMAIL_UPLOAD_NO_ATTACHMENT = "email_upload_no_attachment";
    public static final String JSON_VALUE_TYPE_EMAIL_UPLOAD_NO_SUCCESS = "email_upload_no_success";
    public static final String JSON_VALUE_TYPE_EMAIL_UPLOAD_PARTIAL_SUCCESS = "email_upload_partial_success";
    public static final String JSON_VALUE_TYPE_EMAIL_UPLOAD_SUCCESS = "email_upload_success";
    public static final String JSON_VALUE_TYPE_VIDEO_ENCODED = "video_encoded";
    public static final String JSON_VALUE_TYPE_VIDEO_ENCODING_FAILED = "video_encoding_failed";
    public static final String JSON_VALUE_TYPE_USER_DELETION = "user_deletion";
    public static final String JSON_VALUE_TYPE_SYSTEMS_NOTIFICATION = "systems_notification";
    public static final String JSON_VALUE_TYPE_CROSS_POSTING = "cross_posting";
    public static final String JSON_VALUE_TYPE_TEST = "test";

    //Backward compatibility

    public static final String JSON_VALUE_TYPE_LEGACY_VERSION = "version";
    public static final String JSON_VALUE_GROUP_LEGACY_FETLIFE = "fetlife";
    public static final String JSON_VALUE_GROUP_LEGACY_MESSAGE = "messages";
    public static final String JSON_VALUE_TYPE_LEGACY_CONVERSATION_CREATED = "conversation_created";
    public static final String JSON_VALUE_TYPE_LEGACY_MESSAGE_CREATED = "message_created";
    public static final String JSON_VALUE_TYPE_LEGACY_FRIEND_REQUEST_CREATED = "friendship_request_created";
    public static final String JSON_FIELD_STRING_GROUPID = "group_id";
    public static final String JSON_FIELD_STRING_GROUPPOSTID = "group_post_id";
    public static final String JSON_VALUE_TYPE_GROUP_COMMENT = "comment_group";
    public static final String JSON_FIELD_STRING_GROUP_POST_TITLE = "group_post_title";
    public static final String JSON_FIELD_OBJECT_API = "api";
    public static final java.lang.String JSON_FIELD_STRING_GROUP_NAME = "group_name";

    public OneSignalNotification parseNotification(FetLifeApplication fetLifeApplication, OSNotificationOpenResult osNotificationOpenResult) {

        OSNotification notification = osNotificationOpenResult.notification;
        List<OSNotificationPayload> groupedNotifications = notification.groupedNotifications;

        if (groupedNotifications == null || groupedNotifications.isEmpty()) {
            return parseNotification(fetLifeApplication, notification.payload);
        }

        List<OneSignalNotification> subNotificationList = new ArrayList<>(groupedNotifications.size());

        for (OSNotificationPayload payload : groupedNotifications) {
            OneSignalNotification subNotification = parseNotification(fetLifeApplication, payload);
            subNotificationList.add(subNotification);
        }

        StackedNotification stackedNotification = new StackedNotification(notification.payload.title, notification.payload.groupMessage, notification.payload.launchURL, notification.payload.additionalData, notification.payload.notificationID, notification.payload.groupKey, subNotificationList);
        return stackedNotification;
    }

    public OneSignalNotification parseNotification(FetLifeApplication fetLifeApplication, OSNotificationReceivedResult osNotificationReceivedResult) {
        return parseNotification(fetLifeApplication, osNotificationReceivedResult.payload);
    }

    private OneSignalNotification parseNotification(FetLifeApplication fetLifeApplication, OSNotificationPayload osNotificationPayload) {

        JSONObject additionalData = osNotificationPayload.additionalData;
        String id = osNotificationPayload.notificationID;
        String title = osNotificationPayload.title;
        String message = osNotificationPayload.body;
        String launchUrl = osNotificationPayload.launchURL;
        String group = osNotificationPayload.groupKey;

        //Check Version relevance

        String minVersion = additionalData != null ? additionalData.optString(JSON_FIELD_INT_MIN_VERSION) : null;
        if (minVersion != null && minVersion.trim().length() > 0) {
            try {
                int minVersionInt = Integer.parseInt(minVersion);
                if (minVersionInt > fetLifeApplication.getVersionNumber()) {
                    return new UnknownNotification(title, message, launchUrl, additionalData, id, group);
                }
            } catch (NumberFormatException nfe) {
                //skip
            }
        }

        String maxVersion = additionalData != null ? additionalData.optString(JSON_FIELD_INT_MAX_VERSION) : null;
        if (maxVersion != null && maxVersion.trim().length() > 0) {
            try {
                int maxVersionInt = Integer.parseInt(maxVersion);
                if (maxVersionInt < fetLifeApplication.getVersionNumber()) {
                    return new UnknownNotification(title, message, launchUrl, additionalData, id, group);
                }
            } catch (NumberFormatException nfe) {
                //skip
            }
        }

        //Check Type

        String type = additionalData != null ? additionalData.optString(JSON_FIELD_STRING_TYPE) : "";
        if (type != null) {
            type.toLowerCase();
        }

        switch (type) {
            case JSON_VALUE_TYPE_INFO:
            case JSON_VALUE_TYPE_LEGACY_VERSION:
                return new InfoNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_LEGACY_CONVERSATION_CREATED:
            case JSON_VALUE_TYPE_LEGACY_MESSAGE_CREATED:
            case JSON_VALUE_TYPE_CONVERSATION_NEW:
            case JSON_VALUE_TYPE_CONVERSATION_RESPONSE:
                return new MessageNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION:
            case JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION_COMMENT:
            case JSON_VALUE_TYPE_COMMENT_GROUP:
                return new GroupMessageNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_FRIEND_REQUEST:
            case JSON_VALUE_TYPE_LEGACY_FRIEND_REQUEST_CREATED:
                return new FriendRequestNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_LOVE_PICTURE:
            case JSON_VALUE_TYPE_LOVE_VIDEO:
            case JSON_VALUE_TYPE_LOVE_WRITING:
            case JSON_VALUE_TYPE_LOVE_STATUS_UPDATE:
            case JSON_VALUE_TYPE_LOVE_SUGGESTION:
                return new LoveNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_COMMENT_PICTURE:
            case JSON_VALUE_TYPE_COMMENT_STATUS_UPDATE:
            case JSON_VALUE_TYPE_COMMENT_SUGGESTION:
            case JSON_VALUE_TYPE_COMMENT_VIDEO:
            case JSON_VALUE_TYPE_COMMENT_WRITING:
                return new CommentNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_MENTION:
            case JSON_VALUE_TYPE_MENTION_PICTURE_CAPTION:
            case JSON_VALUE_TYPE_MENTION_PICTURE_COMMENT:
            case JSON_VALUE_TYPE_MENTION_VIDEO_CAPTION:
            case JSON_VALUE_TYPE_MENTION_VIDEO_COMMENT:
            case JSON_VALUE_TYPE_MENTION_WRITING:
            case JSON_VALUE_TYPE_MENTION_WRITING_COMMENT:
            case JSON_VALUE_TYPE_MENTION_GROUP_DESCRIPTION:
            case JSON_VALUE_TYPE_MENTION_STATUS_UPDATE:
            case JSON_VALUE_TYPE_MENTION_STATUS_UPDATE_COMMENT:
            case JSON_VALUE_TYPE_MENTION_FETISH:
            case JSON_VALUE_TYPE_MENTION_SUGGESTION:
            case JSON_VALUE_TYPE_MENTION_SUGGESTION_COMMENT:
            case JSON_VALUE_TYPE_MENTION_WALL_POST:
            case JSON_VALUE_TYPE_MENTION_EVENT_LISTING:
            case JSON_VALUE_TYPE_MENTION_ABOUT_ME:
                return new MentionNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_QUESTIONS_NEW_ANSWER:
                return new NewAnswerNotification(title, message, launchUrl, additionalData, id, group);
            default:
                OneSignalNotification notification = parseByUrl(title, message, launchUrl, additionalData, id, group);
                if (notification != null) {
                    return notification;
                }
                if (title != null || message != null) {
                    return new InfoNotification(title, message, launchUrl, additionalData, id, group);
                } else {
                    return new UnknownNotification(title, message, launchUrl, additionalData, id, group);
                }
        }
    }

    private OneSignalNotification parseByUrl(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        if (launchUrl == null) {
            return null;
        }
        return null;
    }

    public void clearNotification(String notificationType) {
        switch (notificationType) {
            case JSON_VALUE_TYPE_INFO:
                InfoNotification.clearNotifications();
                break;
            case JSON_VALUE_TYPE_LEGACY_VERSION:
                //Legacy, skip
                break;
            case JSON_VALUE_TYPE_LEGACY_CONVERSATION_CREATED:
            case JSON_VALUE_TYPE_LEGACY_MESSAGE_CREATED:
            case JSON_VALUE_TYPE_CONVERSATION_NEW:
            case JSON_VALUE_TYPE_CONVERSATION_RESPONSE:
                MessageNotification.clearNotifications();
                break;
            case JSON_VALUE_TYPE_FRIEND_REQUEST:
            case JSON_VALUE_TYPE_LEGACY_FRIEND_REQUEST_CREATED:
                FriendRequestNotification.clearNotifications();
                break;
            case JSON_VALUE_TYPE_LOVE_PICTURE:
            case JSON_VALUE_TYPE_LOVE_VIDEO:
            case JSON_VALUE_TYPE_LOVE_WRITING:
                LoveNotification.clearNotifications();
                break;
            case JSON_VALUE_TYPE_COMMENT_PICTURE:
            case JSON_VALUE_TYPE_COMMENT_STATUS_UPDATE:
            case JSON_VALUE_TYPE_COMMENT_SUGGESTION:
            case JSON_VALUE_TYPE_COMMENT_VIDEO:
            case JSON_VALUE_TYPE_COMMENT_WRITING:
                CommentNotification.clearNotifications();
                break;
            case JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION:
            case JSON_VALUE_TYPE_MENTION_GROUP_DISCUSSION_COMMENT:
            case JSON_VALUE_TYPE_MENTION_GROUP_DESCRIPTION:
                GroupMessageNotification.clearNotifications();
                break;
            case JSON_VALUE_TYPE_MENTION:
            case JSON_VALUE_TYPE_MENTION_PICTURE_CAPTION:
            case JSON_VALUE_TYPE_MENTION_PICTURE_COMMENT:
            case JSON_VALUE_TYPE_MENTION_VIDEO_CAPTION:
            case JSON_VALUE_TYPE_MENTION_VIDEO_COMMENT:
            case JSON_VALUE_TYPE_MENTION_WRITING:
            case JSON_VALUE_TYPE_MENTION_WRITING_COMMENT:
            case JSON_VALUE_TYPE_MENTION_STATUS_UPDATE:
            case JSON_VALUE_TYPE_MENTION_STATUS_UPDATE_COMMENT:
            case JSON_VALUE_TYPE_MENTION_FETISH:
            case JSON_VALUE_TYPE_MENTION_SUGGESTION:
            case JSON_VALUE_TYPE_MENTION_SUGGESTION_COMMENT:
            case JSON_VALUE_TYPE_MENTION_WALL_POST:
            case JSON_VALUE_TYPE_MENTION_EVENT_LISTING:
            case JSON_VALUE_TYPE_MENTION_ABOUT_ME:
                MentionNotification.clearNotifications();
                break;
            case JSON_VALUE_TYPE_QUESTIONS_NEW_ANSWER:
                NewAnswerNotification.clearNotifications();
            default:
                AnonymNotification.clearNotifications();
                break;
        }
    }

}
