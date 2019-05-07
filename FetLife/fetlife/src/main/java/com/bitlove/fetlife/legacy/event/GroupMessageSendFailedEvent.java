package com.bitlove.fetlife.legacy.event;

public class GroupMessageSendFailedEvent {

    private final String groupId;
    private final String groupPostId;

    public GroupMessageSendFailedEvent(String groupId, String groupPostId) {
        this.groupId = groupId;
        this.groupPostId = groupPostId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupPostId() {
        return groupPostId;
    }

}
