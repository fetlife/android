package com.bitlove.fetlife.nativeapp.event;

public class LoginFailedEvent {

    private boolean serverConnectionFailed;

    public LoginFailedEvent() {
    }

    public LoginFailedEvent(boolean serverConnectionFailed) {
        this.serverConnectionFailed = serverConnectionFailed;
    }

    public boolean isServerConnectionFailed() {
        return serverConnectionFailed;
    }
}
