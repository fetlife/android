
package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bitlove.fetlife.nativeapp.model.db.FetLifeDatabase;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.Rsvp;
import com.bitlove.fetlife.nativeapp.util.DateUtil;
import com.bitlove.fetlife.nativeapp.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.SimpleDateFormat;

@Table(database = FetLifeDatabase.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event extends BaseModel implements Comparable<Event>, ClusterItem {

    public static final double NOT_SET = 0.0d;

    public static Event loadEvent(String eventId) {
        try {
            Event event = new Select().from(Event.class).where(Event_Table.id.is(eventId)).querySingle();
            return event;
        } catch (Throwable t) {
            //db not available
            return null;
        }
    }

    @Column
    @JsonProperty("address")
    private String address;

    @Column
    @JsonProperty("description")
    private String description;

    @Column
    @JsonProperty("end_date_time")
    private String endDateTime;

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("location")
    private String location;

    @Column
    @JsonProperty("name")
    private String name;

    @Column
    @JsonProperty("start_date_time")
    private String startDateTime;

    @Column
    @JsonProperty("tagline")
    private String tagline;

    @Column
    @JsonProperty("url")
    private String url;

    @Column
    @JsonProperty("latitude")
    private double latitude = NOT_SET;

    @Column
    @JsonProperty("longitude")
    private double longitude = NOT_SET;

    @Column
    @JsonProperty("cost")
    private String cost;

    @Column
    @JsonProperty("dress_code")
    private String dressCode;

    //db only

    @Column
    @JsonIgnore
    private Rsvp.RsvpStatus rsvpStatus;

    @Column
    @JsonIgnore
    private String memberId;

    @Column
    @JsonIgnore
    private long date;

    //Json only
    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("member")
    private Member member;

    @JsonProperty("distance")
    private double distance;

    //local
    @JsonIgnore
    @Column
    private long roughtStartDate;

    @JsonIgnore
    private long roughtEndDate;

    @JsonIgnore
    private long endDate;

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("cost")
    public String getCost() {
        return cost;
    }

    @JsonProperty("cost")
    public void setCost(String cost) {
        this.cost = cost;
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
    private CharSequence htmlDescription;

    public CharSequence getHtmlDescription() {
        if (htmlDescription == null && description != null) {
            this.htmlDescription = StringUtil.parseMarkedHtml(description);
        }
        return htmlDescription;
    }

    @JsonProperty("dress_code")
    public String getDressCode() {
        return dressCode;
    }

    @JsonProperty("dress_code")
    public void setDressCode(String dressCode) {
        this.dressCode = dressCode;
    }

    @JsonProperty("end_date_time")
    public String getEndDateTime() {
        return endDateTime;
    }

    @JsonProperty("end_date_time")
    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
        if (endDateTime != null) {
            endDate = DateUtil.parseDate(endDateTime,true);
            roughtEndDate = DateUtil.addRoughTimeOffset(endDate, longitude);
        }
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("start_date_time")
    public String getStartDateTime() {
        return startDateTime;
    }

    public Rsvp.RsvpStatus getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(Rsvp.RsvpStatus rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }

    @JsonProperty("start_date_time")
    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
        if (startDateTime != null) {
            date = DateUtil.parseDate(startDateTime,true);
            roughtStartDate = DateUtil.addRoughTimeOffset(date, longitude);
        }
    }

    @JsonProperty("tagline")
    public String getTagline() {
        return tagline;
    }

    @JsonProperty("tagline")
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(getLatitude(),getLongitude());
    }

    public void setPosition(LatLng position) {
        setLatitude(position.latitude);
        setLongitude(position.longitude);
    }

    @Override
    public String getTitle() {
        return name != null ? name : "";
    }

    @Override
    public String getSnippet() {
        String time = getStartDateTime();
        String snippet = time != null ? SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(time,true)) : "";
        return snippet;
    }

    public long getRoughtStartDate() {
        return roughtStartDate;
    }

    public void setRoughtStartDate(long roughtStartDate) {
        this.roughtStartDate = roughtStartDate;
    }

    public long getRoughtEndDate() {
        return roughtEndDate;
    }

    @Override
    public int hashCode() {
        return (int) roughtStartDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Event)) {
            return false;
        }

        Event otherEvent = (Event) obj;
        return id.equals(otherEvent.id);
    }

    @Override
    public int compareTo(@NonNull Event o) {
        long diff = roughtStartDate - o.roughtStartDate;
        Log.d("COMPARE", roughtStartDate + " ? " + o.roughtStartDate + " = " + diff);
        if (diff == 0) return 0;
        if (diff < 0) return -1;
        return 1;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    private boolean mergeSave() {
        if (rsvpStatus == null) {
            Event savedEvent = loadEvent(id);
            if (savedEvent != null) {
                setRsvpStatus(savedEvent.getRsvpStatus());
            }
        }
        return internalSave();
    }

    @Override
    public boolean save() {
        return mergeSave();
    }

    private boolean internalSave() {
        return super.save();
    }

}
