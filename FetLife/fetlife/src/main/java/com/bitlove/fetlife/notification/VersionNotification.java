package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

@Deprecated
public class VersionNotification extends InfoNotification {

    private String version;

    public VersionNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message, launchUrl, additionalData, id, group);
        collapseId = getClass().getSimpleName();
        version = additionalData != null ? additionalData.optString(NotificationParser.JSON_FIELD_STRING_VERSION) : null;
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        return fetLifeApplication.getVersionText().equals(version);
    }
}
