package com.bitlove.fetlife.nativeapp.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupComment;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupComment_Table;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost_Table;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.MessageEntities;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.nativeapp.util.ColorUtil;
import com.bitlove.fetlife.nativeapp.util.ServerIdUtil;
import com.bitlove.fetlife.nativeapp.util.StringUtil;
import com.bitlove.fetlife.nativeapp.util.UrlUtil;
import com.bitlove.fetlife.nativeapp.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.nativeapp.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.nativeapp.view.widget.AutoAlignGridView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GroupMessagesRecyclerAdapter extends RecyclerView.Adapter<GroupMessageViewHolder> {

    private static final String REQUEST_MORE_CLIENT_ID = GroupMessagesRecyclerAdapter.class.getSimpleName() + "%request_more";

    private final GroupMessageClickListener groupMessageClickListener;

    public static final int ITEM_PER_PAGE = 15;

    public interface GroupMessageClickListener {
        void onMemberClick(String memberId);
        void onMessageMetaClicked(String meta);
        void onRequestPageClick(int page);
    }

    private static final float PENDING_ALPHA = 0.5f;

    private GroupPost groupDiscussion;
    private String groupDiscussionId;
    private String groupId;

    private List<GroupComment> itemList;
    private int requestedPageCount;

    public GroupMessagesRecyclerAdapter(String groupId, String groupDiscussionId, GroupMessageClickListener groupMessageClickListener) {
        this.groupId = groupId;
        this.groupDiscussionId = groupDiscussionId;
        this.groupMessageClickListener = groupMessageClickListener;
        this.requestedPageCount = 1;
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
        if (groupId != null && ServerIdUtil.isServerId(groupId) && ServerIdUtil.containsServerId(groupId)) {
            groupId = ServerIdUtil.getLocalId(groupId);
        }
        if (groupDiscussionId != null && ServerIdUtil.isServerId(groupDiscussionId) && ServerIdUtil.containsServerId(groupDiscussionId)) {
            groupDiscussionId = ServerIdUtil.getLocalId(groupDiscussionId);
        }
        //TODO: think of moving to separate thread with specific DB executor
        groupDiscussion = new Select().from(GroupPost.class).where(GroupPost_Table.id.is(groupDiscussionId)).querySingle();
        if (groupDiscussion == null) {
            return;
        }
        itemList = new Select().from(GroupComment.class).where(GroupComment_Table.groupPostId.is(groupDiscussionId)).orderBy(GroupComment_Table.pending,false).orderBy(GroupComment_Table.date,false).orderBy(GroupComment_Table.id,false).limit(requestedPageCount * ITEM_PER_PAGE).queryList();
        if (itemList.size() == requestedPageCount *ITEM_PER_PAGE) {
            GroupComment requestMorePlaceHolder = new GroupComment();
            requestMorePlaceHolder.setBody(groupDiscussion.getBody());
            requestMorePlaceHolder.setClientId(REQUEST_MORE_CLIENT_ID);
            itemList.add(requestMorePlaceHolder);
        }
        GroupComment baseComment = new GroupComment();
        baseComment.setBody(groupDiscussion.getBody());
        baseComment.setAvatarLink(groupDiscussion.getAvatarLink());
        baseComment.setSenderId(groupDiscussion.getMemberId());
        baseComment.setPending(false);
        baseComment.setCreatedAt(groupDiscussion.getCreatedAt());
        baseComment.setGroupId(groupId);
        baseComment.setGroupPostId(groupDiscussionId);
        baseComment.setSenderNickname(groupDiscussion.getNickname());
        baseComment.setId(groupDiscussion.getId());
        baseComment.setClientId(groupDiscussion.getId());
        itemList.add(baseComment);
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public GroupComment getItem(int position) {
        return itemList.get(position);
    }

    public GroupPost getGroupPost() {
        return groupDiscussion;
    }

    @Override
    public GroupMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_message, parent, false);
        return new GroupMessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupMessageViewHolder messageViewHolder, int position) {
        final GroupComment groupMessage = itemList.get(position);

        if (REQUEST_MORE_CLIENT_ID.equals(groupMessage.getClientId())) {
            messageViewHolder.subText.setText("...");
            messageViewHolder.subText.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
            messageViewHolder.subText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,42);
            messageViewHolder.subText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.hasOnClickListeners()) {
                        groupMessageClickListener.onRequestPageClick(++requestedPageCount);
                        view.setOnClickListener(null);
                    }
                }
            });
            messageViewHolder.topText.setVisibility(View.GONE);
            messageViewHolder.messageTextContainer.setVisibility(View.GONE);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.extendedHPadding, 0, messageViewHolder.extendedHPadding, messageViewHolder.vPadding);
            return;
        }

        CharSequence messageBody = groupMessage.getHtmlBody();
        MessageEntities messageEntities = StringUtil.getMessageEntities(groupMessage.getEntitiesJson());

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

        messageViewHolder.topText.setVisibility(View.VISIBLE);
        messageViewHolder.messageTextContainer.setVisibility(View.VISIBLE);

        messageViewHolder.messageText.setText(messageBody);
