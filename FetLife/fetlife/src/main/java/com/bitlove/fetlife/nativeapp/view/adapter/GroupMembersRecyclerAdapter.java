package com.bitlove.fetlife.nativeapp.view.adapter;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.GroupMembershipReference;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.GroupMembershipReference_Table;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member_Table;
import com.bitlove.fetlife.nativeapp.util.ServerIdUtil;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMembersRecyclerAdapter extends MembersRecyclerAdapter {

    private String groupId;

    public GroupMembersRecyclerAdapter(String groupId, FetLifeApplication fetLifeApplication) {
        super(fetLifeApplication);
        this.groupId = groupId;
        loadItems();
    }

    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            if (ServerIdUtil.isServerId(groupId)) {
                if (ServerIdUtil.containsServerId(groupId)) {
                    groupId = ServerIdUtil.getLocalId(groupId);
                } else {
                    return;
                }
            }
            List<GroupMembershipReference> relationReferences = new Select().from(GroupMembershipReference.class).where(GroupMembershipReference_Table.groupId.is(groupId)).orderBy(OrderBy.fromProperty(GroupMembershipReference_Table.createdAt).descending().collate(Collate.UNICODE)).queryList();
            final Map<String,Integer> orderReference = new HashMap<>();
            int i = 0;
            List<String> relationIds = new ArrayList<>();
            for (GroupMembershipReference relationReference : relationReferences) {
                orderReference.put(relationReference.getMemberId(),i++);
                relationIds.add(relationReference.getMemberId());
            }
            itemList = new Select().from(Member.class).where(Member_Table.id.in(relationIds)).queryList();
            Collections.sort(itemList, new Comparator<Member>() {
                @Override
                public int compare(Member o1, Member o2) {
                    return orderReference.get(o1.getId()) - orderReference.get(o2.getId());
                }
            });

        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }
}