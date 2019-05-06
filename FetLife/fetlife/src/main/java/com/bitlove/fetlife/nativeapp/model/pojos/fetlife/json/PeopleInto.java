package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import android.content.Context;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PeopleInto {

    @JsonProperty("activity")
    private String activity;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("fetish")
    private Fetish fetish;

    @JsonProperty("id")
    private String id;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("status")
    private String status;

    //Transform methods
    public enum Activity {
        _NO_STATUS_,
        GIVING,
        RECEIVING,
        WATCHING,
        WEARING,
        WATCHING_OTHERS_WEAR,
        EVERYTHING_TO_DO_WITH_IT;

        public static Activity fromString(String text) {
            if (text == null || text.trim().length() == 0) {
                return _NO_STATUS_;
            }
            try {
                return Activity.valueOf(text.toUpperCase().replaceAll(" ", "_"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public String toString(Context context) {
            switch (this) {
                case GIVING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_giving);
                case RECEIVING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_receiving);
                case WATCHING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_watching);
                case WEARING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_wearing);
                case WATCHING_OTHERS_WEAR:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_watching_others_wear);
                case EVERYTHING_TO_DO_WITH_IT:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_everything_to_do_with_it);
                case _NO_STATUS_:
                    return "";
            }
            return null;
        }
    }

    public enum Status {

        INTO,
        CURIOUS_ABOUT,
        SOFT_LIMIT,
        HARD_LIMIT;

        public static Status fromString(String text) {
            try {
                return Status.valueOf(text.toUpperCase().replaceAll(" ", "_"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public String toString(Context context, String nickName, String fetishName, String statusText) {
            switch (this) {
                case INTO:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_into, nickName, statusText, fetishName);
                case CURIOUS_ABOUT:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_curious_about, nickName, statusText, fetishName);
                case SOFT_LIMIT:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_soft_limit, nickName, statusText, fetishName);
                case HARD_LIMIT:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_hard_limit, nickName, statusText, fetishName);
            }
            return null;
        }
    }

    @JsonIgnore
    public Status getActivityEnum() {
        return Status.fromString(status);
    }

    @JsonIgnore
    public Activity getStatusEnum() {
        return Activity.fromString(activity);
    }

    //Getters/Setters
    @JsonProperty("activity")
    public String getActivity() {
        return activity;
    }

    @JsonProperty("activity")
    public void setActivity(String activity) {
        this.activity = activity;
    }


    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("fetish")
    public Fetish getFetish() {
        return fetish;
    }

    @JsonProperty("fetish")
    public void setFetish(Fetish fetish) {
        this.fetish = fetish;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

}