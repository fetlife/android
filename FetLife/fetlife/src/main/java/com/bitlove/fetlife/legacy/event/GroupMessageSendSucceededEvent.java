package com.bitlove.fetlife.legacy.event;

public class GroupMessageSendSucceededEvent {

    private final String groupId;
    private final String groupPostId;

    public GroupMessageSendSucceededEvent(String groupId, String groupPostId) {
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
