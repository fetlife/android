package com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.bitlove.fetlife.legacy.util.DateUtil;
import com.bitlove.fetlife.legacy.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class Status extends BaseModel {

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
    @JsonIgnore
    private long date;

    @Column
    @JsonProperty("is_loved_by_me")
    private boolean isLovedByMe;

    @Column
    @JsonProperty("love_count")
    private Integer loveCount;

    @Column
    @JsonProperty("url")
    private String url;

    //Db only
    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("created_at")
    private String createdAt;

    //Json only
    @JsonProperty("member")
    private Member member;


    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
        this.htmlBody = null;
    }

    @JsonIgnore
    private CharSequence htmlBody = null;

    public CharSequence getHtmlBody() {
        if (htmlBody == null && body != null) {
            this.htmlBody = StringUtil.parseMarkedHtml(body);
        }
        return htmlBody;
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

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("is_loved_by_me")
    public boolean isLovedByMe() {
        return isLovedByMe;
    }

    @JsonProperty("is_loved_by_me")
    public void setLovedByMe(boolean lovedByMe) {
        isLovedByMe = lovedByMe;
    }

}