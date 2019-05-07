package com.bitlove.fetlife.legacy.view.screen.resource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.util.UrlUtil;
import com.bitlove.fetlife.legacy.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.legacy.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.screen.BaseActivity;
import com.bitlove.fetlife.legacy.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.legacy.view.screen.resource.groups.GroupActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.groups.GroupMessagesActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;

public class FeedActivity extends ResourceListActivity<Story> implements FeedRecyclerAdapter.OnFeedItemClickListener {

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static void startActivity(Context context, View transitionView, String transitionName) {
        if (transitionView != null && context instanceof Activity) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity)context, transitionView, transitionName);
            context.startActivity(createIntent(context),options.toBundle());
        } else {
            context.startActivity(createIntent(context));
        }
    }

    private static Intent createIntent(Context context) {
        Intent intent = new Intent(context, FeedActivity.class);
        intent.putExtra(BaseActivity.EXTRA_HAS_BOTTOM_NAVIGATION,true);
        intent.putExtra(BaseActivity.EXTRA_SELECTED_BOTTOM_NAV_ITEM,R.id.navigation_bottom_feed);
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onResourceCreate(savedInstanceState);
    }

    @Override
    protected ResourceListRecyclerAdapter<Story, ?> createRecyclerAdapter(Bundle savedInstanceState) {
        return new FeedRecyclerAdapter(getFetLifeApplication(), this, null);
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_FEED;
    }

    @Override
    public void onItemClick(Story feedStory) {
    }

    @Override
    public void onAvatarClick(Story feedStory) {
    }

    @Override
    public void onMemberClick(Member member) {
        //TODO(feed): Remove this mergeSave after Feed is stored in local db
        member.mergeSave();
        ProfileActivity.startActivity(this, member.getId());
    }

    @Override
    public void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, FeedItemResourceHelper feedItemResourceHelper) {
        if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
            Member targetMember = feedItemResourceHelper.getTargetMember(feedEvent);
            if (targetMember != null) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity(this, targetMember.getId());
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.POST_CREATED || feedStoryType == Story.FeedStoryType.LIKE_CREATED || feedStoryType == Story.FeedStoryType.COMMENT_CREATED || feedStoryType == Story.FeedStoryType.POST_COMMENT_CREATED) {
            Writing targetWriting = feedItemResourceHelper.getWriting(feedEvent);
            if (targetWriting != null) {
                targetWriting.save();
//                WritingActivity.startActivity(this,targetWriting.getId(), targetWriting.getMemberId());
                FetLifeWebViewActivity.Companion.startActivity(this,targetWriting.getUrl(),false,null, false, null, null);
//                TurboLinksViewActivity.startActivity(this,targetWriting.getUrl(),targetWriting.getTitle(), false, null, null, false);
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.STATUS_COMMENT_CREATED || feedStoryType == Story.FeedStoryType.STATUS_CREATED) {
            Status targetStatus = feedItemResourceHelper.getStatus(feedEvent);
            if (targetStatus != null) {
                targetStatus.save();
                Member member = targetStatus.getMember();
                String nickname = member != null ? getString(R.string.title_activity_status,member.getNickname()) : "";
                FetLifeWebViewActivity.Companion.startActivity(this,targetStatus.getUrl(),false,null, false, null, null);
//                TurboLinksViewActivity.startActivity(this,targetStatus.getUrl(),nickname, false, null, null, false);
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.RSVP_CREATED) {
            Event targetEvent = feedItemResourceHelper.getEvent(feedEvent);
            if (targetEvent != null) {
                targetEvent.save();
                EventActivity.startActivity(this,targetEvent.getId());
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.GROUP_MEMBERSHIP_CREATED) {
            Group targetGroup = feedItemResourceHelper.getGroup(feedEvent);
            if (targetGroup != null) {
                targetGroup.save();
                GroupActivity.startActivity(this,targetGroup.getId(),targetGroup.getName(),false);
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.GROUP_COMMENT_CREATED || feedStoryType == Story.FeedStoryType.GROUP_MEMBERSHIP_CREATED || feedStoryType == Story.FeedStoryType.GROUP_POST_CREATED) {
            Group targetGroup = feedItemResourceHelper.getGroup(feedEvent);
            GroupPost targetGroupPost = feedItemResourceHelper.getGroupPost(feedEvent);
            if (targetGroup != null && targetGroupPost != null) {
                targetGroup.save();
                targetGroupPost.save();
                GroupMessagesActivity.startActivity(this,targetGroup.getId(),targetGroupPost.getId(),targetGroupPost.getTitle(),null,false);
                return;
            } else if (targetGroup != null) {
                targetGroup.save();
                GroupActivity.startActivity(this,targetGroup.getId(),targetGroup.getName(),false);
                return;
            }
        }
        UrlUtil.openUrl(this,url, true, false);
    }

    @Override
    public void onFeedImageClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        if (targetMember != null) {
            if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity(this, targetMember.getId());
                return;
            }
        }
        if ((feedStoryType == Story.FeedStoryType.LIKE_CREATED || feedStoryType == Story.FeedStoryType.VIDEO_COMMENT_CREATED) && feedEvent.getSecondaryTarget().getVideo() != null) {
            String videoUrl = feedEvent.getSecondaryTarget().getVideo().getVideoUrl();
            if (videoUrl == null || videoUrl.endsWith("null")) {
                return;
            }
            Uri uri = Uri.parse(videoUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
            return;
        }
        if (feedStoryType == Story.FeedStoryType.VIDEO_CREATED) {
            String videoUrl = feedEvent.getTarget().getVideo().getVideoUrl();
            if (videoUrl == null || videoUrl.endsWith("null")) {
                return;
            }
            Uri uri = Uri.parse(videoUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
            return;
        }
        UrlUtil.openUrl(this,url, true, false);
    }

    @Override
    public void onFeedImageLongClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        UrlUtil.openUrl(this,url, true, false);
    }

    @Override
    public void onVisitPicture(Picture picture, String url) {
        UrlUtil.openUrl(this,url, true, false);
    }

    @Override
    public void onSharePicture(Picture picture, String url) {
        if (picture.isOnShareList()) {
            Picture.unsharePicture(picture);
        } else {
            Picture.sharePicture(picture);
        }
    }
}
