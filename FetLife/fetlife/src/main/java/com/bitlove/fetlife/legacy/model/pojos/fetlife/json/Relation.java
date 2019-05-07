
package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Relation {

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("id")
    private String id;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("target_member")
    private Member targetMember;


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

    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

    @JsonProperty("target_member")
    public Member getTargetMember() {
        return targetMember;
    }

    @JsonProperty("target_member")
    public void setTargetMember(Member targetMember) {
        this.targetMember = targetMember;
    }

}
