
package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Writing;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Target {

    @JsonProperty("comment")
    private Comment comment;

    @JsonProperty("group_membership")
    private GroupMembership groupMembership;

    @JsonProperty("group_post")
    private GroupPost groupPost;

    @JsonProperty("love")
    private Love love;

    @JsonProperty("people_into")
    private PeopleInto peopleInto;

    @JsonProperty("picture")
    private Picture picture;

    @JsonProperty("relation")
    private Relation relation;

    @JsonProperty("rsvp")
    private Rsvp rsvp;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("video")
    private Video video;

    @JsonProperty("wall_post")
    private WallPost wallPost;

    @JsonProperty("writing")
    private Writing writing;

    @JsonProperty("comment")
    public Comment getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @JsonProperty("group_membership")
    public GroupMembership getGroupMembership() {
        return groupMembership;
    }

    @JsonProperty("group_membership")
    public void setGroupMembership(GroupMembership groupMembership) {
        this.groupMembership = groupMembership;
    }

    @JsonProperty("group_post")
    public GroupPost getGroupPost() {
        return groupPost;
    }

    @JsonProperty("group_post")
    public void setGroupPost(GroupPost groupPost) {
        this.groupPost = groupPost;
    }

    @JsonProperty("love")
    public Love getLove() {
        return love;
    }

    @JsonProperty("love")
    public void setLove(Love love) {
        this.love = love;
    }

    @JsonProperty("people_into")
    public PeopleInto getPeopleInto() {
        return peopleInto;
    }

    @JsonProperty("people_into")
    public void setPeopleInto(PeopleInto peopleInto) {
        this.peopleInto = peopleInto;
    }

    @JsonProperty("picture")
    public Picture getPicture() {
        return picture;
    }

    @JsonProperty("picture")
    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @JsonProperty("relation")
    public Relation getRelation() {
        return relation;
    }

    @JsonProperty("relation")
    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    @JsonProperty("rsvp")
    public Rsvp getRsvp() {
        return rsvp;
    }

    @JsonProperty("rsvp")
    public void setRsvp(Rsvp rsvp) {
        this.rsvp = rsvp;
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
