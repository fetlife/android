package com.bitlove.fetlife.event;

public class ServiceCallFailedEvent {

    private final boolean serverConnectionFailed;
    private final String serviceCallAction;

    public ServiceCallFailedEvent(String serviceCallAction) {
        this(serviceCallAction, false);
    }

    public ServiceCallFailedEvent(String action, boolean serverConnectionFailed) {
        this.serverConnectionFailed = serverConnectionFailed;
        this.serviceCallAction = action;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }

    public boolean isServerConnectionFailed() {
        return serverConnectionFailed;
    }

}
