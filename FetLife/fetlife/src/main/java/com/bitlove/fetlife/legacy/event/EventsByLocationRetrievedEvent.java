package com.bitlove.fetlife.legacy.event;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class EventsByLocationRetrievedEvent {

    private final int page;
    private List<Event> events;
    private LatLngBounds searchBounds;

    public EventsByLocationRetrievedEvent(LatLngBounds searchBounds, int page, List<Event> events) {
        this.events = events;
        this.searchBounds = searchBounds;
        this.page = page;
    }

    public List<Event> getEvents() {
        return events;
    }

    public LatLngBounds getSearchBounds() {
        return searchBounds;
    }

    public int getPage() {
        return page;
    }
}
