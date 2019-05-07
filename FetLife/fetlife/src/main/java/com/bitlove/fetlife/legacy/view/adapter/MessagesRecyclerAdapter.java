package com.bitlove.fetlife.legacy.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.common.app.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Conversation_Table;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Message_Table;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.MessageEntities;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.legacy.util.ColorUtil;
import com.bitlove.fetlife.legacy.util.ServerIdUtil;
import com.bitlove.fetlife.legacy.util.StringUtil;
import com.bitlove.fetlife.legacy.util.UrlUtil;
import com.bitlove.fetlife.legacy.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.legacy.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.legacy.view.widget.AutoAlignGridView;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final float PENDING_ALPHA = 0.5f;

    private final String conversationId;
    private List<Message> itemList;
    private Conversation conversation;

    public MessagesRecyclerAdapter(String conversationId) {
        this.conversationId = conversationId;
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
        String localId = ServerIdUtil.isServerId(conversationId) ? ServerIdUtil.getLocalId(conversationId) : conversationId;
        //TODO: think of moving to separate thread with specific DB executor
        conversation = new Select().from(Conversation.class).where(Conversation_Table.id.is(localId)).querySingle();
        itemList = new Select().from(Message.class).where(Message_Table.conversationId.is(localId)).orderBy(Message_Table.pending,false).orderBy(Message_Table.date,false).orderBy(Message_Table.id,false).queryList();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Message getItem(int position) {
        return itemList.get(position);
    }

    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    private SpannableStringBuilder spanBuilder = null;

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int position) {
        Message message = itemList.get(position);

        CharSequence messageBody = message.getHtmlBody();

        MessageEntities messageEntities = StringUtil.getMessageEntities(message.getEntitiesJson());
        List<Picture> pictures = messageEntities.getPictures();
        if (pictures.isEmpty()) {
            messageViewHolder.messageEntitiesGrid.setVisibility(View.GONE);
        } else {
            messageViewHolder.messageEntitiesGrid.setVisibility(View.VISIBLE);
            int columnCount = Math.min(4,(pictures.size()+2)/3);
            messageViewHolder.messageEntitiesGrid.setNumColumns(columnCount);
            PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(new FeedRecyclerAdapter.OnFeedItemClickListener() {
                @Override
                public void onMemberClick(Member member) {
                    member.mergeSave();
                    ProfileActivity.startActivity(FetLifeApplication.getInstance(), member.getId());
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
                    UrlUtil.openUrl(FetLifeApplication.getInstance(),url, true, false);
                }

                @Override
                public void onSharePicture(Picture picture, String url) {
                    if (picture.isOnShareList()) {
                        Picture.unsharePicture(picture);
                    } else {
                        Picture.sharePicture(picture);
                    }
                }

            });
            pictureGridAdapter.setPictures(pictures);
            messageViewHolder.messageEntitiesGrid.setAdapter(pictureGridAdapter);
        }

//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        textView.setHighlightColor(Color.TRANSPARENT);

        messageViewHolder.messageText.setText(messageBody);
//        messageViewHolder.subText.setText(message.getSenderNickname() + messageViewHolder.subMessageSeparator + SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));
        messageViewHolder.topText.setText(message.getSenderNickname());
        messageViewHolder.subText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));

        boolean myMessage = message.getSenderId().equals(messageViewHolder.getSelfMessageId());

        if (myMessage) {
//            messageViewHolder.subText.setGravity(Gravity.RIGHT);
            messageViewHolder.messsageAligner.setGravity(Gravity.LEFT);
//            messageViewHolder.messageContainer.setGravity(Gravity.RIGHT);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.extendedHPadding, messageViewHolder.vPadding, messageViewHolder.hPadding, messageViewHolder.vPadding);
        } else {
//            messageViewHolder.subText.setGravity(Gravity.LEFT);
            messageViewHolder.messsageAligner.setGravity(Gravity.LEFT);
//            messageViewHolder.messageContainer.setGravity(Gravity.LEFT);
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

}

class MessageViewHolder extends RecyclerView.ViewHolder {

    private static final int EXTEND_PADDING_MULTIPLIER = 10;

    LinearLayout messsageAligner;
    ViewGroup messageContainer;
    TextView messageText, subText, topText;
    String subMessageSeparator;
    int extendedHPadding, hPadding, vPadding;
    AutoAlignGridView messageEntitiesGrid;

    public String selfMessageId;
    public int primaryTextColor, errorTextColor;


    public MessageViewHolder(View itemView) {
        super(itemView);

        Context context = itemView.getContext();

        subMessageSeparator = context.getResources().getString(R.string.message_sub_separator);

        hPadding = (int) context.getResources().getDimension(R.dimen.listitem_horizontal_margin);
        vPadding = (int) context.getResources().getDimension(R.dimen.listitem_vertical_margin);
        extendedHPadding = (int) context.getResources().getDimension(R.dimen.message_extended_horizontal_margin);

        primaryTextColor = ColorUtil.retrieverColor(context, R.color.text_color_primary);
        errorTextColor = ColorUtil.retrieverColor(context, R.color.text_color_error);

        messsageAligner = itemView.findViewById(R.id.message_aligner);
        messageContainer = itemView.findViewById(R.id.message_container);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
        messageText.setMovementMethod(LinkMovementMethod.getInstance());
        subText = (TextView) itemView.findViewById(R.id.message_sub);
        topText = (TextView) itemView.findViewById(R.id.message_top);

        messageEntitiesGrid = itemView.findViewById(R.id.message_grid_entities);
    }

    public String getSelfMessageId() {
        if (selfMessageId == null) {
            FetLifeApplication fetLifeApplication = (FetLifeApplication) messageContainer.getContext().getApplicationContext();
            Member currentUser = fetLifeApplication.getUserSessionManager().getCurrentUser();
            if (currentUser != null) {
                selfMessageId = currentUser.getId();
            }
        }
        return selfMessageId;
    }
}