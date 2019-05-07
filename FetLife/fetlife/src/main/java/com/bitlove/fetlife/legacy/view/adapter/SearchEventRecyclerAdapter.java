package com.bitlove.fetlife.legacy.view.adapter;

import com.bitlove.fetlife.common.app.FetLifeApplication;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event_Table;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;

public class SearchEventRecyclerAdapter extends EventsRecyclerAdapter {

    private String query;

    public SearchEventRecyclerAdapter(String query, FetLifeApplication fetLifeApplication) {
        super(null);
        this.query = query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            if (query == null || query.trim().length() == 0) {
                itemList = new ArrayList<>();
                return;
            }

            itemList = new Select().from(Event.class).where(Event_Table.name.like("%" + query + "%")).or(Event_Table.tagline.like("%" + query + "%")).or(Event_Table.description.like("%" + query + "%")).or(Event_Table.address.like("%" + query + "%")).or(Event_Table.location.like("%" + query + "%")).or(Event_Table.dressCode.like("%" + query + "%")).orderBy(OrderBy.fromProperty(Event_Table.roughtStartDate).ascending().collate(Collate.UNICODE)).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }
}