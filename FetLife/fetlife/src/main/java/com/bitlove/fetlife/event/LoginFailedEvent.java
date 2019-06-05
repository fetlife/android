package com.bitlove.fetlife.event;

public class LoginFailedEvent {

    private boolean serverConnectionFailed;
    private String serverErrorCode;

    public LoginFailedEvent() {
    }

    public LoginFailedEvent(boolean serverConnectionFailed, String serverErrorCode) {
        this.serverConnectionFailed = serverConnectionFailed;
        this.serverErrorCode = serverErrorCode;
    }

    public boolean isServerConnectionFailed() {
        return serverConnectionFailed;
    }

    public String getServerErrorCode() {
        return serverErrorCode;
    }
}
