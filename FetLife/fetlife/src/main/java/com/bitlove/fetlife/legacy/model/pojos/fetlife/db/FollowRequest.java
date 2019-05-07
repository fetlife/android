package com.bitlove.fetlife.legacy.model.pojos.fetlife.db;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
//Db only
public class FollowRequest extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = false)
    private String memberId;

    @Column
    private boolean follow = true;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}


