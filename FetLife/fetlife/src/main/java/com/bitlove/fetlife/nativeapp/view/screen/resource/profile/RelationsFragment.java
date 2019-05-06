package com.bitlove.fetlife.nativeapp.view.screen.resource.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.adapter.RelationsRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity;
import com.bitlove.fetlife.nativeapp.view.screen.resource.LoadFragment;

public class RelationsFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Member> {

    public static RelationsFragment newInstance(String memberId, int relationType) {
        RelationsFragment friendsFragment = new RelationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        args.putInt(ARG_REFERENCE_TYPE, relationType);
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        RelationsRecyclerAdapter adapter = new RelationsRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID), getArguments().getInt(ARG_REFERENCE_TYPE),getFetLifeApplication());
        adapter.setOnItemClickListener(this);
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            RelationsRecyclerAdapter recyclerViewAdapter = (RelationsRecyclerAdapter) recyclerView.getAdapter();
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
        ProfileActivity.startActivity((BaseActivity) getActivity(),member.getId());
    }
}
