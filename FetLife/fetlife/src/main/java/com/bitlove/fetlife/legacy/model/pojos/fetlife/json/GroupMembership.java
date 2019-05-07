package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupMembership {

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("last_visited_at")
    private String lastVisitedAt;

    @JsonProperty("group")
    private Group group;

    @JsonProperty("id")
    private String id;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("group")
    public Group getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(Group group) {
        this.group = group;
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

    public String getLastVisitedAt() {
        return lastVisitedAt;
    }

    public void setLastVisitedAt(String lastVisitedAt) {
        this.lastVisitedAt = lastVisitedAt;
    }
}