package com.bitlove.fetlife.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.pojos.FriendRequest_Table;
import com.bitlove.fetlife.model.pojos.FriendSuggestion;
import com.bitlove.fetlife.model.pojos.FriendSuggestion_Table;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FriendRequestsRecyclerAdapter extends RecyclerView.Adapter<FriendRequestViewHolder> {

    private static final int FRIENDREQUEST_UNDO_DURATION = 5000;
    private static final int VIEWTYPE_HEADER = 0;
    private static final int VIEWTYPE_ITEM = 1;

    public interface OnFriendRequestClickListener {
        public void onItemClick(FriendRequest friendRequest);
        public void onAvatarClick(FriendRequest friendRequest);
    }

    public interface OnFriendSuggestionClickListener {
        public void onItemClick(FriendSuggestion friendSuggestion);
        public void onAvatarClick(FriendSuggestion friendSuggestion);
    }

    static class Undo {
        AtomicBoolean pending = new AtomicBoolean(true);
    }

    private final ImageLoader imageLoader;

    private List<FriendRequest> friendRequestList;
    private List<FriendSuggestion> friendSuggestionList;
    OnFriendRequestClickListener onFriendRequestClickListener;
    OnFriendSuggestionClickListener onFriendSuggestionClickListener;

    public FriendRequestsRecyclerAdapter(ImageLoader imageLoader, boolean clearItems) {
        this.imageLoader = imageLoader;
        if (clearItems) {
            clearItems();
        } else {
            loadItems();
        }
    }

    public void setOnFriendRequestClickListener(OnFriendRequestClickListener onFriendRequestClickListener) {
        this.onFriendRequestClickListener = onFriendRequestClickListener;
    }

    public void setOnFriendSuggestionClickListener(OnFriendSuggestionClickListener onFriendSuggestionClickListener) {
        this.onFriendSuggestionClickListener = onFriendSuggestionClickListener;
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            friendRequestList = new Select().from(FriendRequest.class).where(FriendRequest_Table.pending.is(false)).queryList();
        } catch (Throwable t) {
            friendRequestList = new ArrayList<>();
        }
        //TODO: think of moving to separate thread with specific DB executor
        try {
            friendSuggestionList = new Select().from(FriendSuggestion.class).where(FriendSuggestion_Table.pending.is(false)).queryList();
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
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (getItemViewType(viewHolder.getAdapterPosition()) == VIEWTYPE_HEADER) {
                    return;
                }
                FriendRequestsRecyclerAdapter.this.onItemRemove(viewHolder, recyclerView, swipeDir == ItemTouchHelper.RIGHT);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    if (getItemViewType(viewHolder.getAdapterPosition()) == VIEWTYPE_HEADER) {
                        return;
                    }
                    getDefaultUIUtil().onSelected(((FriendRequestViewHolder) viewHolder).swipableLayout);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (getItemViewType(viewHolder.getAdapterPosition()) == VIEWTYPE_HEADER) {
                    return;
                }
                FriendRequestViewHolder friendRequestViewHolder = ((FriendRequestViewHolder) viewHolder);
                friendRequestViewHolder.acceptBackgroundLayout.setVisibility(View.GONE);
                friendRequestViewHolder.rejectBackgroundLayout.setVisibility(View.GONE);
                getDefaultUIUtil().clearView(((FriendRequestViewHolder) viewHolder).swipableLayout);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (getItemViewType(viewHolder.getAdapterPosition()) == VIEWTYPE_HEADER) {
                    return;
                }
                getDefaultUIUtil().onDraw(c, recyclerView, ((FriendRequestViewHolder) viewHolder).swipableLayout, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (getItemViewType(viewHolder.getAdapterPosition()) == VIEWTYPE_HEADER) {
                    return;
                }
                FriendRequestViewHolder friendRequestViewHolder = ((FriendRequestViewHolder) viewHolder);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
                    if (dX > 0) {
                        friendRequestViewHolder.acceptBackgroundLayout.setVisibility(View.VISIBLE);
                        friendRequestViewHolder.rejectBackgroundLayout.setVisibility(View.GONE);
                    } else if (dX < 0) {
                        friendRequestViewHolder.acceptBackgroundLayout.setVisibility(View.GONE);
                        friendRequestViewHolder.rejectBackgroundLayout.setVisibility(View.VISIBLE);
                    } else {
                        friendRequestViewHolder.acceptBackgroundLayout.setVisibility(View.GONE);
                        friendRequestViewHolder.rejectBackgroundLayout.setVisibility(View.GONE);
                    }
                } else {
                    friendRequestViewHolder.acceptBackgroundLayout.setVisibility(View.GONE);
                    friendRequestViewHolder.rejectBackgroundLayout.setVisibility(View.GONE);
                }
                getDefaultUIUtil().onDrawOver(c, recyclerView, friendRequestViewHolder.swipableLayout, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView, boolean accepted) {
        int position = viewHolder.getAdapterPosition();
        if (position == 0 || position == friendRequestList.size()+1) {
            return;
        }
        if (--position < friendRequestList.size()) {
            onFriendRequestRemove(friendRequestList.get(position), position, viewHolder, recyclerView, accepted);
        } else {
            position--;
            position -= friendSuggestionList.size();
            onFriendSuggestionRemove(friendSuggestionList.get(position), position, viewHolder, recyclerView, accepted);
        }
    }

    void onFriendSuggestionRemove(final FriendSuggestion friendSuggestion, final int listPosition, final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView, boolean accepted) {

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

        startDelayedFriendSuggestionDecision(friendSuggestion, accepted, undo, FRIENDREQUEST_UNDO_DURATION, recyclerView.getContext());
    }

    private void startDelayedFriendSuggestionDecision(final FriendSuggestion friendSuggestion, final boolean accepted, final Undo undo, final int undoDuration, final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (undo.pending.compareAndSet(true, false)) {
                    if (accepted) {
                        friendSuggestion.setPending(true);
                        friendSuggestion.save();
                        FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_SEND_FRIENDREQUESTS);
                    } else {
                        friendSuggestion.delete();
                    }
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
                if (undo.pending.compareAndSet(true, false)) {
                    friendRequest.setPending(true);
                    friendRequest.save();
                    FetLifeApiIntentService.startApiCall(context, FetLifeApiIntentService.ACTION_APICALL_SEND_FRIENDREQUESTS);
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
    public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEWTYPE_HEADER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friendrequest_header, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friendrequest, parent, false);
        }
        return new FriendRequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendRequestViewHolder friendRequestViewHolder, int position) {
        if (getItemViewType(position) == VIEWTYPE_HEADER) {
            onBindHeaderViewHolder(friendRequestViewHolder, position == 0);
        } else if (--position < friendRequestList.size()) {
            onBindFriendReuestViewHolder(friendRequestViewHolder, friendRequestList.get(position));
        } else {
            position--;
            position -= friendRequestList.size();
            onBindFriendSuggestionViewHolder(friendRequestViewHolder, friendSuggestionList.get(position));
        }
    }

    private void onBindHeaderViewHolder(FriendRequestViewHolder friendRequestViewHolder, boolean friendRequestItem) {
        if (friendRequestItem) {
            friendRequestViewHolder.headerText.setText(R.string.header_friendrequest);
            friendRequestViewHolder.itemView.setVisibility(friendRequestList.isEmpty() ? View.GONE : View.VISIBLE);
        } else {
            friendRequestViewHolder.headerText.setText(R.string.header_friendsuggestion);
            friendRequestViewHolder.itemView.setVisibility(friendSuggestionList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    public void onBindFriendReuestViewHolder(FriendRequestViewHolder friendRequestViewHolder, final FriendRequest friendRequest) {

        friendRequestViewHolder.headerText.setText(friendRequest.getNickname());
        friendRequestViewHolder.upperText.setText(friendRequest.getMetaInfo());

//        FriendRequestViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(FriendRequest.getDate())));

        friendRequestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFriendRequestClickListener != null) {
                    onFriendRequestClickListener.onItemClick(friendRequest);
                }
            }
        });

        friendRequestViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFriendRequestClickListener != null) {
                    onFriendRequestClickListener.onAvatarClick(friendRequest);
                }
            }
        });

        friendRequestViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = friendRequest.getAvatarLink();
        imageLoader.loadImage(friendRequestViewHolder.itemView.getContext(), avatarUrl, friendRequestViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

    public void onBindFriendSuggestionViewHolder(FriendRequestViewHolder FriendRequestViewHolder, final FriendSuggestion friendSuggestion) {

        FriendRequestViewHolder.headerText.setText(friendSuggestion.getNickname());
        FriendRequestViewHolder.upperText.setText(friendSuggestion.getMetaInfo());

//        FriendRequestViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(FriendRequest.getDate())));

        FriendRequestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFriendSuggestionClickListener != null) {
                    onFriendSuggestionClickListener.onItemClick(friendSuggestion);
                }
            }
        });

        FriendRequestViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFriendSuggestionClickListener != null) {
                    onFriendSuggestionClickListener.onAvatarClick(friendSuggestion);
                }
            }
        });

        FriendRequestViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = friendSuggestion.getAvatarLink();
        imageLoader.loadImage(FriendRequestViewHolder.itemView.getContext(), avatarUrl, FriendRequestViewHolder.avatarImage, R.drawable.dummy_avatar);
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
        return friendRequestList.size() + friendSuggestionList.size() + 2;
    }
}

class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    ImageView avatarImage;
    TextView headerText, upperText, dateText, lowerText;
    View swipableLayout, acceptBackgroundLayout, rejectBackgroundLayout;

    public FriendRequestViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.friendrequest_header);

        swipableLayout = itemView.findViewById(R.id.swipeable_layout);
        acceptBackgroundLayout = itemView.findViewById(R.id.friendrequest_accept_layout);
        rejectBackgroundLayout = itemView.findViewById(R.id.friendrequest_reject_layout);

        upperText = (TextView) itemView.findViewById(R.id.friendrequest_upper);
        dateText = (TextView) itemView.findViewById(R.id.friendrequest_right);
        lowerText = (TextView) itemView.findViewById(R.id.friendrequest_lower);
        avatarImage = (ImageView) itemView.findViewById(R.id.friendrequest_icon);
    }
}