package com.bitlove.fetlife.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Message;
import com.bitlove.fetlife.model.pojos.Message$Table;
import com.bitlove.fetlife.util.ColorUtil;
import com.bitlove.fetlife.util.StringUtil;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final float PENDING_ALPHA = 0.5f;

    private final String conversationId;
    private List<Message> itemList;

    public MessagesRecyclerAdapter(String conversationId) {
        this.conversationId = conversationId;
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
        itemList = new Select().from(Message.class).where(Condition.column(Message$Table.CONVERSATIONID).eq(conversationId)).orderBy(false,Message$Table.DATE).queryList();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int position) {
        Message message = itemList.get(position);
        String messageBody = message.getBody().trim();

        messageViewHolder.messageText.setText(StringUtil.parseHtml(messageBody));
        messageViewHolder.subText.setText(message.getSenderNickname() + messageViewHolder.subMessageSeparator + SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));

        boolean myMessage = message.getSenderId().equals(messageViewHolder.selfMessageId);

        if (myMessage) {
            messageViewHolder.subText.setGravity(Gravity.RIGHT);
            messageViewHolder.messageContainer.setGravity(Gravity.RIGHT);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.extendedHPadding, messageViewHolder.vPadding, messageViewHolder.hPadding, messageViewHolder.vPadding);
        } else {
            messageViewHolder.subText.setGravity(Gravity.LEFT);
            messageViewHolder.messageContainer.setGravity(Gravity.LEFT);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.hPadding, messageViewHolder.vPadding, messageViewHolder.extendedHPadding, messageViewHolder.vPadding);
        }

        if (message.getPending()) {
            messageViewHolder.messageText.setTextColor(messageViewHolder.primaryTextColor);
            messageViewHolder.messageText.setAlpha(PENDING_ALPHA);
        } else if (message.getFailed()) {
            messageViewHolder.messageText.setAlpha(1f);
            messageViewHolder.messageText.setTextColor(messageViewHolder.errorTextColor);
        } else {
            messageViewHolder.messageText.setTextColor(messageViewHolder.primaryTextColor);
            messageViewHolder.messageText.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Message getItem(int position) {
        return itemList.get(position);
    }
}

class MessageViewHolder extends RecyclerView.ViewHolder {

    private static final int EXTEND_PADDING_MULTIPLYER = 10;

    LinearLayout messageContainer;
    TextView messageText, subText;
    String subMessageSeparator;
    int extendedHPadding, hPadding, vPadding;
    public String selfMessageId;
    public int primaryTextColor, errorTextColor;

    public MessageViewHolder(View itemView) {
        super(itemView);

        Context context = itemView.getContext();
        FetLifeApplication fetLifeApplication = (FetLifeApplication) context.getApplicationContext();
        selfMessageId = fetLifeApplication.getMe().getId();

        subMessageSeparator = context.getResources().getString(R.string.message_sub_separator);

        hPadding = (int) context.getResources().getDimension(R.dimen.listitem_horizontal_margin);
        vPadding = (int) context.getResources().getDimension(R.dimen.listitem_vertical_margin);
        extendedHPadding = EXTEND_PADDING_MULTIPLYER * hPadding;

        primaryTextColor = ColorUtil.retrieverColor(context, R.color.text_color_primary);
        errorTextColor = ColorUtil.retrieverColor(context, R.color.text_color_error);

        messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
        messageText.setMovementMethod(LinkMovementMethod.getInstance());
        subText = (TextView) itemView.findViewById(R.id.message_sub);
    }
}
