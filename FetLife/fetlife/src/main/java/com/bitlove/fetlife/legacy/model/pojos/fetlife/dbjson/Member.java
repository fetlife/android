package com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson;

import android.net.Uri;
import android.text.TextUtils;

import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Avatar;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.AvatarVariants;
import com.bitlove.fetlife.legacy.util.ServerIdUtil;
import com.bitlove.fetlife.legacy.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: clean up the POJOs and define relations
@Table(database = FetLifeDatabase.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member extends BaseModel {

    public static final String VALUE_FRIEND = "friend";
    public static final String VALUE_FRIEND_WITHOUT_FOLLOWING = "friend_without_following";
    public static final String VALUE_FOLLOWING = "following";
    public static final String VALUE_FRIEND_REQUEST_PENDING = "friend_request_pending";
    public static final String VALUE_FRIEND_REQUEST_SENT = "friend_request_sent";
    public static final String VALUE_FOLLOWING_FRIEND_REQUEST_SENT = "following_friend_request_sent";
    public static final String VALUE_FOLLOWING_FRIEND_REQUEST_PENDING = "following_friend_request_pending";

    private static final String SEPARATOR_LOOKING_FOR = ";";

    @JsonProperty("id")
    @Column
    @PrimaryKey(autoincrement = false)
    private String id;

    @JsonIgnore
    @Column
    private String serverId;

    @JsonProperty("about")
    @Column
    private String about;

    @JsonIgnore
    @Column
    private String accessToken;

    @JsonProperty("administrative_area")
    @Column
    private String administrativeArea;

    @JsonIgnore
    @Column
    private String avatarLink;

    @JsonProperty("city")
    @Column
    private String city;

    @JsonProperty("country")
    @Column
    private String country;

    @JsonProperty("is_followable")
    @Column
    private boolean followable;

    @JsonProperty("url")
    @Column
    private String link;

    @JsonProperty("meta_line")
    @Column
    private String metaInfo;

    @JsonProperty("nickname")
    @Column
    private String nickname;

    @JsonProperty("notification_token")
    @Column
    private String notificationToken;

    @JsonIgnore
    @Column
    private String refreshToken;

    @JsonProperty("relation_with_me")
    @Column
    private String relationWithMe;

    @JsonProperty("sexual_orientation")
    @Column
    private String sexualOrientation;

    //Db Only
    @Column
    private String lookingForRawString;

    @Column
    @JsonIgnore
    private long lastViewedAt;

    //Json Only
    @JsonProperty("avatar")
    private Avatar avatar;

    @JsonProperty("looking_for")
    private List<String> lookingFor;

    //In Memory only
    @JsonIgnore
    private List<Relationship> relationships = new ArrayList<>();


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
        this.htmlAbout = null;
    }

    @JsonIgnore
    private CharSequence htmlAbout;

    public CharSequence getHtmlAbout() {
        if (htmlAbout == null && about != null) {
            this.htmlAbout = StringUtil.parseMarkedHtml(about);
        }
        return htmlAbout;
    }

    public String getServerId() {
        if (serverId != null || link == null) return serverId;
        serverId = Uri.parse(link).getLastPathSegment();
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public long getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(long lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        if (avatar != null) {
            AvatarVariants variants = avatar.getVariants();
            if (variants != null) {
                setAvatarLink(variants.getMedium());
            }
        }
    }

    @JsonIgnore
    public String getAvatarLink() {
        return avatarLink;
    }

    @JsonIgnore
    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    @JsonIgnore
    public Picture getAvatarPicture() {
        return avatar.getAsMediumPicture(Member.this);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<String> getLookingFor() {
        if (lookingFor == null && lookingForRawString != null) {
            lookingFor = Arrays.asList(lookingForRawString.split(SEPARATOR_LOOKING_FOR));
        }
        return lookingFor;
    }

    public void setLookingFor(List<String> lookingFor) {
        this.lookingFor = lookingFor;
        if (lookingFor != null) {
            String lookingForText = "";
            for (String lookingForElement : lookingFor) {
                lookingForText += SEPARATOR_LOOKING_FOR + lookingForElement;
            }
            setLookingForRawString(lookingForText);
        }
    }

    public String getLookingForRawString() {
        return lookingForRawString;
    }

    public void setLookingForRawString(String lookingForRawString) {
        this.lookingForRawString = lookingForRawString;
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRelationWithMe() {
        return relationWithMe;
    }

    public void setRelationWithMe(String relationWithMe) {
        this.relationWithMe = relationWithMe;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships.clear();
        if (relationships != null) {
            this.relationships.addAll(relationships);
        }
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public void setSexualOrientation(String sexualOrientation) {
        this.sexualOrientation = sexualOrientation;
    }

    public boolean isFollowable() {
        return followable;
    }

    public void setFollowable(boolean followable) {
        this.followable = followable;
    }

    public boolean mergeSave() {
        Member savedMember = Member.loadMember(id);
        if (savedMember != null) {
            if (TextUtils.isEmpty(about)) {
                setAbout(savedMember.about);
            }
            if (TextUtils.isEmpty(country)) {
                setCountry(savedMember.country);
            }
            if (TextUtils.isEmpty(administrativeArea)) {
                setAdministrativeArea(savedMember.administrativeArea);
            }
            if (TextUtils.isEmpty(city)) {
                setCity(savedMember.city);
            }
            if (TextUtils.isEmpty(sexualOrientation)) {
                setSexualOrientation(savedMember.sexualOrientation);
            }
            if (TextUtils.isEmpty(lookingForRawString)) {
                setLookingForRawString(savedMember.lookingForRawString);
            }
            if (savedMember.getLastViewedAt() > lastViewedAt) {
                lastViewedAt = savedMember.getLastViewedAt();
            }
            if (!isFollowable()) {
                setFollowable(savedMember.isFollowable());
            }
        }
        return internalSave();
    }

    public boolean isDetailRetrieved() {
        return getCountry() != null;
    }

    @Override
    public boolean save() {
        return mergeSave();
    }

    public boolean internalSave() {
        Member savedMember = Member.loadMember(id);
        if (savedMember != null) {
            if (TextUtils.isEmpty(accessToken)) {
                setAccessToken(savedMember.accessToken);
            }
            if (TextUtils.isEmpty(refreshToken)) {
                setRefreshToken(savedMember.refreshToken);
            }
        }
        return super.save();
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public boolean hasIgnoreMarker(AnnotatedMember m) {
                return m.getDeclaringClass() == BaseModel.class || super.hasIgnoreMarker(m);
            }
        }).writeValueAsString(this);
    }

    public static Member loadMember(String memberId) {
        if (ServerIdUtil.containsServerId(memberId)) {
            memberId = ServerIdUtil.getLocalId(memberId);
        }
        Member member = new Select().from(Member.class).where(Member_Table.id.is(memberId)).querySingle();
        if (member == null) {
            return null;
        }
        return member;
    }

}
