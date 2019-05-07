package com.bitlove.fetlife.legacy.model.pojos.fetlife.db;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.bitlove.fetlife.legacy.util.DateUtil;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class GroupReference extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = false)
    private String id;

    @Column
    private String updatedAt;

    @Column
    private long date;

    @Column
    private String userId;

    public String getUpdatedAt() {
        return updatedAt;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
