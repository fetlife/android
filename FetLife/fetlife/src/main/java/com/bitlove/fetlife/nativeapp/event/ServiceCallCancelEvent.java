package com.bitlove.fetlife.nativeapp.event;

public class ServiceCallCancelEvent {

    private final String serviceCallAction;

    public ServiceCallCancelEvent(String action) {
        this.serviceCallAction = action;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }
}
