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
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.adapter.StatusesRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.resource.LoadFragment;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;

public class StatusesFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Status> {

    public static StatusesFragment newInstance(String memberId) {
        StatusesFragment statusesFragment = new StatusesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        statusesFragment.setArguments(args);
        return statusesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        StatusesRecyclerAdapter adapter = new StatusesRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID));
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_STATUSES;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            StatusesRecyclerAdapter recyclerViewAdapter = (StatusesRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

    @Override
    public void onItemClick(Status status) {
        Member member = Member.loadMember(getArguments().getString(ARG_REFERENCE_ID));
        String nickname = member != null ? getString(R.string.title_activity_status,member.getNickname()) : "";
        FetLifeWebViewActivity.Companion.startActivity(getBaseActivity(),status.getUrl(),false,null, false, null, null);
//        TurboLinksViewActivity.startActivity(getBaseActivity(),status.getUrl(),nickname, false, null, null, false);
    }

    @Override
    public void onAvatarClick(Status status) {

    }
}
