package com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.legacy.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.legacy.model.db.FetLifeDatabase;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.util.DateUtil;
import com.bitlove.fetlife.legacy.util.ServerIdUtil;
import com.bitlove.fetlife.legacy.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Table(database = FetLifeDatabase.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Writing extends BaseModel{

    private static class LoveWritingCallObserver {

        private final FetLifeApplication fetLifeApplication;
        String action;
        Writing writing;
        boolean loved;

        LoveWritingCallObserver(FetLifeApplication fetLifeApplication,String action, Writing writing, boolean loved) {
            this.action = action;
            this.writing = writing;
            this.loved = loved;
            this.fetLifeApplication = fetLifeApplication;
        }

        private boolean checkParams(String... params) {
            if (params == null || params.length != 2) {
                return false;
            }
            return writing.getId().equals(params[0]) && writing.getContentType().equals(params[1]);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onResourceListCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
            if (serviceCallFailedEvent.getServiceCallAction().equals(action) && checkParams(serviceCallFailedEvent.getParams())) {
                writing.setLovedByMe(!loved);
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

    public static void startLoveCallWithObserver(final FetLifeApplication fetLifeApplication, final Writing writing, final boolean loved) {
        final String action = loved ? FetLifeApiIntentService.ACTION_APICALL_ADD_LOVE : FetLifeApiIntentService.ACTION_APICALL_REMOVE_LOVE;
        fetLifeApplication.getEventBus().register(new Writing.LoveWritingCallObserver(fetLifeApplication, action, writing, loved));
        FetLifeApiIntentService.startApiCall(fetLifeApplication, action, writing.getId(), writing.getContentType());
    }

    public static Writing loadWriting(String writingId) {
        try {
            if (ServerIdUtil.containsServerId(writingId)) {
                writingId = ServerIdUtil.getLocalId(writingId);
            }
            Writing writing = new Select().from(Writing.class).where(Writing_Table.id.is(writingId)).querySingle();
            return writing;
        } catch (Throwable t) {
            //db not available
            return null;
        }
    }

    @Column
    @JsonProperty("body")
    private String body;

    @Column
    @JsonProperty("comment_count")
    private Integer commentCount;

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
    private boolean lovedByMe;

    @Column
    @JsonProperty("love_count")
    private Integer loveCount;

    @JsonProperty("member")
    private Member member;

    @Column
    @JsonProperty("title")
    private String title;

    @Column
    @JsonProperty("url")
    private String url;

    @Column
    @JsonIgnore
    private boolean detailLoaded;

    @Column
    @JsonIgnore
    private String memberId;

    @Column
    @JsonIgnore
    private String nickname;

    @Column
    @JsonIgnore
    private String avatarUrl;



    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
        this.htmlBody = null;
    }

    @JsonIgnore
    private CharSequence htmlBody;

    public CharSequence getHtmlBody() {
        if (htmlBody == null && body != null) {
            this.htmlBody = StringUtil.parseMarkedHtml(body);
        }
        return htmlBody;
    }

    @JsonProperty("comment_count")
    public Integer getCommentCount() {
        return commentCount;
    }

    @JsonProperty("comment_count")
    public void setCommentCount(Integer commentCount) {
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

    @Column
    @JsonIgnore
    private long date;

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

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("love_count")
    public Integer getLoveCount() {
        return loveCount;
    }

    @JsonProperty("love_count")
    public void setLoveCount(Integer loveCount) {
        this.loveCount = loveCount;
    }

    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
        if (member != null) {
            setMemberId(member.getId());
            setNickname(member.getNickname());
            setAvatarUrl(member.getAvatarLink());
        }
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isLovedByMe() {
        return lovedByMe;
    }

    public void setLovedByMe(boolean lovedByMe) {
        this.lovedByMe = lovedByMe;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isDetailLoaded() {
        return detailLoaded;
    }

    public void setDetailLoaded(boolean detailLoaded) {
        this.detailLoaded = detailLoaded;
    }

    @Override
    public boolean save() {
        return mergeSave();
    }

    public boolean mergeSave() {
        Writing savedWriting = Writing.loadWriting(id);
        if (savedWriting != null) {
            setDetailLoaded(savedWriting.isDetailLoaded());
        }
        return super.save();
    }

    public boolean detailSave() {
        setDetailLoaded(true);
        return super.save();
    }
}

