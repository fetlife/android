package com.bitlove.fetlife.legacy.model.pojos.fetlife.db;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = FetLifeDatabase.class)
public class EventRsvpReference extends BaseModel {

    public static final int VALUE_RSVPTYPE_GOING = 0;
    public static final int VALUE_RSVPTYPE_MAYBE = 10;

    @Column
    @PrimaryKey(autoincrement = false)
    private String id;

    @Column
    private String nickname;

    @Column
    private int rsvpType;

    @Column
    private String eventId;


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

    public int getRsvpType() {
        return rsvpType;
    }

    public void setRsvpType(int rsvpType) {
        this.rsvpType = rsvpType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
