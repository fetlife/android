
package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson;

import android.text.Html;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.nativeapp.model.db.FetLifeDatabase;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.PictureVariants;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Table(database = FetLifeDatabase.class)
public class Picture extends BaseModel {

    @Column
    @JsonProperty("body")
    private String body;

    @Column
    @JsonProperty("comment_count")
    private int commentCount;

    @Column
    @JsonProperty("content_type")
    private String contentType;

    @Column
    @JsonProperty("created_at")
    private String createdAt;

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("is_loved_by_me")
    private boolean isLovedByMe;

    @Column
    @JsonProperty("love_count")
    private int loveCount;

    @Column
    @JsonProperty("url")
    private String url;

    //Db Only
    @Column
    @JsonIgnore
    private long date;

    @Column
    @JsonIgnore
    private String memberId;

    @Column
    @JsonIgnore
    private String displayUrl;

    @Column
    @JsonIgnore
    private String thumbUrl;

    @Column
    @JsonIgnore
    public boolean onShareList;

    @Column
    @JsonIgnore
    private long lastViewedAt;

    //Json only
    @JsonProperty("member")
    private Member member;

    @JsonProperty("variants")
    private PictureVariants variants;

    //Helper methods
    public static String getFormattedBody(String body) {
        try {
            return Html.fromHtml(body).toString();
        } catch (Throwable t) {
            return body;
        }
    }

    public static void startLoveCallWithObserver(final FetLifeApplication fetLifeApplication, final Picture picture, final boolean loved) {
        final String action = loved ? FetLifeApiIntentService.ACTION_APICALL_ADD_LOVE : FetLifeApiIntentService.ACTION_APICALL_REMOVE_LOVE;
        fetLifeApplication.getEventBus().register(new LoveImageCallObserver(fetLifeApplication, action, picture, loved));
        FetLifeApiIntentService.startApiCall(fetLifeApplication, action, picture.getId(), picture.getContentType());
    }


    //Getters/Setters
    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("comment_count")
    public int getCommentCount() {
        return commentCount;
    }

    @JsonProperty("comment_count")
    public void setCommentCount(int commentCount) {
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

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        if (createdAt != null) {
            try {
                setDate(DateUtil.parseDate(createdAt));
            } catch (Exception e) {
            }
        }
    }

    public long getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(long lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public boolean isOnShareList() {
        return onShareList;
    }

    public void setOnShareList(boolean onShareList) {
        this.onShareList = onShareList;
    }

    @JsonIgnore
    public long getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(long date) {
        this.date = date;
    }

    @JsonIgnore
    public String getDisplayUrl() {
        return displayUrl;
    }

    @JsonIgnore
    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("love_count")
    public int getLoveCount() {
        return loveCount;
    }

    @JsonProperty("love_count")
    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }

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
            member.mergeSave();
            setMemberId(member.getId());
        }
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
    public String getThumbUrl() {
        return thumbUrl;
    }

    @JsonIgnore
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("variants")
    public PictureVariants getVariants() {
        return variants;
    }

    @JsonProperty("variants")
    public void setVariants(PictureVariants variants) {
        this.variants = variants;
        if (variants != null) {
            setThumbUrl(variants.getLargeUrl());
            setDisplayUrl(variants.getHugeUrl());
        }
    }

    @JsonProperty("is_loved_by_me")
    public boolean isLovedByMe() {
        return isLovedByMe;
    }

    @JsonProperty("is_loved_by_me")
    public void setLovedByMe(boolean lovedByMe) {
        isLovedByMe = lovedByMe;
    }

    private static class LoveImageCallObserver {

        private final FetLifeApplication fetLifeApplication;
        String action;
        Picture picture;
        boolean loved;

        LoveImageCallObserver(FetLifeApplication fetLifeApplication,String action, Picture picture, boolean loved) {
            this.action = action;
            this.picture = picture;
            this.loved = loved;
            this.fetLifeApplication = fetLifeApplication;
        }

        private boolean checkParams(String... params) {
            if (params == null || params.length != 2) {
                return false;
            }
            return picture.getId().equals(params[0]) && picture.getContentType().equals(params[1]);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onResourceListCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
            if (serviceCallFailedEvent.getServiceCallAction().equals(action) && checkParams(serviceCallFailedEvent.getParams())) {
                picture.setLovedByMe(!loved);
                fetLifeApplication.getEventBus().unregister(this);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onResourceListCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
            if (serviceCallFinishedEvent.getServiceCallAction().equals(action) && checkParams(serviceCallFinishedEvent.getParams())) {
                fetLifeApplication.getEventBus().unregister(this);
            }
        }
    }

    @Override
    public boolean save() {
        Picture currentPicture = Picture.loadPicture(id);
        if (currentPicture != null) {
            if (currentPicture.getLastViewedAt() > lastViewedAt) {
                lastViewedAt = currentPicture.getLastViewedAt();
                onShareList = currentPicture.isOnShareList();
            }
        }
        return super.save();
    }

    public static Picture loadPicture(String pictureId) {
        Picture picture = new Select().from(Picture.class).where(Picture_Table.id.is(pictureId)).querySingle();
        if (picture == null) {
            return null;
        }
        return picture;
    }

    public static void sharePicture(Picture picture) {
        picture.setOnShareList(true);
        picture.save();
        FetLifeApplication.getInstance().showToast(R.string.message_picture_shared);

    }

    public static void unsharePicture(Picture picture) {
        picture.setOnShareList(false);
        picture.save();
    }
}
