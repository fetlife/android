package com.bitlove.fetlife.legacy.view.screen.resource;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.view.adapter.ResourceListRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceListActivity<Resource> extends ResourceActivity {

    private static final int PAGE_COUNT = 25;

    protected FloatingActionButton floatingActionButton;
    protected RecyclerView recyclerView;
    protected ResourceListRecyclerAdapter<Resource, ?> recyclerAdapter;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;

    protected int requestedItems = 0;
    protected int requestedPage = 1;

    @Override
    @CallSuper
    protected void onResourceCreate(Bundle savedInstanceState) {

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);
//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerAdapter = createRecyclerAdapter(savedInstanceState);
        recyclerAdapter.setOnItemClickListener(new ResourceListRecyclerAdapter.OnResourceClickListener<Resource>() {
            @Override
            public void onItemClick(Resource resource) {
                ResourceListActivity.this.onItemClick(resource);
            }

            @Override
            public void onAvatarClick(Resource resource) {
                ResourceListActivity.this.onAvatarClick(resource);
            }
        });
        recyclerView.setAdapter(recyclerAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestedItems = PAGE_COUNT;
                requestedPage = 1;
                String apiAction = getApiCallAction();
                if (apiAction != null) {
                    startResourceCall(PAGE_COUNT);
                }
            }
        });

        String apiCallAction = getApiCallAction();
        if (apiCallAction != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        int visibleItemCount = recyclerLayoutManager.getChildCount();
                        int pastVisibleItems = recyclerLayoutManager.findFirstVisibleItemPosition();
                        int lastVisiblePosition = visibleItemCount + pastVisibleItems;

                        if (lastVisiblePosition >= requestedItems) {
                            requestedItems += PAGE_COUNT;
                            startResourceCall(PAGE_COUNT, ++requestedPage);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Workaround for Android OS crash issue
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_resource_recycler_menu);
    }

    @Override
    @CallSuper
    protected void onResourceStart() {
        recyclerAdapter.refresh();

        String apiCallAction = getApiCallAction();
        if (apiCallAction != null) {
            showProgress();
            if (!FetLifeApiIntentService.isActionInProgress(apiCallAction)) {
                startResourceCall(PAGE_COUNT);
            }
            requestedPage = 1;
            requestedItems = PAGE_COUNT;
        }
    }

    protected static final int DEFAULT_REQUESTED_PAGE = Integer.MIN_VALUE;

    protected void startResourceCall(int pageCount) {
        startResourceCall(pageCount, DEFAULT_REQUESTED_PAGE);
    }

    protected void startResourceCall(int pageCount, int requestedPage) {
        String apiCallAction = getApiCallAction();
        if (apiCallAction == null) {
            return;
        }
        List<String> requestedParams = getApiParams();
        requestedParams.add(Integer.toString(pageCount));
        if (requestedPage != DEFAULT_REQUESTED_PAGE) {
            requestedParams.add(Integer.toString(requestedPage));
        }
        FetLifeApiIntentService.startApiCall(ResourceListActivity.this, apiCallAction, requestedParams.toArray(new String[requestedParams.size()]));
    }

    protected List<String> getApiParams() {
        return new ArrayList<>();
    }

    protected abstract String getApiCallAction();

    protected abstract ResourceListRecyclerAdapter createRecyclerAdapter(Bundle savedInstanceState);

    public abstract void onItemClick(Resource resource);

    public abstract void onAvatarClick(Resource resource);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(getApiCallAction())) {
            int countIncrease = serviceCallFinishedEvent.getItemCount();
            if (countIncrease != Integer.MIN_VALUE) {
                //One Item we already expected at the call
                requestedItems += countIncrease - PAGE_COUNT;
            }
            recyclerAdapter.refresh();
            dismissProgress();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction().equals(getApiCallAction())) {
            recyclerAdapter.refresh();
            dismissProgress();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction().equals(getApiCallAction())) {
            showProgress();
        }
    }
}
