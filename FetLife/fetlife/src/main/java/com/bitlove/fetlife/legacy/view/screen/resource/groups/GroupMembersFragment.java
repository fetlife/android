package com.bitlove.fetlife.legacy.view.screen.resource.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.view.adapter.GroupMembersRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.screen.BaseActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.LoadFragment;
import com.bitlove.fetlife.legacy.view.screen.resource.profile.ProfileActivity;

public class GroupMembersFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Member> {

    public static GroupMembersFragment newInstance(String groupId) {
        GroupMembersFragment groupMembersFragment = new GroupMembersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, groupId);
        groupMembersFragment.setArguments(args);
        return groupMembersFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        GroupMembersRecyclerAdapter adapter = new GroupMembersRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID), getFetLifeApplication());
        adapter.setOnItemClickListener(this);
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_GROUP_MEMBERS;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            GroupMembersRecyclerAdapter recyclerViewAdapter = (GroupMembersRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

    @Override
    public void onItemClick(Member member) {
        openProfileScreen(member);
    }

    @Override
    public void onAvatarClick(Member member) {
        openProfileScreen(member);
    }

    private void openProfileScreen(Member member) {
        member.mergeSave();
        ProfileActivity.startActivity((BaseActivity) getActivity(),member.getId());
    }
}
