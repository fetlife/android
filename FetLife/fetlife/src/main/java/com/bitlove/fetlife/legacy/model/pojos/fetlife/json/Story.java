
package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.util.EnumUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Story {

    //Supported Stories
    public static enum FeedStoryType {

        LIKE_CREATED, // 182660
        FRIEND_CREATED, // 31992
        COMMENT_CREATED, // 18851
        FOLLOW_CREATED, // 15933
        GROUP_COMMENT_CREATED, // 9340
        PICTURE_CREATED, // 7063
        VIDEO_CREATED, //139
        PEOPLE_INTO_CREATED, // 6195
        GROUP_MEMBERSHIP_CREATED, // 5102
        POST_COMMENT_CREATED, // 2111
        UPDATED_ABOUT, // 1743 //
        PROFILE_UPDATED,
        RSVP_CREATED, // 1600
        STATUS_COMMENT_CREATED, // 1513
        STATUS_CREATED, // 1445
        WALL_POST_CREATED, // 1238
        GROUP_POST_CREATED, // 1073
        //        USER_SIGNED_UP, // 696
        POST_CREATED, // 448
        VIDEO_COMMENT_CREATED, // 375
//        UPDATED_FETISH_STATUS, // 309
//        LOCATION_UPDATED, // 276
//        POST_UPDATED, // 262
//        ROLE_UPDATED, // 257
//        DS_RELATIONSHIP_CREATED, // 254
//        RELATIONSHIP_CREATED, // 215
//        RSVP_UPDATED, //204
//        NICKNAME_UPDATED, //146
//        SEXUAL_ORIENTATION_UPDATED, //118
//        UPDATED_WEBSITES, //107
//        RELATIONSHIP_UPDATED, //80
//        EVENT_CREATED, //77
//        SUPPORTED_FETLIFE, //71
//        DS_RELATIONSHIP_UPDATED, //45
//        VOTE_CREATED, //34
//        SEX_UPDATED, //26
//        PROMOTED_TO_GROUP_LEADER, //10
//        VOLUNTEERED_TO_BE_GROUP_LEADER, //3
//        IMPROVEMENT_COMMENT_CREATED, //1
//        INVITED_USER_SIGNED_UP //1
    }

    public FeedStoryType getType() {
        return EnumUtil.safeParse(FeedStoryType.class, name);
    }

    @JsonProperty("events")
    private List<FeedEvent> events = new ArrayList<FeedEvent>();

    @JsonProperty("name")
    private String name;

    @JsonProperty("events")
    public List<FeedEvent> getEvents() {
        return events;
    }

    @JsonProperty("events")
    public void setEvents(List<FeedEvent> events) {
        this.events = events;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

}
