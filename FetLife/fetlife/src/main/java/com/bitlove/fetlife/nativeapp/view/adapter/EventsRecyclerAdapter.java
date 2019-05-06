package com.bitlove.fetlife.nativeapp.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.EventReference;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.EventReference_Table;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Event_Table;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json.Rsvp;
import com.bitlove.fetlife.nativeapp.util.DateUtil;
import com.bitlove.fetlife.nativeapp.util.ServerIdUtil;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventsRecyclerAdapter extends ResourceListRecyclerAdapter<Event, EventsViewHolder> {

    private static final String DATE_INTERVAL_SEPARATOR = " - ";
    private static final String LOCATION_SEPARATOR = " - ";

    private String memberId;
    protected List<Event> itemList;
    private HashMap<String,Rsvp.RsvpStatus> statusMap;

    public EventsRecyclerAdapter(String memberId) {
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
            List<EventReference> eventReferences = new Select().from(EventReference.class).where(EventReference_Table.userId.is(memberId)).and(EventReference_Table.rsvpStatus.isNot(Rsvp.RsvpStatus.BANNED)).orderBy(OrderBy.fromProperty(EventReference_Table.id).ascending().collate(Collate.NOCASE)).queryList();
            List<String> eventIds = new ArrayList<>();
            statusMap = new HashMap<String,Rsvp.RsvpStatus>();
            for (EventReference eventReference : eventReferences) {
                eventIds.add(eventReference.getId());
                statusMap.put(eventReference.getId(),eventReference.getRsvpStatus());
            }
            itemList = new Select().from(Event.class).where(Event_Table.id.in(eventIds)).orderBy(OrderBy.fromProperty(Event_Table.date).ascending()).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_event, parent, false);
        return new EventsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {
        final Event event = itemList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResourceClickListener.onItemClick(event);
            }
        });
        holder.eventName.setText(event.getName());
        holder.eventName.setVisibility(TextUtils.isEmpty(event.getName()) ? View.GONE : View.VISIBLE);
        holder.eventTagline.setText(event.getTagline());
        holder.eventTagline.setVisibility(TextUtils.isEmpty(event.getTagline()) ? View.GONE : View.VISIBLE);
        holder.eventLocation.setText(event.getLocation() + LOCATION_SEPARATOR + event.getAddress());
        holder.eventLocation.setVisibility(TextUtils.isEmpty(event.getLocation()) && TextUtils.isEmpty(event.getAddress()) ? View.GONE : View.VISIBLE);
        String startDateTime = event.getStartDateTime();
        String endDateTime = event.getEndDateTime();
        long startTimeLong = !TextUtils.isEmpty(startDateTime) ? DateUtil.parseDate(startDateTime,true) : -1;
        long endTimeLong = !TextUtils.isEmpty(endDateTime) ? DateUtil.parseDate(endDateTime,true) : -1;
        if (startTimeLong > -1) {
            startDateTime = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT,SimpleDateFormat.SHORT).format(startTimeLong);
            if (endTimeLong > -1) {
                endDateTime = ((endTimeLong - startTimeLong) > 24*60*60*1000) ? SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT,SimpleDateFormat.SHORT).format(endTimeLong) : SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(endTimeLong);
            } else {
                endDateTime = "";
            }
        } else {
            startDateTime = "";
            if (endTimeLong > -1) {
                endDateTime = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT,SimpleDateFormat.SHORT).format(endTimeLong);
            } else {
                endDateTime = "";
            }
        }
        holder.eventDate.setText(startDateTime + DATE_INTERVAL_SEPARATOR + endDateTime);
        holder.eventDate.setVisibility((startTimeLong + endTimeLong) > 0 ? View.VISIBLE : View.GONE);
        if (statusMap != null) {
            holder.eventRsvpContainer.setVisibility(View.VISIBLE);
            boolean going = statusMap.get(event.getId()) == Rsvp.RsvpStatus.YES;
            holder.eventRsvpText.setText(going ? R.string.menu_event_going : R.string.menu_event_maybe);
            holder.eventRsvpIcon.setImageResource(going ? R.drawable.ic_event_going_24dp_black : R.drawable.ic_event_maybe_going_24dp_black);
        } else {
            holder.eventRsvpContainer.setVisibility(View.GONE);
        }
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
    protected void onItemRemove(EventsViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }

}
class EventsViewHolder extends SwipeableViewHolder {

    View eventRsvpContainer;
    ImageView eventRsvpIcon;
    TextView eventName, eventDate, eventLocation, eventTagline, eventRsvpText;

    public EventsViewHolder(View itemView) {
        super(itemView);
        eventName = (TextView) itemView.findViewById(R.id.event_name);
        eventTagline = (TextView) itemView.findViewById(R.id.event_tagline);
        eventLocation = (TextView) itemView.findViewById(R.id.event_location);
        eventDate = (TextView) itemView.findViewById(R.id.event_date);
        eventRsvpIcon = (ImageView) itemView.findViewById(R.id.event_rsvp_icon);
        eventRsvpText = (TextView) itemView.findViewById(R.id.event_rsvp_text);
        eventRsvpContainer = itemView.findViewById(R.id.event_rsvp_container);
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

