package com.bitlove.fetlife.legacy.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Conversation_Table;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationsRecyclerAdapter extends ResourceListRecyclerAdapter<Conversation, ConversationViewHolder> {

    private List<Conversation> itemList;

    public ConversationsRecyclerAdapter() {
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
        itemList = new Select().from(Conversation.class).orderBy(Conversation_Table.date,false).queryList();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Conversation getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_conversation, parent, false);
        return new ConversationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder conversationViewHolder, int position) {

        final Conversation conversation = itemList.get(position);

        conversationViewHolder.headerText.setText(conversation.getNickname());
        conversationViewHolder.messageText.setText(conversation.getSubject());

        if (conversation.getContainNewMessage()) {
            conversationViewHolder.newMessageIndicator.setVisibility(View.VISIBLE);
        } else {
            conversationViewHolder.newMessageIndicator.setVisibility(View.GONE);
        }
        conversationViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(conversation.getDate())));

        conversationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onItemClick(conversation);
                }
            }
        });

        conversationViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onAvatarClick(conversation);
                }
            }
        });

        conversationViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = conversation.getAvatarLink();
        conversationViewHolder.avatarImage.setImageURI(avatarUrl);
//        imageLoader.loadImage(conversationViewHolder.itemView.getContext(), avatarUrl, conversationViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

    @Override
    protected void onItemRemove(ConversationViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class ConversationViewHolder extends SwipeableViewHolder {

    SimpleDraweeView avatarImage;
    TextView headerText, messageText, dateText, newMessageIndicator;

    public ConversationViewHolder(View itemView) {
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
