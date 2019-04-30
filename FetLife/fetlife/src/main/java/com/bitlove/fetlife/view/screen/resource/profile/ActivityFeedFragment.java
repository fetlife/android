package com.bitlove.fetlife.view.screen.resource.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.EventActivity;
import com.bitlove.fetlife.view.screen.resource.LoadFragment;
import com.bitlove.fetlife.view.screen.resource.WritingActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupMessagesActivity;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityFeedFragment extends LoadFragment implements FeedRecyclerAdapter.OnFeedItemClickListener {

    public static ActivityFeedFragment newInstance(String memberId) {
        ActivityFeedFragment friendsFragment = new ActivityFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        FeedRecyclerAdapter adapter = new FeedRecyclerAdapter(getFetLifeApplication(),this,getArguments().getString(ARG_REFERENCE_ID));
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_FEED;
    }

    @Override
    public void onMemberClick(Member member) {
        //TODO(feed): Remove this mergeSave after Feed is stored in local db
        member.mergeSave();
        ProfileActivity.startActivity((BaseActivity) getActivity(), member.getId());
    }

    @Override
    public void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, FeedItemResourceHelper feedItemResourceHelper) {
        if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
            Member targetMember = feedItemResourceHelper.getTargetMember(feedEvent);
            if (targetMember != null) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity((BaseActivity) getActivity(), targetMember.getId());
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.POST_CREATED || feedStoryType == Story.FeedStoryType.LIKE_CREATED || feedStoryType == Story.FeedStoryType.COMMENT_CREATED) {
            Writing targetWriting = feedItemResourceHelper.getWriting(feedEvent);
            if (targetWriting != null) {
                targetWriting.save();
//                WritingActivity.startActivity((BaseActivity) getActivity(),targetWriting.getId(),targetWriting.getMemberId());
                FetLifeWebViewActivity.Companion.startActivity(getBaseActivity(),targetWriting.getUrl(),false,null, false, null, null);
//                TurboLinksViewActivity.startActivity(getBaseActivity(),targetWriting.getUrl(),targetWriting.getTitle(), false, null, null, false);
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.STATUS_COMMENT_CREATED || feedStoryType == Story.FeedStoryType.STATUS_CREATED) {
            Status targetStatus = feedItemResourceHelper.getStatus(feedEvent);
            if (targetStatus != null) {
                targetStatus.save();
                Member member = targetStatus.getMember();
                String nickname = member != null ? getString(R.string.title_activity_status,member.getNickname()) : "";
                FetLifeWebViewActivity.Companion.startActivity(getBaseActivity(),targetStatus.getUrl(),false,null, false, null, null);
//                TurboLinksViewActivity.startActivity(getBaseActivity(),targetStatus.getUrl(),nickname, false, null, null, false);
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.RSVP_CREATED) {
            Event targetEvent = feedItemResourceHelper.getEvent(feedEvent);
            if (targetEvent != null) {
                targetEvent.save();
                EventActivity.startActivity((BaseActivity) getActivity(),targetEvent.getId());
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.GROUP_MEMBERSHIP_CREATED) {
            Group targetGroup = feedItemResourceHelper.getGroup(feedEvent);
            if (targetGroup != null) {
                targetGroup.save();
                GroupActivity.startActivity((BaseActivity) getActivity(),targetGroup.getId(),targetGroup.getName(),false);
                return;
            }
        } else if (feedStoryType == Story.FeedStoryType.GROUP_COMMENT_CREATED || feedStoryType == Story.FeedStoryType.GROUP_MEMBERSHIP_CREATED || feedStoryType == Story.FeedStoryType.GROUP_POST_CREATED) {
            Group targetGroup = feedItemResourceHelper.getGroup(feedEvent);
            GroupPost targetGroupPost = feedItemResourceHelper.getGroupPost(feedEvent);
            if (targetGroup != null && targetGroupPost != null) {
                targetGroup.save();
                targetGroupPost.save();
                GroupMessagesActivity.startActivity(getActivity(),targetGroup.getId(),targetGroupPost.getId(),targetGroupPost.getTitle(),null,false);
                return;
            } else if (targetGroup != null) {
                targetGroup.save();
                GroupActivity.startActivity((BaseActivity) getActivity(),targetGroup.getId(),targetGroup.getName(),false);
                return;
            }
        }
        UrlUtil.openUrl(getActivity(),url, true, false);
    }

    @Override
    public void onFeedImageClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        if (targetMember != null) {
            if (feedStoryType == Story.FeedStoryType.FOLLOW_CREATED || feedStoryType == Story.FeedStoryType.FRIEND_CREATED) {
                //TODO(feed): Remove this mergeSave after Feed is stored in local db
                targetMember.mergeSave();
                ProfileActivity.startActivity((BaseActivity) getActivity(), targetMember.getId());
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
        UrlUtil.openUrl(getActivity(),url, true, false);
    }

    @Override
    public void onFeedImageLongClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
        UrlUtil.openUrl(getActivity(),url, true, false);
    }

    @Override
    public void onVisitPicture(Picture picture, String url) {
        UrlUtil.openUrl(getActivity(),url, true, false);
    }

    @Override
    public void onSharePicture(Picture picture, String url) {
        if (picture.isOnShareList()) {
            Picture.unsharePicture(picture);
        } else {
            Picture.sharePicture(picture);
        }
    }

    public void refreshUi() {
        if (recyclerView != null) {
            FeedRecyclerAdapter recyclerViewAdapter = (FeedRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

    private void openProfileScreen(Member member) {
        ProfileActivity.startActivity((BaseActivity) getActivity(),member.getId());
    }
}
