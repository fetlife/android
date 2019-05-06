
package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Writing;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SecondaryTarget {

    @JsonProperty("group_post")
    private GroupPost groupPost;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("picture")
    private Picture picture;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("video")
    private Video video;

    @JsonProperty("wall_post")
    private WallPost wallPost;

    @JsonProperty("writing")
    private Writing writing;

    @JsonProperty("group")
    private Group group;

    @JsonProperty("group")
    public Group getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(Group group) {
        this.group = group;
    }

    @JsonProperty("group_post")
    public GroupPost getGroupPost() {
        return groupPost;
    }

    @JsonProperty("group_post")
    public void setGroupPost(GroupPost groupPost) {
        this.groupPost = groupPost;
    }

    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

    @JsonProperty("picture")
    public Picture getPicture() {
        return picture;
    }

    @JsonProperty("picture")
    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("video")
    public Video getVideo() {
        return video;
    }

    @JsonProperty("video")
    public void setVideo(Video video) {
        this.video = video;
    }

    @JsonProperty("wall_post")
    public WallPost getWallPost() {
        return wallPost;
    }

    @JsonProperty("wall_post")
    public void setWallPost(WallPost wallPost) {
        this.wallPost = wallPost;
    }

    @JsonProperty("writing")
    public Writing getWriting() {
        return writing;
    }

    @JsonProperty("writing")
    public void setWriting(Writing writing) {
        this.writing = writing;
    }

}
