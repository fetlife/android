package com.bitlove.fetlife.nativeapp.event;

public class NewGroupMessageEvent {

    private final String groupId;
    private final String groupDiscussionId;

    public NewGroupMessageEvent(String groupId, String groupDisucssionId) {
       this.groupId = groupId;
       this.groupDiscussionId = groupDisucssionId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupDiscussionId() {
        return groupDiscussionId;
    }
}
