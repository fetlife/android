package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostComment {

    @JsonProperty("body")
    private String body;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("id")
    private String id;

    @JsonProperty("is_loved_by_me")
    private Boolean isLovedByMe;

    @JsonProperty("love_count")
    private Integer loveCount;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("url")
    private String url;


    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
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

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("is_loved_by_me")
    public Boolean getIsLovedByMe() {
        return isLovedByMe;
    }

    @JsonProperty("is_loved_by_me")
    public void setIsLovedByMe(Boolean isLovedByMe) {
        this.isLovedByMe = isLovedByMe;
    }

    @JsonProperty("love_count")
    public Integer getLoveCount() {
        return loveCount;
    }

    @JsonProperty("love_count")
    public void setLoveCount(Integer loveCount) {
        this.loveCount = loveCount;
    }

    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

}