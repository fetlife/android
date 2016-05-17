package com.bitlove.fetlife.view;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.Conversation$Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationsRecyclerAdapter extends RecyclerView.Adapter<ConversationViewHolder> {

    public interface OnConversationClickListener {
        public void onClick(Conversation conversation);
    }

    private List<Conversation> itemList;
    OnConversationClickListener onConversationClickListener;

    public ConversationsRecyclerAdapter() {
        loadItems();
    }

    public void setOnItemClickListener(OnConversationClickListener onConversationClickListener) {
        this.onConversationClickListener = onConversationClickListener;
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        itemList = new Select().from(Conversation.class).orderBy(false,Conversation$Table.DATE).queryList();
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

        if (conversation.getHasNewMessage()) {
            conversationViewHolder.newMessageIndicator.setVisibility(View.VISIBLE);
        } else {
            conversationViewHolder.newMessageIndicator.setVisibility(View.GONE);
        }
        conversationViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(conversation.getDate())));

        conversationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConversationClickListener != null) {
                    onConversationClickListener.onClick(conversation);
                }
            }
        });
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

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Conversation getItem(int position) {
        return itemList.get(position);
    }
}

class ConversationViewHolder extends RecyclerView.ViewHolder {

    TextView headerText, messageText, dateText, lowerText, newMessageIndicator;

    public ConversationViewHolder(View itemView) {
        super(itemView);

        newMessageIndicator = (TextView) itemView.findViewById(R.id.conversation_new_message_indicator);
        headerText = (TextView) itemView.findViewById(R.id.conversation_header);
        messageText = (TextView) itemView.findViewById(R.id.conversation_message_text);
        dateText = (TextView) itemView.findViewById(R.id.conversation_date);
        lowerText = (TextView) itemView.findViewById(R.id.conversation_lower);
    }
}
