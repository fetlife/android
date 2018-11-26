package com.bitlove.fetlife.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.FriendRequestScreenModelObject;
import com.bitlove.fetlife.model.pojos.fetlife.db.SharedProfile;
import com.bitlove.fetlife.model.pojos.fetlife.db.SharedProfile_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.resource.ResourceListActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.recyclerview.widget.RecyclerView;

public class FriendRequestsRecyclerAdapter extends ResourceListRecyclerAdapter<FriendRequestScreenModelObject, FriendRequestScreenViewHolder> {

    private static final int FRIENDREQUEST_UNDO_DURATION = 5000;
    private static final int VIEWTYPE_HEADER = 0;
    private static final int VIEWTYPE_ITEM = 1;

    static class Undo {
        AtomicBoolean pending = new AtomicBoolean(true);
    }

    private List<FriendRequest> friendRequestList = new ArrayList<>();
    private List<SharedProfile> friendSuggestionList = new ArrayList<>();

    public FriendRequestsRecyclerAdapter(boolean clearItems) {
        if (clearItems) {
            clearItems();
        } else {
            loadItems();
        }
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

    @Override
    public int getItemCount() {
        return friendRequestList.size() + friendSuggestionList.size() + 2;
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            friendRequestList = new Select().from(FriendRequest.class).where(FriendRequest_Table.pending.is(false)).and(FriendRequest_Table.pendingState.in(FriendRequest.PendingState.NEW, FriendRequest.PendingState.REJECTED)).queryList();
        } catch (Throwable t) {
            friendRequestList = new ArrayList<>();
        }
        //TODO: think of moving to separate thread with specific DB executor
        try {
            friendSuggestionList = new Select().from(SharedProfile.class).where(SharedProfile_Table.pending.is(false)).queryList();
        } catch (Throwable t) {
            friendSuggestionList = new ArrayList<>();
        }
    }

    private void clearItems() {
        friendRequestList = new ArrayList<>();
        //TODO: think of moving to separate thread with specific DB executor
        try {
            new Delete().from(FriendRequest.class).where(FriendRequest_Table.pending.is(false)).query();
        } catch (Throwable t) {
        }
    }

    @Override
    protected void onItemRemove(final FriendRequestScreenViewHolder viewHolder, final RecyclerView recyclerView, boolean accepted) {
        int position = viewHolder.getAdapterPosition();
        if (position == 0 || position == friendRequestList.size()+1) {
            return;
        }
        if (--position < friendRequestList.size()) {
            onFriendRequestRemove(friendRequestList.get(position), position, viewHolder, recyclerView, accepted);
        } else {
            position--;
            position -= friendRequestList.size();
            onSharedProfileRemove(friendSuggestionList.get(position), position, viewHolder, recyclerView, accepted);
        }
    }

