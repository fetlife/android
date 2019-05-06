package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationCounts {

    @JsonProperty("message_count")
    private Integer messageCount;

    @JsonProperty("friendrequest_count")
    private Integer requestCount;

    @JsonProperty("at_count")
    private Integer notificationCount;

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }
}