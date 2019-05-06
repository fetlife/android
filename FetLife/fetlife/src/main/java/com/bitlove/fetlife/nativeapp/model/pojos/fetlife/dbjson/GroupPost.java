package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson;

import com.bitlove.fetlife.nativeapp.model.db.FetLifeDatabase;
import com.bitlove.fetlife.nativeapp.util.DateUtil;
import com.bitlove.fetlife.nativeapp.util.ServerIdUtil;
import com.bitlove.fetlife.nativeapp.util.StringUtil;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONObject;

import java.util.Map;

//TODO: updated at attribute?

@Table(database = FetLifeDatabase.class)
public class GroupPost extends BaseModel {

    public static GroupPost loadGroupPost(String groupPostId) {
        try {
            if (ServerIdUtil.containsServerId(groupPostId)) {
                groupPostId = ServerIdUtil.getLocalId(groupPostId);
            }
            GroupPost groupPost = new Select().from(GroupPost.class).where(GroupPost_Table.id.is(groupPostId)).querySingle();
            return groupPost;
        } catch (Throwable t) {
            Crashlytics.logException(t);
            return null;
        }
    }

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @JsonProperty("body")
    @Column
    private String body;

    @JsonProperty("comment_count")
    @Column
    private Integer commentCount;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("last_comment_at")
    @Column
    private String updatedAt;

    @JsonProperty("created_at")
    @Column
    private String createdAt;

    @JsonProperty("group")
    private Group group;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("title")
    @Column
    private String title;

    @JsonProperty("url")
    @Column
    private String url;

    @JsonProperty("body_entities")
    private Object bodyEntities;

    @Column
    @JsonIgnore
    private String entitiesJson;

    @Column
    @JsonIgnore
    private String avatarLink;

    @Column
    @JsonIgnore
    private long date;

    @Column
    @JsonIgnore
    private String draftMessage;

    @Column
    @JsonIgnore
    private String memberId;

    @Column
    @JsonIgnore
    private String groupId;

    @Column
    @JsonIgnore
    private String memberLink;

    @Column
    @JsonIgnore
    private String nickname;

    @Column
    @JsonProperty("current_member_is_following")
    private boolean followed;

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
    private CharSequence htmlBody;

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

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        if (updatedAt != null) {
            try {
                setDate(DateUtil.parseDate(updatedAt));
            } catch (Exception e) {
            }
        }
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        if (date == 0 && createdAt != null) {
            try {
                setDate(DateUtil.parseDate(createdAt));
            } catch (Exception e) {
            }
        }
    }

    @JsonProperty("group")
    public Group getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(Group group) {
        this.group = group;
        if (group != null) {
            setGroupId(group.getId());
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    //current_member_is_following

    @JsonProperty("member")
    public Member getMember() {
        if (member == null) {
            member = Member.loadMember(memberId);
        }
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
        if (member != null) {
            setMemberId(member.getId());
            setNickname(member.getNickname());
            setAvatarLink(member.getAvatarLink());
            setMemberLink(member.getLink());
        }
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDraftMessage() {
        return draftMessage;
    }

    public void setDraftMessage(String draftMessage) {
        this.draftMessage = draftMessage;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberLink() {
        return memberLink;
    }

    public void setMemberLink(String memberLink) {
        this.memberLink = memberLink;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Object getBodyEntities() {
        return bodyEntities;
    }

    public void setBodyEntities(Object bodyEntities) {
        this.bodyEntities = bodyEntities;
        if (bodyEntities != null) {
            entitiesJson = new JSONObject((Map)bodyEntities).toString();
        }
    }

    public String getEntitiesJson() {
        return entitiesJson;
    }

    public void setEntitiesJson(String entitiesJson) {
        this.entitiesJson = entitiesJson;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

}