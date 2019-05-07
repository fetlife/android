package com.bitlove.fetlife.legacy.event;

public class ServiceCallStartedEvent {

    private String[] params;
    private String serviceCallAction;

    public ServiceCallStartedEvent(String serviceCallAction, String... params) {
        this.serviceCallAction = serviceCallAction;
        this.params = params;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }

    public String[] getParams() {
        return params;
    }
}
