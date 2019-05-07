package com.bitlove.fetlife.legacy.view.adapter.feed;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.legacy.util.PictureUtil;
import com.bitlove.fetlife.legacy.util.ServerIdUtil;
import com.bitlove.fetlife.legacy.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.adapter.SwipeableViewHolder;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FeedRecyclerAdapter extends ResourceListRecyclerAdapter<Story, FeedViewHolder> {

    protected String memberId;

    public interface OnFeedItemClickListener extends PictureUtil.OnPictureOverlayClickListener {
        void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, FeedItemResourceHelper feedItemResourceHelper);
        void onFeedImageClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember);
        void onFeedImageLongClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember);
    }

    protected final FetLifeApplication fetLifeApplication;
    protected final OnFeedItemClickListener onFeedItemClickListener;

    protected List<Story> itemList;

    FeedAdapterBinder feedImageAdapterBinder;
    FeedNotSupportedAdapterBinder feedNotSupportedAdapterBinder;

    public FeedRecyclerAdapter(FetLifeApplication fetLifeApplication, OnFeedItemClickListener onFeedItemClickListener, String memberId) {
        this.fetLifeApplication = fetLifeApplication;
        feedImageAdapterBinder = new FeedAdapterBinder(fetLifeApplication, this);
        feedNotSupportedAdapterBinder = new FeedNotSupportedAdapterBinder(this);
        this.onFeedItemClickListener = onFeedItemClickListener;
        this.memberId = memberId;
        loadItems();
    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //TODO: think of possibility of update only specific items instead of the whole list
                loadItems();
                notifyDataSetChanged();
            }
        });
    }

    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        if (memberId != null) {
            if (ServerIdUtil.isServerId(memberId)) {
                if (ServerIdUtil.containsServerId(memberId)) {
                    memberId = ServerIdUtil.getLocalId(memberId);
                } else {
                    return;
                }
            }
            itemList = fetLifeApplication.getInMemoryStorage().getProfileFeed();
        } else {
            itemList = fetLifeApplication.getInMemoryStorage().getFeed();
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public Story getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_feed, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder feedViewHolder, int position) {

        Story story = getItem(position);
        Story.FeedStoryType storytype = story.getType();
        if (storytype == null) {
            feedNotSupportedAdapterBinder.bindNotSupportedStory(fetLifeApplication, feedViewHolder,story, onFeedItemClickListener);
            return;
        }

        try {
            switch (storytype) {
                case FOLLOW_CREATED:
                case RSVP_CREATED:
                case COMMENT_CREATED:
                case POST_CREATED:
                case VIDEO_CREATED:
                case PICTURE_CREATED:
                case GROUP_COMMENT_CREATED:
                case POST_COMMENT_CREATED:
                case GROUP_MEMBERSHIP_CREATED:
                case LIKE_CREATED:
                case FRIEND_CREATED:
                case VIDEO_COMMENT_CREATED:
                case PEOPLE_INTO_CREATED:
                case STATUS_CREATED:
                    feedImageAdapterBinder.bindImageStory(fetLifeApplication, feedViewHolder, story, onFeedItemClickListener);
                    break;
                case STATUS_COMMENT_CREATED:
                case WALL_POST_CREATED:
                case GROUP_POST_CREATED:
                    feedImageAdapterBinder.bindImageStory(fetLifeApplication, feedViewHolder, story, onFeedItemClickListener);
                    break;
                default:
                    feedNotSupportedAdapterBinder.bindNotSupportedStory(fetLifeApplication, feedViewHolder,story, onFeedItemClickListener);
                    break;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            feedNotSupportedAdapterBinder.bindNotSupportedStory(fetLifeApplication, feedViewHolder,story, onFeedItemClickListener);
        }
    }

    @Override
    protected void onItemRemove(FeedViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class FeedViewHolder extends SwipeableViewHolder {

    GridView gridExpandArea;
    LinearLayout listExpandArea;
    SimpleDraweeView avatarImage;
    TextView nameText, metaText, feedText, timeText;
    View separatorView, feedContainer;

    public FeedViewHolder(View itemView) {
        super(itemView);

        feedContainer = itemView.findViewById(R.id.feeditem_container);
        feedText = (TextView) itemView.findViewById(R.id.feeditem_text);
        nameText = (TextView) itemView.findViewById(R.id.feeditem_name);
        metaText = (TextView) itemView.findViewById(R.id.feeditem_meta);
        timeText = (TextView) itemView.findViewById(R.id.feeditem_time);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.feeditem_icon);
        separatorView = itemView.findViewById(R.id.feeditem_separator);

        gridExpandArea = (GridView) itemView.findViewById(R.id.feeditem_grid_expandable);
        listExpandArea = (LinearLayout) itemView.findViewById(R.id.feeditem_list_expandable);
    }

    @Override
    public View getSwipeableLayout() {
        return null;
    }

    @Override
    public View getSwipeRightBackground() {
        return null;
    }

    @Override
    public View getSwipeLeftBackground() {
        return null;
    }
}

