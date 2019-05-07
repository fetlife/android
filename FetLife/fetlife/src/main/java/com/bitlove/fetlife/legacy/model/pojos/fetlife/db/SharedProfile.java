package com.bitlove.fetlife.legacy.model.pojos.fetlife.db;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.FriendRequestScreenModelObject;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class SharedProfile extends BaseModel implements FriendRequestScreenModelObject {

    @Column
    @PrimaryKey(autoincrement = false)
    private String memberId;

    @Column
    private boolean pending;


    //Load helper
    public Member getMember() {
        return Member.loadMember(memberId);
    }


    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
