package com.bitlove.fetlife.nativeapp.event;

public class MessageSendFailedEvent {
    private final String conversationId;
    private final boolean isForbidden;

    public MessageSendFailedEvent(String conversationId, boolean isForbidden) {
        this.conversationId = conversationId;
        this.isForbidden = isForbidden;
    }

    public String getConversationId() {
        return conversationId;
    }

    public boolean isForbidden() {
        return isForbidden;
    }
}
