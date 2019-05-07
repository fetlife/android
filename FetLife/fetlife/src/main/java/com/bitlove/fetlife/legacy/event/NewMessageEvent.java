package com.bitlove.fetlife.legacy.event;

public class NewMessageEvent {

    private final String conversationId;

    public NewMessageEvent(String conversationId) {
       this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
