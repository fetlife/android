package com.bitlove.fetlife.nativeapp.view.screen.resource;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.adapter.SearchEventRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity;

public class SearchEventFragment extends LoadFragment implements ResourceListRecyclerAdapter.OnResourceClickListener<Event> {

    private static final String ARG_SEARCH_QUERY = "ARG_SEARCH_QUERY";

    private String lastQueryString = "";

    public static SearchEventFragment newInstance(String searchQuery) {
        SearchEventFragment friendsFragment = new SearchEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, searchQuery != null ? searchQuery : "");
        friendsFragment.setArguments(args);
        return friendsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            lastQueryString = savedInstanceState.getString(lastQueryString);
        } else {
            lastQueryString = getArguments().getString(ARG_SEARCH_QUERY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_SEARCH_QUERY,lastQueryString);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.post(new Runnable() {
            @Override
            public void run() {
                searchView.setQuery(lastQueryString,false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                lastQueryString = query;
                refreshUi();
                refresh();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        SearchEventRecyclerAdapter adapter = new SearchEventRecyclerAdapter(getArguments().getString(ARG_SEARCH_QUERY),getFetLifeApplication());
        adapter.setOnItemClickListener(this);
        adapter.setUseSwipe(false);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_TAG;
    }

    @Override
    public void startResourceCall(int pageCount, int requestedPage) {
        if (lastQueryString != null && lastQueryString.trim().length() > 0) {
            FetLifeApiIntentService.startApiCall(getContext(),FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_TAG, lastQueryString,Integer.toString(pageCount),Integer.toString(requestedPage));
        }
    }

    @Override
    public void refreshUi() {
        if (recyclerView != null) {
            SearchEventRecyclerAdapter recyclerViewAdapter = (SearchEventRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.setQuery(lastQueryString);
            recyclerViewAdapter.refresh();
        }
    }

    @Override
    public void onItemClick(Event event) {
        openEventScreen(event);
    }

    @Override
    public void onAvatarClick(Event event) {
        openEventScreen(event);
    }

    private void openEventScreen(Event event) {
        EventActivity.startActivity((BaseActivity) getActivity(),event.getId());
    }
}
