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
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.NotificationHistoryItem;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationHistoryRecyclerAdapter extends RecyclerView.Adapter<NotificationHistoryItemViewHolder> {

    private static final int NOTIFICATION_HISTORYITEM_REMOVE_UNDO_DURATION = 5000;

    public interface OnNotificationHistoryItemClickListener {
        public void onItemClick(NotificationHistoryItem notificationHistoryItem);
    }

    static class Undo {
        AtomicBoolean pending = new AtomicBoolean(true);
    }

    private List<NotificationHistoryItem> notificationHistoryItems;
    private OnNotificationHistoryItemClickListener onNotificationHistoryClickListener;

    public NotificationHistoryRecyclerAdapter() {
        loadItems();
    }

    public void setOnNotificationHistoryItemClickListener(OnNotificationHistoryItemClickListener notificationHistoryItemClickListener) {
        this.onNotificationHistoryClickListener = notificationHistoryItemClickListener;
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            notificationHistoryItems = new Select().from(NotificationHistoryItem.class).queryList();
        } catch (Throwable t) {
            notificationHistoryItems = new ArrayList<>();
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                NotificationHistoryRecyclerAdapter.this.onItemRemove(viewHolder, recyclerView);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected(((NotificationHistoryItemViewHolder) viewHolder).swipableLayout);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                getDefaultUIUtil().clearView(((NotificationHistoryItemViewHolder) viewHolder).swipableLayout);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDraw(c, recyclerView, ((NotificationHistoryItemViewHolder) viewHolder).swipableLayout, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDrawOver(c, recyclerView, ((NotificationHistoryItemViewHolder) viewHolder).swipableLayout, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
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
                if (undo.pending.compareAndSet(true, false)) {
                    notificationHistoryItem.delete();
                }
            }
        }, undoDuration);
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
                if (onNotificationHistoryClickListener != null) {
                    onNotificationHistoryClickListener.onItemClick(notificationHistoryItem);
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
        return notificationHistoryItems.size();
    }
}

class NotificationHistoryItemViewHolder extends RecyclerView.ViewHolder {

    TextView titleText, messageText, timeText;
    View swipableLayout, removeBackgroundLayout;

    public NotificationHistoryItemViewHolder(View itemView) {
        super(itemView);

        titleText = (TextView) itemView.findViewById(R.id.notificationhistoryitem_title);
        messageText = (TextView) itemView.findViewById(R.id.notificationhistoryitem_message);
        timeText = (TextView) itemView.findViewById(R.id.notificationhistoryitem_time);

        swipableLayout = itemView.findViewById(R.id.swipeable_layout);
        removeBackgroundLayout = itemView.findViewById(R.id.notificationhistory_remove_layout);
    }
}

