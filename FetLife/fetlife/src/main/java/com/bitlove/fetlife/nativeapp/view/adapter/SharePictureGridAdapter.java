package com.bitlove.fetlife.nativeapp.view.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.nativeapp.util.UrlUtil;
import com.bitlove.fetlife.nativeapp.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.nativeapp.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.resource.profile.ProfileActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharePictureGridAdapter extends BaseAdapter {

    private static final float ALPHA_UNSELECTED = 0.4f;

    private List<Picture> pictures = new ArrayList<>();
    private ArrayList<String> gridLinks = new ArrayList<>();
    private ArrayList<String> displayLinks = new ArrayList<>();

    private Set<Integer> selections = new HashSet<>();

    public List<String> getSelectedUrls() {
        List<String> urls = new ArrayList<>();
        for (Integer position : selections) {
            urls.add(pictures.get(position).getUrl());
        }
        return urls;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures.clear();
        gridLinks.clear();
        displayLinks.clear();
        for (Picture picture : pictures) {
            this.pictures.add(picture);
            gridLinks.add(picture != null ? picture.getThumbUrl() : null);
            displayLinks.add(picture != null ? picture.getDisplayUrl() : null);
        }
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Picture getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Uri pictureUri = gridLinks.get(position) != null ? Uri.parse(gridLinks.get(position)) : null;

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) inflater.inflate(R.layout.listitem_feed_griditem, parent, false);
        simpleDraweeView.setImageURI(pictureUri);
        if (pictureUri == null) {
            simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.dummy_avatar);
        } else {
            simpleDraweeView.getHierarchy().setPlaceholderImage(null);
        }

        simpleDraweeView.setAlpha(selections.contains(position) ? 1.0f : ALPHA_UNSELECTED);

        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selections.contains(position)) {
                    selections.remove(position);
                    v.setAlpha(ALPHA_UNSELECTED);
                } else {
                    selections.add(position);
                    v.setAlpha(1.0f);
                }
            }
        });

        simpleDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                final View overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null);
                final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener = new FeedRecyclerAdapter.OnFeedItemClickListener() {
                    @Override
                    public void onMemberClick(Member member) {
                        member.mergeSave();
                        ProfileActivity.startActivity(v.getContext(),member.getId());
                    }

                    @Override
                    public void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, FeedItemResourceHelper feedItemResourceHelper) {
                    }

                    @Override
                    public void onFeedImageClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
                    }

                    @Override
                    public void onFeedImageLongClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {
                    }

                    @Override
                    public void onVisitPicture(Picture picture, String url) {
                        UrlUtil.openUrl(v.getContext(),url, true, false);
                    }

                    @Override
                    public void onSharePicture(Picture picture, String url) {
                        if (picture.isOnShareList()) {
                            Picture.unsharePicture(picture);
                        } else {
                            Picture.sharePicture(picture);
                        }
                    }

                };
                FetLifeApplication.getInstance().getImageViewerWrapper().show(v.getContext(), pictures, position);
                return true;
            }
        });

        return simpleDraweeView;
    }
}

