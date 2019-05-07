
package com.bitlove.fetlife.legacy.view.adapter.feed;

import android.content.Context;
import android.view.View;

import com.bitlove.fetlife.common.app.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;

public class FeedNotSupportedAdapterBinder {

    public FeedNotSupportedAdapterBinder(FeedRecyclerAdapter feedRecyclerAdapter) {
    }

    public void bindNotSupportedStory(FetLifeApplication fetLifeApplication, final FeedViewHolder feedViewHolder, final Story story, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
        Context context = feedViewHolder.avatarImage.getContext();

        String title  = "Not yet supported story type: " + story.getName();

        if (title != null) {
            feedViewHolder.feedText.setText(title);
        } else {
            feedViewHolder.feedText.setText(R.string.feed_title_like_unknown);
        }
        feedViewHolder.metaText.setText(null);
        feedViewHolder.nameText.setText(null);
        feedViewHolder.timeText.setText(null);
        feedViewHolder.gridExpandArea.setAdapter(null);
        feedViewHolder.listExpandArea.removeAllViews();
        feedViewHolder.gridExpandArea.setVisibility(View.GONE);
        feedViewHolder.listExpandArea.setVisibility(View.GONE);
        feedViewHolder.separatorView.setVisibility(View.GONE);
        feedViewHolder.avatarImage.setImageURI((String)null);

        feedViewHolder.feedContainer.setVisibility(View.GONE);
   }

}