package com.bitlove.fetlife;

import android.util.Log;

public class AnswersWrapper {
    protected AnswersWrapper() {
    }
    private static AnswersWrapper instance = null;
    private static final String TAG = "Answers";
    public static AnswersWrapper getInstance(){
        if(instance == null){
            instance = new AnswersWrapper();
        }
        return instance;
    }
    public void logCustom(CustomEventWrapper customEventWrapper){
        Log.d(TAG, customEventWrapper.message);
    }

}