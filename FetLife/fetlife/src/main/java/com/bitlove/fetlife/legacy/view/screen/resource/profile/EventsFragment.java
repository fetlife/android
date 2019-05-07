package com.bitlove.fetlife.legacy.view.screen.resource.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.view.adapter.EventsRecyclerAdapter;
import com.bitlove.fetlife.legacy.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.common.view.BaseActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.EventActivity;
import com.bitlove.fetlife.legacy.view.screen.resource.LoadFragment;

public class EventsFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Event> {

    public static EventsFragment newInstance(String memberId) {
        EventsFragment eventsFragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        eventsFragment.setArguments(args);
        return eventsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        EventsRecyclerAdapter adapter = new EventsRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID));
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_EVENTS;
    }

    @Override
    public void startResourceCall(int pageCount, int requestedPage) {
        super.startResourceCall(pageCount, requestedPage);
    }


    @Override
    public void onItemClick(Event event) {
        event.save();
        EventActivity.startActivity((BaseActivity) getActivity(),event.getId());
    }

    @Override
    public void onAvatarClick(Event event) {

    }

    public void refreshUi() {
        if (recyclerView != null) {
            EventsRecyclerAdapter recyclerViewAdapter = (EventsRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.setOnItemClickListener(this);
            recyclerViewAdapter.refresh();
        }
    }

}
