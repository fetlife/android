package com.bitlove.fetlife.legacy.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.WritingReference;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.WritingReference_Table;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Writing_Table;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Rsvp;
import com.bitlove.fetlife.legacy.util.DateUtil;
import com.bitlove.fetlife.legacy.util.ServerIdUtil;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WritingsRecyclerAdapter extends ResourceListRecyclerAdapter<Writing, WritingsViewHolder> {

    private static final String DATE_INTERVAL_SEPARATOR = " - ";
    private static final String LOCATION_SEPARATOR = " - ";

    private String memberId;
    protected List<Writing> itemList;
    private HashMap<String,Rsvp.RsvpStatus> statusMap;

    public WritingsRecyclerAdapter(String memberId) {
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

    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            if (ServerIdUtil.isServerId(memberId)) {
                if (ServerIdUtil.containsServerId(memberId)) {
                    memberId = ServerIdUtil.getLocalId(memberId);
                } else {
                    return;
                }
            }
            List<WritingReference> writingReferences = new Select().from(WritingReference.class).where(WritingReference_Table.userId.is(memberId)).queryList();
            List<String> writingIds = new ArrayList<>();
            for (WritingReference writingReference : writingReferences) {
                writingIds.add(writingReference.getId());
            }
            itemList = new Select().from(Writing.class).where(Writing_Table.id.in(writingIds)).orderBy(OrderBy.fromProperty(Writing_Table.date).descending()).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public WritingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_writing, parent, false);
        return new WritingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WritingsViewHolder holder, int position) {
        final Writing writing = itemList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResourceClickListener.onItemClick(writing);
            }
        });
        holder.writingHeader.setText(writing.getTitle());
        holder.writingBody.setText(writing.getHtmlBody());
        long dateLong = DateUtil.parseDate(writing.getCreatedAt(),true);
        String dateText = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT, SimpleDateFormat.SHORT).format(dateLong);
        holder.writingDate.setText(dateText);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    protected boolean useSwipe() {
        return false;
    }

    @Override
    protected void onItemRemove(WritingsViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }

}

class WritingsViewHolder extends SwipeableViewHolder {

    TextView writingHeader, writingDate, writingBody;

    public WritingsViewHolder(View itemView) {
        super(itemView);
        writingHeader = itemView.findViewById(R.id.writing_header);
        writingBody = itemView.findViewById(R.id.writing_body);
        writingDate = itemView.findViewById(R.id.writing_date);
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