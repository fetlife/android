package com.bitlove.fetlife.nativeapp.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.NotificationHistoryItem_Table;
import com.bitlove.fetlife.nativeapp.view.screen.resource.ResourceListActivity;
import com.google.android.material.snackbar.Snackbar;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationHistoryRecyclerAdapter extends ResourceListRecyclerAdapter<NotificationHistoryItem, NotificationHistoryItemViewHolder> {

    private static final int NOTIFICATION_HISTORYITEM_REMOVE_UNDO_DURATION = 5000;

    static class Undo {
        AtomicBoolean pending = new AtomicBoolean(true);
    }

    private List<NotificationHistoryItem> notificationHistoryItems;

    public NotificationHistoryRecyclerAdapter() {
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
        try {
            notificationHistoryItems = new Select().from(NotificationHistoryItem.class).orderBy(NotificationHistoryItem_Table.timeStamp,false).queryList();
        } catch (Throwable t) {
            notificationHistoryItems = new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        return notificationHistoryItems.size();
    }

    @Override
    public NotificationHistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_notificationhistory, parent, false);
        return new NotificationHistoryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationHistoryItemViewHolder notificationHistoryItemViewHolder, int position) {

        final NotificationHistoryItem notificationHistoryItem = notificationHistoryItems.get(position);

        notificationHistoryItemViewHolder.titleText.setText(notificationHistoryItem.getDisplayHeader());
        notificationHistoryItemViewHolder.messageText.setText(notificationHistoryItem.getDisplayMessage());
        notificationHistoryItemViewHolder.timeText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(notificationHistoryItem.getTimeStamp())));

        notificationHistoryItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onItemClick(notificationHistoryItem);
                }
            }
        });
    }

    @Override
    protected int getSwipeDirections() {
        return ItemTouchHelper.LEFT;
    }

    @Override
    protected void onItemRemove(final NotificationHistoryItemViewHolder viewHolder, final RecyclerView recyclerView, boolean swipedRight) {
        final int position = viewHolder.getAdapterPosition();
        final NotificationHistoryItem notificationHistoryItem = notificationHistoryItems.get(position);

        final Undo undo = new Undo();

        Snackbar snackbar = Snackbar
                .make(recyclerView, R.string.text_notificationhistoryitem_removed, Snackbar.LENGTH_LONG)
                .setActionTextColor(recyclerView.getContext().getResources().getColor(R.color.text_color_link))
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (undo.pending.compareAndSet(true, false)) {
                            notificationHistoryItems.add(position, notificationHistoryItem);
                            notifyItemInserted(position);
                            recyclerView.scrollToPosition(position);
                        } else {
                            Context context = recyclerView.getContext();
                            if (context instanceof ResourceListActivity) {
                                ((ResourceListActivity) context).showToast(context.getString(R.string.undo_no_longer_possible));
                            }
                        }
                    }
                });
        snackbar.getView().setBackgroundColor(recyclerView.getContext().getResources().getColor(R.color.color_reject));

        notificationHistoryItems.remove(position);
        notifyItemRemoved(position);
        snackbar.show();

        startDelayedItemRemove(notificationHistoryItem, undo, NOTIFICATION_HISTORYITEM_REMOVE_UNDO_DURATION, recyclerView.getContext());

    }

    private void startDelayedItemRemove(final NotificationHistoryItem notificationHistoryItem, final Undo undo, final int undoDuration, final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FetLifeApplication.getInstance().getUserSessionManager().getCurrentUser() != null) {
                    if (undo.pending.compareAndSet(true, false)) {
                        notificationHistoryItem.delete();
                    }
                } else {
                    //Non-critical case so do not bother the user with notification
                }
            }
        }, undoDuration);
    }


}

class NotificationHistoryItemViewHolder extends SwipeableViewHolder {

    TextView titleText, messageText, timeText;
    View swipeableLayout, removeBackgroundLayout, rejectBackground;

    public NotificationHistoryItemViewHolder(View itemView) {
        super(itemView);

        titleText = (TextView) itemView.findViewById(R.id.notificationhistoryitem_title);
        messageText = (TextView) itemView.findViewById(R.id.notificationhistoryitem_message);
        timeText = (TextView) itemView.findViewById(R.id.notificationhistoryitem_time);

        swipeableLayout = itemView.findViewById(R.id.swipeable_layout);
        removeBackgroundLayout = itemView.findViewById(R.id.notificationhistory_remove_layout);
    }

    @Override
    public View getSwipeableLayout() {
        return swipeableLayout;
    }

    @Override
    public View getSwipeRightBackground() {
        return null;
    }

    @Override
    public View getSwipeLeftBackground() {
        return removeBackgroundLayout;
    }
}

