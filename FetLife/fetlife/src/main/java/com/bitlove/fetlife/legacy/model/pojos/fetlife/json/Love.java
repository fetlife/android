
package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Love {

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("id")
    private String id;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("target_id")
    private String targetId;

    @JsonProperty("target_type")
    private String targetType;


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

    @JsonProperty("target_id")
    public String getTargetId() {
        return targetId;
    }

    @JsonProperty("target_id")
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    @JsonProperty("target_type")
    public String getTargetType() {
        return targetType;
    }

    @JsonProperty("target_type")
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

}
