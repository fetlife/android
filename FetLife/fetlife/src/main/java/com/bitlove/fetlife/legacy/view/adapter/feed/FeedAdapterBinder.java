

package com.bitlove.fetlife.legacy.view.adapter.feed;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.legacy.view.adapter.PictureGridAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapterBinder {

    private final FeedRecyclerAdapter feedRecyclerAdapter;
    private final FetLifeApplication fetLifeApplication;

    private SparseArray<Boolean> expandHistory = new SparseArray<>();

    public FeedAdapterBinder(FetLifeApplication fetLifeApplication, FeedRecyclerAdapter feedRecyclerAdapter) {
        this.fetLifeApplication = fetLifeApplication;
        this.feedRecyclerAdapter = feedRecyclerAdapter;
    }

    public void bindImageStory(FetLifeApplication fetLifeApplication, final FeedViewHolder feedViewHolder, Story story, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {

        feedViewHolder.feedContainer.setVisibility(View.VISIBLE);

        FeedItemResourceHelper feedItemResourceHelper = new FeedItemResourceHelper(fetLifeApplication, story);

        List<FeedEvent> events = story.getEvents();
        final Member member = feedItemResourceHelper.getMember(events);

        if (member == null) {
            throw new IllegalArgumentException();
        }

        Context context = feedViewHolder.avatarImage.getContext();

        final int position = feedViewHolder.getAdapterPosition();

        String title = feedItemResourceHelper.getHeader(events);
        if (title == null) {
            throw new IllegalArgumentException();
        }
        feedViewHolder.feedText.setText(title);

        feedViewHolder.nameText.setText(member.getNickname());
        feedViewHolder.metaText.setText(member.getMetaInfo());

        String createdAt = feedItemResourceHelper.getCreatedAt(events.get(0));

        feedViewHolder.timeText.setText(createdAt);

        feedViewHolder.avatarImage.setBackgroundResource(R.drawable.dummy_avatar);

        String avatarLink = member.getAvatarLink();
        if (avatarLink == null) {
            feedViewHolder.avatarImage.setImageURI((String)null);
        } else {
            Uri avatarUri = Uri.parse(avatarLink);
            feedViewHolder.avatarImage.setImageURI(avatarUri);
        }
        feedViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onMemberClick(member);
            }
        });

        boolean useListLayout = listLayout(events, feedItemResourceHelper);

        final int expandableResourceId = useListLayout ? R.id.feeditem_list_expandable : R.id.feeditem_grid_expandable;
        feedViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view2Expand = v.findViewById(expandableResourceId);
                View separator = v.findViewById(R.id.feeditem_separator);

                boolean visible = view2Expand.getVisibility() == View.VISIBLE;
                int newVisibility = visible ? View.GONE : View.VISIBLE;

                view2Expand.setVisibility(newVisibility);
                separator.setVisibility(newVisibility);

                expandHistory.put(position,!visible);
            }
        });


        if (useListLayout) {

            LinearLayout linearLayout = feedViewHolder.listExpandArea;

            boolean expandByPreference = feedItemResourceHelper.getExpandPreference();
            boolean expanded = expandHistory.get(position,expandByPreference);
            feedViewHolder.gridExpandArea.setVisibility(View.GONE);
            linearLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.separatorView.setVisibility(expanded ? View.VISIBLE : View.GONE);

            addViews(events, linearLayout, onItemClickListener, feedItemResourceHelper);

        } else {

            PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(feedItemResourceHelper, onItemClickListener);
            pictureGridAdapter.setEvents(events);
            feedViewHolder.gridExpandArea.setAdapter(pictureGridAdapter);

            boolean expandByPreference = feedItemResourceHelper.getExpandPreference();
            boolean expanded = expandHistory.get(position,expandByPreference);

            feedViewHolder.listExpandArea.setVisibility(View.GONE);
            feedViewHolder.gridExpandArea.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.separatorView.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }
    }

    private void addViews(List<FeedEvent> events, LinearLayout linearLayout, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener, FeedItemResourceHelper feedItemResourceHelper) {
        linearLayout.removeAllViews();

        if (feedItemResourceHelper.imageOnlyListItems()) {
            addImageOnlyListItemViews(events, linearLayout, onItemClickListener, feedItemResourceHelper);
        } else {
            addImageTextListItemViews(events, linearLayout, onItemClickListener, feedItemResourceHelper);
        }
    }

    private void addImageTextListItemViews(List<FeedEvent> events, LinearLayout linearLayout, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener, final FeedItemResourceHelper feedItemResourceHelper) {
        final Context context = linearLayout.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (final FeedEvent feedEvent : events) {

            View itemView = inflater.inflate(R.layout.listitem_feed_innerlistitem, linearLayout, false);

            SimpleDraweeView itemImage = (SimpleDraweeView) itemView.findViewById(R.id.feed_innerlist_icon);
            final Picture picture = feedItemResourceHelper.getPicture(feedEvent);
            if (picture != null) {
                itemImage.setVisibility(View.VISIBLE);
                itemImage.getHierarchy().setPlaceholderImage(null);
                itemImage.setImageURI(picture.getVariants().getMediumUrl());
                itemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (feedItemResourceHelper.browseImageOnClick()) {
                            List<Picture> pictures = new ArrayList<>();
                            pictures.add(picture);
                            fetLifeApplication.getImageViewerWrapper().show(context,pictures,0);
                        } else {
                            onItemClickListener.onFeedInnerItemClick(feedItemResourceHelper.getFeedStoryType(), feedItemResourceHelper.getUrl(feedEvent), feedEvent, feedItemResourceHelper);
                        }
                    }
                });
            } else if (feedItemResourceHelper.useImagePlaceHolder(feedEvent)) {
                itemImage.setVisibility(View.VISIBLE);
                itemImage.getHierarchy().setPlaceholderImage(R.drawable.dummy_avatar);
            } else {
                itemImage.setVisibility(View.GONE);
            }

            CharSequence itemHeaderText = feedItemResourceHelper.getItemTitle(feedEvent);
            CharSequence itemTextText = feedItemResourceHelper.getItemBody(feedEvent);
            CharSequence itemTimeText = feedItemResourceHelper.getItemCaption(feedEvent);

            TextView itemHeader = (TextView) itemView.findViewById(R.id.feed_innerlist_header);
            itemHeader.setText(itemHeaderText);
            TextView itemSingleText = (TextView) itemView.findViewById(R.id.feed_innerlist_single_text);
            itemSingleText.setText(itemTextText);
            TextView itemText = (TextView) itemView.findViewById(R.id.feed_innerlist_upper);
            itemText.setText(itemTextText);
            TextView timeText = (TextView) itemView.findViewById(R.id.feed_innerlist_caption);
            timeText.setText(itemTimeText);

            if (itemHeaderText == null && itemTimeText == null) {
                itemHeader.setVisibility(View.GONE);
                itemText.setVisibility(View.GONE);
                timeText.setVisibility(View.GONE);
                itemSingleText.setVisibility(View.VISIBLE);
            } else {
                itemHeader.setVisibility(View.VISIBLE);
                itemText.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
                itemSingleText.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onFeedInnerItemClick(feedItemResourceHelper.getFeedStoryType(), feedItemResourceHelper.getUrl(feedEvent), feedEvent, feedItemResourceHelper);
                }
            });

            linearLayout.addView(itemView);

        }
    }

    private void addImageOnlyListItemViews(final List<FeedEvent> events, LinearLayout linearLayout, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener, final FeedItemResourceHelper feedItemResourceHelper) {
        final Context context = linearLayout.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (final FeedEvent event : events) {

            final Picture picture = feedItemResourceHelper.getPicture(event);

            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) inflater.inflate(R.layout.listitem_feed_imageitem, linearLayout, false);
            simpleDraweeView.setImageURI(picture.getVariants().getLargeUrl());
            if (feedItemResourceHelper.browseImageOnClick()) {
                simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Picture> pictures = new ArrayList<>();
                        pictures.add(picture);
                        fetLifeApplication.getImageViewerWrapper().show(context,pictures,0);
                    }
                });
            } else {
                simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onFeedImageClick(feedItemResourceHelper.getFeedStoryType(),feedItemResourceHelper.getUrl(event), event, feedItemResourceHelper.getTargetMember(event));
                    }
                });
                simpleDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemClickListener.onFeedImageLongClick(feedItemResourceHelper.getFeedStoryType(),feedItemResourceHelper.getUrl(event), event, feedItemResourceHelper.getTargetMember(event));
                        return true;
                    }
                });
            }
            linearLayout.removeAllViews();
            linearLayout.addView(simpleDraweeView);
        }
    }

    private boolean listLayout(List<FeedEvent> events, FeedItemResourceHelper feedItemResourceHelper) {
        if (feedItemResourceHelper.listOnly()) {
            return true;
        }
        return events.size() == 1;
    }

}
