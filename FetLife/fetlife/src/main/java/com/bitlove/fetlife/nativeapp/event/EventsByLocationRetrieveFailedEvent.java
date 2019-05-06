package com.bitlove.fetlife.nativeapp.event;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Event;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class EventsByLocationRetrieveFailedEvent {

    private final int page;
    private List<Event> events;
    private LatLngBounds searchBounds;

    public EventsByLocationRetrieveFailedEvent(LatLngBounds searchBounds, int page) {
        this.searchBounds = searchBounds;
        this.page = page;
    }

    public LatLngBounds getSearchBounds() {
        return searchBounds;
    }

    public int getPage() {
        return page;
    }
}
