package com.bitlove.fetlife.legacy.model.pojos.fetlife.db;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class RelationReference extends BaseModel {

    public static final int VALUE_RELATIONTYPE_FRIEND = 0;
    public static final int VALUE_RELATIONTYPE_FOLLOWER = 10;
    public static final int VALUE_RELATIONTYPE_FOLLOWING = 20;

    @Column
    @PrimaryKey(autoincrement = false)
    private String id;

    @Column
    private String nickname;

    @Column
    private int relationType;

    @Column
    private String userId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRelationType() {
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
