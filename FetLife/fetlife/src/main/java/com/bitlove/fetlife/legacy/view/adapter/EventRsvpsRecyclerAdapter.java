package com.bitlove.fetlife.legacy.view.adapter;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.EventRsvpReference;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.EventRsvpReference_Table;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member_Table;
import com.bitlove.fetlife.legacy.util.ServerIdUtil;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventRsvpsRecyclerAdapter extends MembersRecyclerAdapter {

    private String eventId;
    private final int rsvpType;

    public EventRsvpsRecyclerAdapter(String eventId, int rsvpType, FetLifeApplication fetLifeApplication) {
        super(fetLifeApplication);
        this.eventId = eventId;
        this.rsvpType = rsvpType;
        loadItems();
    }

    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            if (ServerIdUtil.isServerId(eventId)) {
                if (ServerIdUtil.containsServerId(eventId)) {
                    eventId = ServerIdUtil.getLocalId(eventId);
                } else {
                    return;
                }
            }
            List<EventRsvpReference> eventRsvpEventReferences = new Select().from(EventRsvpReference.class).where(EventRsvpReference_Table.eventId.is(eventId)).and(EventRsvpReference_Table.rsvpType.is(rsvpType)).orderBy(OrderBy.fromProperty(EventRsvpReference_Table.nickname).ascending().collate(Collate.UNICODE)).queryList();
            List<String> memberIds = new ArrayList<>();
            for (EventRsvpReference eventRsvpReference : eventRsvpEventReferences) {
                memberIds.add(eventRsvpReference.getId());
            }
            itemList = new Select().from(Member.class).where(Member_Table.id.in(memberIds)).orderBy(OrderBy.fromProperty(Member_Table.nickname).ascending().collate(Collate.UNICODE)).queryList();
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