//        messageViewHolder.subText.setText(groupMessage.getSenderNickname() + messageViewHolder.subMessageSeparator + SimpleDateFormat.getDateTimeInstance().format(new Date(groupMessage.getDate())));
        messageViewHolder.topText.setText(groupMessage.getSenderNickname());
        messageViewHolder.subText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(groupMessage.getDate())));

        messageViewHolder.subText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
        messageViewHolder.topText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupMessageClickListener.onMessageMetaClicked(GroupComment.MENTION_PREFIX + groupMessage.getSenderNickname() + " ");
            }
        });

        boolean myMessage = groupMessage.getSenderId().equals(messageViewHolder.getSelfMessageId());

        if (myMessage) {
//            messageViewHolder.subText.setGravity(Gravity.RIGHT);
//            messageViewHolder.messageContainer.setGravity(Gravity.RIGHT);
//            messageViewHolder.messageText.setGravity(Gravity.RIGHT);
            messageViewHolder.messsageAligner.setGravity(Gravity.LEFT);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.extendedHPadding, messageViewHolder.vPadding, messageViewHolder.hPadding, messageViewHolder.vPadding);
            messageViewHolder.memberAvatar.setVisibility(View.GONE);
            messageViewHolder.selfAvatar.setVisibility(View.VISIBLE);
            messageViewHolder.selfAvatar.setImageURI(groupMessage.getAvatarLink());
        } else {
//            messageViewHolder.subText.setGravity(Gravity.LEFT);
//            messageViewHolder.messageContainer.setGravity(Gravity.LEFT);
//            messageViewHolder.messageText.setGravity(Gravity.LEFT);
            messageViewHolder.messsageAligner.setGravity(Gravity.LEFT);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.hPadding, messageViewHolder.vPadding, messageViewHolder.extendedHPadding, messageViewHolder.vPadding);
            messageViewHolder.selfAvatar.setVisibility(View.GONE);
            messageViewHolder.memberAvatar.setVisibility(View.VISIBLE);
            messageViewHolder.memberAvatar.setImageURI(groupMessage.getAvatarLink());
        }

        messageViewHolder.memberAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupMessageClickListener != null) {
                    groupMessageClickListener.onMemberClick(groupMessage.getSenderId());
                }
            }
        });

        if (groupMessage.getPending()) {
            messageViewHolder.messageText.setTextColor(messageViewHolder.primaryTextColor);
            messageViewHolder.messageText.setAlpha(PENDING_ALPHA);
        } else if (groupMessage.getFailed()) {
            messageViewHolder.messageText.setAlpha(1f);
            messageViewHolder.messageText.setTextColor(messageViewHolder.errorTextColor);
        } else {
            messageViewHolder.messageText.setTextColor(messageViewHolder.primaryTextColor);
            messageViewHolder.messageText.setAlpha(1f);
        }
    }

}

class GroupMessageViewHolder extends RecyclerView.ViewHolder {

    private static final int EXTEND_PADDING_MULTIPLIER = 5;
    AutoAlignGridView messageEntitiesGrid;

    LinearLayout messsageAligner;
    ViewGroup messageContainer, messageTextContainer;
    TextView messageText, subText, topText;
    String subMessageSeparator;
    SimpleDraweeView memberAvatar, selfAvatar;
    int extendedHPadding, extendedVPadding, hPadding, vPadding;
    public String selfMessageId;
    public int primaryTextColor, errorTextColor;

    public GroupMessageViewHolder(View itemView) {
        super(itemView);

        Context context = itemView.getContext();

        subMessageSeparator = context.getResources().getString(R.string.message_sub_separator);

        hPadding = (int) context.getResources().getDimension(R.dimen.listitem_horizontal_margin);
        vPadding = (int) context.getResources().getDimension(R.dimen.listitem_vertical_margin);
        extendedHPadding = EXTEND_PADDING_MULTIPLIER * hPadding;
        extendedVPadding = EXTEND_PADDING_MULTIPLIER * vPadding;

        primaryTextColor = ColorUtil.retrieverColor(context, R.color.text_color_primary);
        errorTextColor = ColorUtil.retrieverColor(context, R.color.text_color_error);

        messsageAligner = itemView.findViewById(R.id.message_aligner);
        messageTextContainer = itemView.findViewById(R.id.message_text_container);
        messageContainer = itemView.findViewById(R.id.message_container);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
        messageText.setMovementMethod(LinkMovementMethod.getInstance());
        topText = (TextView) itemView.findViewById(R.id.message_top);
        subText = (TextView) itemView.findViewById(R.id.message_sub);
        memberAvatar = (SimpleDraweeView) itemView.findViewById(R.id.left_member_image);
        selfAvatar = (SimpleDraweeView) itemView.findViewById(R.id.right_member_image);

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