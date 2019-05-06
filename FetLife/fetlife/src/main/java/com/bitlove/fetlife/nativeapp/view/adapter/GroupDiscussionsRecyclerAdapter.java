package com.bitlove.fetlife.nativeapp.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost_Table;
import com.bitlove.fetlife.nativeapp.util.ServerIdUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GroupDiscussionsRecyclerAdapter extends ResourceListRecyclerAdapter<GroupPost, GroupDiscussionViewHolder> {

    private static final int MAX_BODY_LENGTH = 125;

    private String groupId;
    private List<GroupPost> itemList = new ArrayList<>();

    public GroupDiscussionsRecyclerAdapter(String groupId, FetLifeApplication fetLifeApplication) {
        this.groupId = groupId;
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

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        if (ServerIdUtil.isServerId(groupId)) {
            if (ServerIdUtil.containsServerId(groupId)) {
                groupId = ServerIdUtil.getLocalId(groupId);
            } else {
                return;
            }
        }
        itemList = new Select().from(GroupPost.class).where(GroupPost_Table.groupId.is(groupId)).orderBy(GroupPost_Table.date,false).queryList();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public GroupPost getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public GroupDiscussionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_group_discussion, parent, false);
        return new GroupDiscussionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupDiscussionViewHolder groupDiscussionViewHolder, int position) {

        final GroupPost groupPost = itemList.get(position);

        groupDiscussionViewHolder.headerText.setText(groupPost.getTitle());
        String groupDiscussionBody = groupPost.getHtmlBody().toString();
//        groupDiscussionBody = StringUtil.parseMarkedHtml(groupDiscussionBody).toString();
        String descPreview = groupDiscussionBody.substring(0,Math.min(MAX_BODY_LENGTH,groupDiscussionBody.length())).trim();

        groupDiscussionViewHolder.messageText.setText(descPreview);
        groupDiscussionViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(groupPost.getDate()));

        groupDiscussionViewHolder.newMessageIndicator.setVisibility(View.GONE);

        groupDiscussionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onItemClick(groupPost);
                }
            }
        });

        groupDiscussionViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onAvatarClick(groupPost);
                }
            }
        });

        String avatarUrl = groupPost.getAvatarLink();
        groupDiscussionViewHolder.avatarImage.setImageURI(avatarUrl);
//        imageLoader.loadImage(groupDiscussionViewHolder.itemView.getContext(), avatarUrl, groupDiscussionViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

    @Override
    protected void onItemRemove(GroupDiscussionViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class GroupDiscussionViewHolder extends SwipeableViewHolder {

    SimpleDraweeView avatarImage;
    TextView headerText, messageText, dateText, newMessageIndicator;

    public GroupDiscussionViewHolder(View itemView) {
        super(itemView);

        newMessageIndicator = (TextView) itemView.findViewById(R.id.conversation_new_message_indicator);
        headerText = (TextView) itemView.findViewById(R.id.conversation_header);
        messageText = (TextView) itemView.findViewById(R.id.conversation_message_text);
        dateText = (TextView) itemView.findViewById(R.id.conversation_date);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.conversation_icon);
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