    void onSharedProfileRemove(final SharedProfile friendSuggestion, final int listPosition, final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView, boolean accepted) {

        final Undo undo = new Undo();

        final int adapterPosition = viewHolder.getAdapterPosition();

        Snackbar snackbar = Snackbar
                .make(recyclerView, accepted ? R.string.text_friendrequests_accepted :  R.string.text_friendrequests_rejected, Snackbar.LENGTH_LONG)
                .setActionTextColor(recyclerView.getContext().getResources().getColor(R.color.text_color_link))
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (undo.pending.compareAndSet(true, false)) {
                            friendSuggestionList.add(listPosition, friendSuggestion);
                            notifyItemInserted(adapterPosition);
                            recyclerView.scrollToPosition(adapterPosition);
                        } else {
                            Context context = recyclerView.getContext();
                            if (context instanceof ResourceListActivity) {
                                ((ResourceListActivity)context).showToast(context.getString(R.string.undo_no_longer_possible));
                            }
                        }
                    }
                });
        snackbar.getView().setBackgroundColor(accepted ? recyclerView.getContext().getResources().getColor(R.color.color_accept) : recyclerView.getContext().getResources().getColor(R.color.color_reject));

        friendSuggestionList.remove(listPosition);
        notifyItemRemoved(adapterPosition);
        snackbar.show();

        startDelayedSharedProfileDecision(friendSuggestion, accepted, undo, FRIENDREQUEST_UNDO_DURATION, recyclerView.getContext());
    }

    private void startDelayedSharedProfileDecision(final SharedProfile friendSuggestion, final boolean accepted, final Undo undo, final int undoDuration, final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FetLifeApplication.getInstance().getUserSessionManager().getCurrentUser() != null) {
                    if (undo.pending.compareAndSet(true, false)) {
                        if (accepted) {
                            friendSuggestion.setPending(true);
                            friendSuggestion.save();
                            FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
                        } else {
                            friendSuggestion.delete();
                        }
                    }
                } else {
                    FetLifeApplication.getInstance().showLongToast(R.string.message_friend_decision_failed);
                }
            }
        }, undoDuration);
    }

    void onFriendRequestRemove(final FriendRequest friendRequest, final int listPosition, final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView, boolean accepted) {

        final int adapterPosition = viewHolder.getAdapterPosition();

        final Undo undo = new Undo();

        Snackbar snackbar = Snackbar
                .make(recyclerView, accepted ? R.string.text_friendrequests_accepted :  R.string.text_friendrequests_rejected, Snackbar.LENGTH_LONG)
                .setActionTextColor(recyclerView.getContext().getResources().getColor(R.color.text_color_link))
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (undo.pending.compareAndSet(true, false)) {
                            friendRequest.setPendingState(null);
                            friendRequestList.add(listPosition, friendRequest);
                            notifyItemInserted(adapterPosition);
                            recyclerView.scrollToPosition(adapterPosition);
                        } else {
                            Context context = recyclerView.getContext();
                            if (context instanceof ResourceListActivity) {
                                ((ResourceListActivity)context).showToast(context.getString(R.string.undo_no_longer_possible));
                            }
                        }
                    }
                });
        snackbar.getView().setBackgroundColor(accepted ? recyclerView.getContext().getResources().getColor(R.color.color_accept) : recyclerView.getContext().getResources().getColor(R.color.color_reject));

        friendRequest.setPendingState(accepted ? FriendRequest.PendingState.ACCEPTED : FriendRequest.PendingState.REJECTED);
        friendRequestList.remove(listPosition);
        notifyItemRemoved(adapterPosition);
        snackbar.show();

        startDelayedFriendRequestDecision(friendRequest, friendRequest.getPendingState(), undo, FRIENDREQUEST_UNDO_DURATION, recyclerView.getContext());
    }

    private void startDelayedFriendRequestDecision(final FriendRequest friendRequest, final FriendRequest.PendingState pendingState, final Undo undo, int friendrequestUndoDuration, final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FetLifeApplication.getInstance().getUserSessionManager().getCurrentUser() != null) {
                    if (undo.pending.compareAndSet(true, false)) {
                        friendRequest.setPending(true);
                        friendRequest.save();
                        FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
                    }
                } else {
                    FetLifeApplication.getInstance().showLongToast(R.string.message_friend_decision_failed);
                }
            }
        }, friendrequestUndoDuration);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == friendRequestList.size()+1) {
            return VIEWTYPE_HEADER;
        } else {
            return VIEWTYPE_ITEM;
        }
    }

    @Override
    public FriendRequestScreenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEWTYPE_HEADER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friendrequest_header, parent, false);
            return new FriendRequestHeaderViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friendrequest, parent, false);
            return new FriendRequestItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(FriendRequestScreenViewHolder friendRequestScreenViewHolder, int position) {
        if (getItemViewType(position) == VIEWTYPE_HEADER) {
            if (!(friendRequestScreenViewHolder instanceof  FriendRequestHeaderViewHolder)) {
                Crashlytics.logException(new ClassCastException("friendRequestScreenViewHolder is not a FriendRequestHeaderViewHolder"));
                return;
            }
            onBindHeaderViewHolder((FriendRequestHeaderViewHolder) friendRequestScreenViewHolder, position == 0);
        } else if (--position < friendRequestList.size()) {
            if (!(friendRequestScreenViewHolder instanceof  FriendRequestItemViewHolder)) {
                Crashlytics.logException(new ClassCastException("friendRequestScreenViewHolder is not a FriendRequestItemViewHolder"));
                return;
            }
            onBindFriendRequestItemViewHolder((FriendRequestItemViewHolder) friendRequestScreenViewHolder, friendRequestList.get(position));
        } else {
            if (!(friendRequestScreenViewHolder instanceof  FriendRequestItemViewHolder)) {
                Crashlytics.logException(new ClassCastException("friendRequestScreenViewHolder is not a FriendRequestItemViewHolder"));
                return;
            }
            position--;
            position -= friendRequestList.size();
            onBindSharedProfileViewHolder((FriendRequestItemViewHolder) friendRequestScreenViewHolder, friendSuggestionList.get(position));
        }
    }

    private void onBindHeaderViewHolder(FriendRequestHeaderViewHolder friendRequestHeaderViewHolder, boolean friendRequestItem) {
        if (friendRequestItem) {
            friendRequestHeaderViewHolder.headerText.setText(R.string.header_friendrequest);
            friendRequestHeaderViewHolder.itemView.setVisibility(friendRequestList.isEmpty() ? View.GONE : View.VISIBLE);
        } else {
            friendRequestHeaderViewHolder.headerText.setText(R.string.header_friendsuggestion);
            friendRequestHeaderViewHolder.itemView.setVisibility(friendSuggestionList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    public void onBindFriendRequestItemViewHolder(FriendRequestItemViewHolder friendRequestItemViewHolder, final FriendRequest friendRequest) {

        friendRequestItemViewHolder.headerText.setText(friendRequest.getNickname());
        friendRequestItemViewHolder.upperText.setText(friendRequest.getMetaInfo());

//        friendRequestItemViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(FriendRequest.getRoughtDate())));

        friendRequestItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onItemClick(friendRequest);
                }
            }
        });

        friendRequestItemViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onAvatarClick(friendRequest);
                }
            }
        });

        friendRequestItemViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = friendRequest.getAvatarLink();
        friendRequestItemViewHolder.avatarImage.setImageURI(avatarUrl);
