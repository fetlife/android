package com.bitlove.fetlife.legacy.event;

public class ServiceCallFinishedEvent {

    private String[] params;
    private String serviceCallAction;
    private int itemCount;

    public ServiceCallFinishedEvent(String serviceCallAction, int itemCount, String... params) {
        this.serviceCallAction = serviceCallAction;
        this.itemCount = itemCount;
        this.params = params;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String[] getParams() {
        return params;
    }
}
