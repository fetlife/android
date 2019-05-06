package com.bitlove.fetlife.nativeapp.view.screen.resource.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.adapter.GroupDiscussionsRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity;
import com.bitlove.fetlife.nativeapp.view.screen.resource.LoadFragment;
import com.bitlove.fetlife.nativeapp.view.screen.resource.profile.ProfileActivity;

public class GroupDiscussionsFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<GroupPost> {

    public static GroupDiscussionsFragment newInstance(String groupId) {
        GroupDiscussionsFragment groupMembersFragment = new GroupDiscussionsFragment();
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
        GroupDiscussionsRecyclerAdapter adapter = new GroupDiscussionsRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID), getFetLifeApplication());
        adapter.setOnItemClickListener(this);
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_GROUP_DISCUSSIONS;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            GroupDiscussionsRecyclerAdapter recyclerViewAdapter = (GroupDiscussionsRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

    @Override
    public void onItemClick(GroupPost groupPost) {
        String groupId = getArguments().getString(ARG_REFERENCE_ID);
        GroupMessagesActivity.startActivity(getContext(),groupId,groupPost.getId(),groupPost.getTitle(),null,false);
    }

    @Override
    public void onAvatarClick(GroupPost groupPost) {
        Member member = groupPost.getMember();
        if (member != null) {
            member.mergeSave();
        }
        ProfileActivity.startActivity((BaseActivity) getActivity(),groupPost.getMemberId());
    }

}