//        imageLoader.loadImage(friendRequestItemViewHolder.itemView.getContext(), avatarUrl, friendRequestItemViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

    public void onBindSharedProfileViewHolder(FriendRequestItemViewHolder friendRequestItemViewHolder, final SharedProfile friendSuggestion) {

        Member member = friendSuggestion.getMember();

        friendRequestItemViewHolder.headerText.setText(member.getNickname());
        friendRequestItemViewHolder.upperText.setText(member.getMetaInfo());

//        friendRequestItemViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(FriendRequest.getRoughtDate())));

        friendRequestItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onItemClick(friendSuggestion);
                }
            }
        });

        friendRequestItemViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onAvatarClick(friendSuggestion);
                }
            }
        });

        friendRequestItemViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = member.getAvatarLink();
        friendRequestItemViewHolder.avatarImage.setImageURI(avatarUrl);
//        imageLoader.loadImage(friendRequestItemViewHolder.itemView.getContext(), avatarUrl, friendRequestItemViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

}

abstract class FriendRequestScreenViewHolder extends SwipeableViewHolder {
    public FriendRequestScreenViewHolder(View itemView) {
        super(itemView);
    }
}

class FriendRequestHeaderViewHolder extends FriendRequestScreenViewHolder {

    TextView headerText;

    public FriendRequestHeaderViewHolder(View itemView) {
        super(itemView);
        headerText = (TextView) itemView.findViewById(R.id.friendrequest_header);
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

class FriendRequestItemViewHolder extends FriendRequestScreenViewHolder {

    SimpleDraweeView avatarImage;
    TextView headerText, upperText, dateText;
    View swipableLayout, acceptBackgroundLayout, rejectBackgroundLayout;

    public FriendRequestItemViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.friendrequest_header);

        swipableLayout = itemView.findViewById(R.id.swipeable_layout);
        acceptBackgroundLayout = itemView.findViewById(R.id.friendrequest_accept_layout);
        rejectBackgroundLayout = itemView.findViewById(R.id.friendrequest_reject_layout);

        upperText = (TextView) itemView.findViewById(R.id.friendrequest_upper);
        dateText = (TextView) itemView.findViewById(R.id.friendrequest_right);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.friendrequest_icon);
    }

    @Override
    public View getSwipeableLayout() {
        return swipableLayout;
    }

    @Override
    public View getSwipeRightBackground() {
        return acceptBackgroundLayout;
    }

    @Override
    public View getSwipeLeftBackground() {
        return rejectBackgroundLayout;
    }
}