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

@Table(database = FetLifeDatabase.class)
public class Group extends BaseModel {

    public static Group loadGroup(String groupId) {
        try {
            if (ServerIdUtil.containsServerId(groupId)) {
                groupId = ServerIdUtil.getLocalId(groupId);
            }
            Group group = new Select().from(Group.class).where(Group_Table.id.is(groupId)).querySingle();
            return group;
        } catch (Throwable t) {
            Crashlytics.logException(t);
            return null;
        }
    }

    //Primary key
    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    //json only
    @JsonProperty("content_type")
    private String contentType;

    //Column and Json
    @Column
    @JsonProperty("updated_at")
    private String updatedAt;

    @Column
    @JsonProperty("created_at")
    private String createdAt;

    @Column
    @JsonProperty("description")
    private String description;

    @Column
    @JsonProperty("member_count")
    private Integer memberCount;

    @Column
    @JsonProperty("current_member_is_member")
    private boolean memberOfGroup;

    @Column
    @JsonProperty("name")
    private String name;

    @Column
    @JsonProperty("rules")
    private String rules;

    @Column
    @JsonProperty("url")
    private String url;

    //dbonly

    @Column
    private long date;

    @Column
    private boolean detailLoaded;

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
        this.htmlDescription = null;
    }

    @JsonIgnore
    private CharSequence htmlDescription = null;

    public CharSequence getHtmlDescription() {
        if (htmlDescription == null && description != null) {
            this.htmlDescription = StringUtil.parseMarkedHtml(description);
        }
        return htmlDescription;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("member_count")
    public Integer getMemberCount() {
        return memberCount;
    }

    @JsonProperty("member_count")
    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("rules")
    public String getRules() {
        return rules;
    }

    @JsonProperty("rules")
    public void setRules(String rules) {
        this.rules = rules;
        this.htmlRules = null;
    }

    @JsonIgnore
    private CharSequence htmlRules = null;

    public CharSequence getHtmlRules() {
        if (htmlRules == null && rules != null) {
            this.htmlRules = StringUtil.parseMarkedHtml(rules);
        }
        return htmlRules;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isMemberOfGroup() {
        return memberOfGroup;
    }

    public void setMemberOfGroup(boolean memberOfGroup) {
        this.memberOfGroup = memberOfGroup;
    }

    @Override
    public boolean save() {
        Group currentGroup = Group.loadGroup(id);
        if (currentGroup!= null) {
            if (currentGroup.getDate() > getDate()) {
                setDate(currentGroup.getDate());
            }
            if (currentGroup.isDetailLoaded()) {
                setDetailLoaded(true);
            }
            if (!isDetailLoaded() && currentGroup.isMemberOfGroup()) {
                setMemberOfGroup(true);
            }
        }
        return super.save();
    }

    public boolean isDetailLoaded() {
        return detailLoaded;
    }

    public void setDetailLoaded(boolean detailLoaded) {
        this.detailLoaded = detailLoaded;
    }
}