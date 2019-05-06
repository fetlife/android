package com.bitlove.fetlife.nativeapp.view.screen.resource.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.adapter.WritingsRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.resource.LoadFragment;
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity;

public class WritingsFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Writing> {

    public static WritingsFragment newInstance(String memberId) {
        WritingsFragment eventsFragment = new WritingsFragment();
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
        WritingsRecyclerAdapter adapter = new WritingsRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID));
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_WRITINGS;
    }

    @Override
    public void startResourceCall(int pageCount, int requestedPage) {
        super.startResourceCall(pageCount, requestedPage);
    }


    @Override
    public void onItemClick(Writing writing) {
        writing.save();
//        WritingActivity.startActivity((BaseActivity) getActivity(),writing.getId(), writing.getMemberId());
        FetLifeWebViewActivity.Companion.startActivity(getBaseActivity(),writing.getUrl(),false,null, false, null, null);
//        TurboLinksViewActivity.startActivity(getBaseActivity(),writing.getUrl(),writing.getTitle(), false, null, null, false);
    }

    @Override
    public void onAvatarClick(Writing writing) {

    }

    public void refreshUi() {
        if (recyclerView != null) {
            WritingsRecyclerAdapter recyclerViewAdapter = (WritingsRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.setOnItemClickListener(this);
            recyclerViewAdapter.refresh();
        }
    }

}
