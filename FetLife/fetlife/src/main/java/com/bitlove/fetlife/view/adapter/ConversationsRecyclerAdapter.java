package com.bitlove.fetlife.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Conversation;

import com.bitlove.fetlife.model.pojos.Conversation_Table;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationsRecyclerAdapter extends ResourceListRecyclerAdapter<Conversation, ConversationViewHolder> {

    private final ImageLoader imageLoader;

    private List<Conversation> itemList;

    public ConversationsRecyclerAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        loadItems();
    }

    public void refresh() {
        loadItems();
        //TODO: think of possibility of update only specific items instead of the whole list
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
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
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(conversation);
                }
            }
        });

        conversationViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onAvatarClick(conversation);
                }
            }
        });

        conversationViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = conversation.getAvatarLink();
        imageLoader.loadImage(conversationViewHolder.itemView.getContext(), avatarUrl, conversationViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

    @Override
    protected void onItemRemove(ConversationViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class ConversationViewHolder extends SwipeableViewHolder {

    ImageView avatarImage;
    TextView headerText, messageText, dateText, newMessageIndicator;

    public ConversationViewHolder(View itemView) {
        super(itemView);

        newMessageIndicator = (TextView) itemView.findViewById(R.id.conversation_new_message_indicator);
        headerText = (TextView) itemView.findViewById(R.id.conversation_header);
        messageText = (TextView) itemView.findViewById(R.id.conversation_message_text);
        dateText = (TextView) itemView.findViewById(R.id.conversation_date);
        avatarImage = (ImageView) itemView.findViewById(R.id.conversation_icon);
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
