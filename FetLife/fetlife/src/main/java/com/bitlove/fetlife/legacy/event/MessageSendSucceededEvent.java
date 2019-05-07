package com.bitlove.fetlife.legacy.event;

public class MessageSendSucceededEvent {

    private final String conversationId;

    public MessageSendSucceededEvent(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
