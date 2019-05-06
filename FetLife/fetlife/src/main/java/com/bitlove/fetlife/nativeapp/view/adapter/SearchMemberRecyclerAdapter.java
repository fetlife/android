package com.bitlove.fetlife.nativeapp.view.adapter;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member_Table;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchMemberRecyclerAdapter extends MembersRecyclerAdapter {

    private String query;

    public SearchMemberRecyclerAdapter(String query, FetLifeApplication fetLifeApplication) {
        super(fetLifeApplication);
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

            itemList = new Select().from(Member.class).where(Member_Table.nickname.like("%" + query + "%")).orderBy(OrderBy.fromProperty(Member_Table.nickname).ascending().collate(Collate.UNICODE)).queryList();
            final Collator coll = Collator.getInstance();
            coll.setStrength(Collator.IDENTICAL);
            Collections.sort(itemList, new Comparator<Member>() {
                @Override
                public int compare(Member member, Member member2) {
                    //Workaround to match with DB sorting
                    String nickname1 = member.getNickname().replaceAll("_","z");
                    String nickname2 = member2.getNickname().replaceAll("_","z");
                    return coll.compare(nickname1,nickname2);
                }
            });
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }
}