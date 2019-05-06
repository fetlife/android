package com.bitlove.fetlife.nativeapp.event;

public class NewConversationEvent {

    private final String conversationId;
    private final String localConversationId;

    public NewConversationEvent(String localConversationId, String conversationId) {
        this.localConversationId = localConversationId;
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getLocalConversationId() {
        return localConversationId;
    }
}
