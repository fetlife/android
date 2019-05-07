package com.bitlove.fetlife.legacy.view.screen.resource.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.view.adapter.GroupsRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.screen.BaseActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.LoadFragment;
import com.bitlove.fetlife.legacy.view.screen.resource.groups.GroupActivity;

public class GroupsFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Group> {

    public static GroupsFragment newInstance(String memberId) {
        GroupsFragment groupsFragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        groupsFragment.setArguments(args);
        return groupsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        GroupsRecyclerAdapter adapter = new GroupsRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID));
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_GROUPS;
    }

    @Override
    public void startResourceCall(int pageCount, int requestedPage) {
        super.startResourceCall(pageCount, requestedPage);
    }


    @Override
    public void onItemClick(Group group) {
        group.save();
        GroupActivity.startActivity((BaseActivity) getActivity(),group.getId(),group.getName(),false);
    }

    @Override
    public void onAvatarClick(Group group) {

    }

    public void refreshUi() {
        if (recyclerView != null) {
            GroupsRecyclerAdapter recyclerViewAdapter = (GroupsRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.setOnItemClickListener(this);
            recyclerViewAdapter.refresh();
        }
    }

}
