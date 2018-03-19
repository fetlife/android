package com.bitlove.fetlife;
import com.crashlytics.android.answers.Answers;

public class AnswersWrapper {
    private Answers answers;
    protected AnswersWrapper() {
        answers = Answers.getInstance();
    }
    private static AnswersWrapper instance = null;
    public static AnswersWrapper getInstance(){
        if(instance == null){
            instance = new AnswersWrapper();
        }
        return instance;
    }

    public void logCustom(CustomEventWrapper customEventWrapper){
        answers.logCustom(customEventWrapper.customEvent);
    }
}