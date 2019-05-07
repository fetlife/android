
package com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@Table(database = FetLifeDatabase.class)
public class Relationship extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("created_at")
    private String createdAt;

    @Column
    @JsonProperty("status")
    private String status;

    @JsonProperty("target_member")
    private Member targetMember;

    @Column
    @JsonProperty("type")
    private String type;

    //Db Only
    @Column
    @JsonIgnore
    private String memberId;

    @Column
    @JsonIgnore
    private String targetMemberId;

    @Column
    @JsonIgnore
    private String targetMemberNickname;


    //Helper loader
    public static void loadForMember(Member member) {
        if (member == null) {
            return;
        }
        List<Relationship> relationships = new Select().from(Relationship.class).where(Relationship_Table.memberId.is(member.getId())).queryList();
        member.setRelationships(relationships);
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

    @JsonIgnore
    public String getMemberId() {
        return memberId;
    }

    @JsonIgnore
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    @JsonIgnore
    public String getStatus() {
        return status;
    }

    @JsonIgnore
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("target_member")
    public Member getTargetMember() {
        if (targetMember == null) {
            targetMember = Member.loadMember(targetMemberId);
        }
        return targetMember;
    }

    @JsonProperty("target_member")
    public void setTargetMember(Member targetMember) {
        this.targetMember = targetMember;
        if (targetMember != null) {
            setTargetMemberId(targetMember.getId());
            setTargetMemberNickname(targetMember.getNickname());
        }
    }

    @JsonIgnore
    public String getTargetMemberId() {
        return targetMemberId;
    }

    @JsonIgnore
    public void setTargetMemberId(String targetMemberId) {
        this.targetMemberId = targetMemberId;
    }

    @JsonIgnore
    public String getTargetMemberNickname() {
        return targetMemberNickname;
    }

    @JsonIgnore
    public void setTargetMemberNickname(String targetMemberNickname) {
        this.targetMemberNickname = targetMemberNickname;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }
}
