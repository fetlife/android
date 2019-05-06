
package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.util.EnumUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rsvp {

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("event")
    private Event event;

    @JsonProperty("id")
    private String id;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("status")
    private String status;

    @JsonProperty("url")
    private String url;


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

    @JsonProperty("event")
    public Event getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(Event event) {
        this.event = event;
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

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }


    public RsvpStatus getRsvpStatus() {
        return EnumUtil.safeParse(RsvpStatus.class, status);
    }

    public enum RsvpStatus {
        YES,
        MAYBE,
        BANNED,
        NO;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
