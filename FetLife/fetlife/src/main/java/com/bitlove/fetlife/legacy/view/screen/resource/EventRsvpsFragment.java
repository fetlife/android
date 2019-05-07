package com.bitlove.fetlife.legacy.view.screen.resource;

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
import com.bitlove.fetlife.legacy.view.adapter.EventRsvpsRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.common.view.BaseActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.profile.ProfileActivity;

public class EventRsvpsFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Member> {

    public static EventRsvpsFragment newInstance(String eventId, int rsvpType) {
        EventRsvpsFragment friendsFragment = new EventRsvpsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, eventId);
        args.putInt(ARG_REFERENCE_TYPE, rsvpType);
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
        EventRsvpsRecyclerAdapter adapter = new EventRsvpsRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID), getArguments().getInt(ARG_REFERENCE_TYPE),getFetLifeApplication());
        adapter.setOnItemClickListener(this);
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_EVENT_RSVPS;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            EventRsvpsRecyclerAdapter recyclerViewAdapter = (EventRsvpsRecyclerAdapter) recyclerView.getAdapter();
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
