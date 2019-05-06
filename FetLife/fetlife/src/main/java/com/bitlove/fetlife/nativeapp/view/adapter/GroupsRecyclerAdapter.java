package com.bitlove.fetlife.nativeapp.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.GroupMembershipReference;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.GroupMembershipReference_Table;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Group_Table;
import com.bitlove.fetlife.nativeapp.util.ServerIdUtil;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsRecyclerAdapter extends ResourceListRecyclerAdapter<Group, GroupsViewHolder> {

    private static final String DATE_INTERVAL_SEPARATOR = " - ";
    private static final String LOCATION_SEPARATOR = " - ";
    private static final int MAX_DESC_LENGTH = 125;

    private String memberId;
    protected List<Group> itemList;

    public GroupsRecyclerAdapter(String memberId) {
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
            List<GroupMembershipReference> groupMembershipReferences = new Select().from(GroupMembershipReference.class).where(GroupMembershipReference_Table.memberId.is(memberId)).orderBy(OrderBy.fromProperty(GroupMembershipReference_Table.lastVisitedAt).descending()).queryList();
            final Map<String,Integer> orderReference = new HashMap<>();
            int i = 0;
            for (GroupMembershipReference membershipReference : groupMembershipReferences) {
                orderReference.put(membershipReference.getGroupId(),i++);
            }
            List<String> groupIds = new ArrayList<>();
            for (GroupMembershipReference groupMembershipReference : groupMembershipReferences) {
                groupIds.add(groupMembershipReference.getGroupId());
            }
            itemList = new Select().from(Group.class).where(Group_Table.id.in(groupIds)).orderBy(OrderBy.fromProperty(Group_Table.name).ascending()).queryList();
            Collections.sort(itemList, new Comparator<Group>() {
                @Override
                public int compare(Group o1, Group o2) {
                    return orderReference.get(o1.getId()) - orderReference.get(o2.getId());
                }
            });
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_group, parent, false);
        return new GroupsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        final Group group = itemList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResourceClickListener.onItemClick(group);
            }
        });
        holder.groupName.setText(group.getName());
        String description = group.getHtmlDescription().toString();
//        description = StringUtil.parseMarkedHtml(description).toString();
        String descPreview = description.substring(0,Math.min(MAX_DESC_LENGTH,description.length())).trim();

        holder.groupDescription.setText(descPreview);
        holder.groupDescription.setVisibility(TextUtils.isEmpty(description) ? View.GONE : View.VISIBLE);
        String memberCount = holder.itemView.getContext().getString((group.getMemberCount() == 1 ? R.string.text_group_member_count : R.string.text_group_members_count),group.getMemberCount());
        holder.groupMemberCount.setText(memberCount);
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
    protected void onItemRemove(GroupsViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }

}

class GroupsViewHolder extends SwipeableViewHolder {

    TextView groupName, groupDescription, groupMemberCount;

    public GroupsViewHolder(View itemView) {
        super(itemView);
        groupName = (TextView) itemView.findViewById(R.id.group_name);
        groupDescription = (TextView) itemView.findViewById(R.id.group_description);
        groupMemberCount = (TextView) itemView.findViewById(R.id.group_member_count);
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