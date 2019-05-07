package com.bitlove.fetlife.legacy.event;

public class ServiceCallCancelEvent {

    private final String serviceCallAction;

    public ServiceCallCancelEvent(String action) {
        this.serviceCallAction = action;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }
}
