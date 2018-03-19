package com.bitlove.fetlife;
import com.crashlytics.android.answers.CustomEvent;


public class CustomEventWrapper {
    CustomEvent customEvent;
    public CustomEventWrapper(String string) {
        customEvent =  new CustomEvent(string);
    }

    public CustomEventWrapper putCustomAttribute(String key, String value){
        customEvent = customEvent.putCustomAttribute(key, value);
        return this;
    }
    public CustomEventWrapper putCustomAttribute(String key, long value){
        customEvent = customEvent.putCustomAttribute(key, value);
        return this;
    }
}