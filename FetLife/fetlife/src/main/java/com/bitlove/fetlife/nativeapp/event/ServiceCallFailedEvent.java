package com.bitlove.fetlife.nativeapp.event;

public class ServiceCallFailedEvent {

    private final boolean serverConnectionFailed;
    private final String serviceCallAction;
    private final String[] params;

    public ServiceCallFailedEvent(String action, boolean serverConnectionFailed, String... params) {
        this.serverConnectionFailed = serverConnectionFailed;
        this.serviceCallAction = action;
        this.params = params;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }

    public boolean isServerConnectionFailed() {
        return serverConnectionFailed;
    }

    public String[] getParams() {
        return params;
    }
}
