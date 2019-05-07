package com.bitlove.fetlife.legacy.event;

public class NotificationCountUpdatedEvent {

    private Integer notificationCount;
    private Integer requestCount;
    private Integer messagesCount;

    public NotificationCountUpdatedEvent() {

    }

    public NotificationCountUpdatedEvent(int notificationCount, int requestCount, int messagesCount) {
        this.notificationCount = notificationCount;
        this.requestCount = requestCount;
        this.messagesCount = messagesCount;
    }

//    public NotificationCountUpdatedEvent(JsonElement countElement) {
//
//    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public Integer getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(Integer messagesCount) {
        this.messagesCount = messagesCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }
}
