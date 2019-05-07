package com.bitlove.fetlife.legacy.view.adapter;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group_Table;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;

public class SearchGroupRecyclerAdapter extends GroupsRecyclerAdapter {

    private String query;

    public SearchGroupRecyclerAdapter(String query) {
        super(null);
        this.query = query;
        loadItems();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            if (query == null || query.trim().length() == 0) {
                itemList = new ArrayList<>();
                return;
            }

            itemList = new Select().from(Group.class).where(Group_Table.description.like("%" + query + "%")).or(Group_Table.name.like("%" + query + "%")).orderBy(OrderBy.fromProperty(Group_Table.name).ascending().collate(Collate.UNICODE)).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }
}