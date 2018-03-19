package com.bitlove.fetlife;


public class CustomEventWrapper {
    String message;
    public CustomEventWrapper(String string) {
        message = string;
    }
    public CustomEventWrapper putCustomAttribute(String key, String value){
        message += "key: " + key;
        message += ", ";
        message += "value: " + value;
        message += "; ";
        return this;
    }
    public CustomEventWrapper putCustomAttribute(String key, long value) {
        message += "key: " + key;
        message += ", ";
        message += "value: " + Long.toString(value);
        message += "; ";
        return this;
    }
}