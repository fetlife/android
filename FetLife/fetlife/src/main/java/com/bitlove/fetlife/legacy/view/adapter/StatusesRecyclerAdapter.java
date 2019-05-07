package com.bitlove.fetlife.legacy.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.StatusReference;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.StatusReference_Table;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Status_Table;
import com.bitlove.fetlife.legacy.util.ServerIdUtil;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatusesRecyclerAdapter extends ResourceListRecyclerAdapter<Status, StatusViewHolder> {

    private String memberId;
    private List<Status> itemList;

    public StatusesRecyclerAdapter(String memberId) {
        this.memberId = memberId;
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
            if (ServerIdUtil.isServerId(memberId)) {
                if (ServerIdUtil.containsServerId(memberId)) {
                    memberId = ServerIdUtil.getLocalId(memberId);
                } else {
                    return;
                }
            }
            List<StatusReference> statusReferences = new Select().from(StatusReference.class).where(StatusReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(StatusReference_Table.id).ascending().collate(Collate.NOCASE)).queryList();
            List<String> statusIds = new ArrayList<>();
            for (StatusReference statusReference : statusReferences) {
                statusIds.add(statusReference.getId());
            }
            itemList = new Select().from(Status.class).where(Status_Table.id.in(statusIds)).orderBy(OrderBy.fromProperty(Status_Table.date).descending()).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_status, parent, false);
        return new StatusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatusViewHolder holder, int position) {
        final Status status = itemList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResourceClickListener.onItemClick(status);
            }
        });
        holder.statusText.setText(status.getHtmlBody());
        holder.statusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResourceClickListener.onItemClick(status);
            }
        });
        holder.statusDate.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(status.getDate())));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    @Override
    protected void onItemRemove(StatusViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {

    }
}

class StatusViewHolder extends SwipeableViewHolder {

    TextView statusText, statusDate;

    public StatusViewHolder(View itemView) {
        super(itemView);
        statusText = (TextView) itemView.findViewById(R.id.status_text);
        statusDate = (TextView) itemView.findViewById(R.id.status_date);
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
