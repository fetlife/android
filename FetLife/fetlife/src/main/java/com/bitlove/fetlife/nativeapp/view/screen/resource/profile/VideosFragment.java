package com.bitlove.fetlife.nativeapp.view.screen.resource.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.adapter.VideosRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.resource.LoadFragment;

public class VideosFragment extends LoadFragment {

    private static final int VIDEO_GRID_COLUMN_COUNT = 3;
    public static int PAGE_COUNT = 24;

    public static VideosFragment newInstance(String memberId) {
        VideosFragment videosFragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        videosFragment.setArguments(args);
        return videosFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getFetLifeApplication(), VIDEO_GRID_COLUMN_COUNT);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        VideosRecyclerAdapter adapter = new VideosRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEOS;
    }

    @Override
    protected int getPageCount() {
        return PAGE_COUNT;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            VideosRecyclerAdapter recyclerViewAdapter = (VideosRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }

}
