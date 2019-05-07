package com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Thumbnail;
import com.bitlove.fetlife.legacy.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class Video extends BaseModel {

    public static Video loadVideo(String videoId) {
        Video video = new Select().from(Video.class).where(Video_Table.id.is(videoId)).querySingle();
        if (video == null) {
            return null;
        }
        return video;
    }


    //Db and Json

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("body")
    private String body;

    @Column
    @JsonProperty("comment_count")
    private Integer commentCount;

    @Column
    @JsonProperty("content_type")
    private String contentType;

    @Column
    @JsonProperty("created_at")
    private String createdAt;

    @Column
    @JsonProperty("is_loved_by_me")
    private Boolean isLovedByMe;

    @Column
    @JsonProperty("love_count")
    private Integer loveCount;

    //Db only

    @Column
    @JsonIgnore
    private String thumbUrl;

    @Column
    @JsonIgnore
    private long date;

    //Json only

    @JsonProperty("member")
    private Member member;

    @JsonProperty("thumbnail")
    private Thumbnail thumbnail;

    @Column
    @JsonProperty("url")
    private String url;

    @Column
    @JsonProperty("video_url")
    private String videoUrl;


    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("comment_count")
    public Integer getCommentCount() {
        return commentCount;
    }

    @JsonProperty("comment_count")
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
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
        if (createdAt != null) {
            try {
                setDate(DateUtil.parseDate(createdAt));
            } catch (Exception e) {
            }
        }
    }

    @JsonIgnore
    public long getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(long date) {
        this.date = date;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
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

    @JsonIgnore
    public String getThumbUrl() {
        return thumbUrl;
    }

    @JsonIgnore
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @JsonProperty("thumbnail")
    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    @JsonProperty("thumbnail")
    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
        if (thumbnail != null) {
            setThumbUrl(thumbnail.getVariants().get150().getUrl());
        }
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("video_url")
    public String getVideoUrl() {
        return videoUrl;
    }

    @JsonProperty("video_url")
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @JsonProperty("is_loved_by_me")
    public Boolean isLovedByMe() {
        return isLovedByMe;
    }

    @JsonProperty("is_loved_by_me")
    public void setLovedByMe(Boolean isLovedByMe) {
        this.isLovedByMe = isLovedByMe;
    }

}