package com.bitlove.fetlife.view.activity.resource;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.component.MenuActivityComponent;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class ResourceListActivity<Resource> extends ResourceActivity implements MenuActivityComponent.MenuActivityCallBack {

    protected FloatingActionButton floatingActionButton;
    protected RecyclerView recyclerView;
    protected ResourceListRecyclerAdapter<Resource, ?> recyclerAdapter;

    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;

    protected int requestedPage = 1;
    protected int pageCount = 25;

    @Override
    @CallSuper
    protected void onResourceCreate(Bundle savedInstanceState) {

        //TODO: consider removing this
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);

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

        String apiCallAction = getApiCallAction();
        if (apiCallAction != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        int visibleItemCount = recyclerLayoutManager.getChildCount();
                        int pastVisiblesItems = recyclerLayoutManager.findFirstVisibleItemPosition();
                        int lastVisiblePosition = visibleItemCount + pastVisiblesItems;

                        if (lastVisiblePosition >= (requestedPage * pageCount)) {
                            startResourceCall(pageCount, ++requestedPage);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_resource_list);
    }

    @Override
    @CallSuper
    protected void onResourceStart() {
        recyclerAdapter.refresh();

        String apiCallAction = getApiCallAction();
        if (apiCallAction != null) {
            showProgress();
            if (!FetLifeApiIntentService.isActionInProgress(apiCallAction)) {
                startResourceCall(pageCount);
            }
            requestedPage = 1;
        }
    }

    private static final int DEFAULT_REQUESTED_PAGE = Integer.MIN_VALUE;

    protected void startResourceCall(int pageCount) {
        startResourceCall(pageCount, DEFAULT_REQUESTED_PAGE);
    }

    protected void startResourceCall(int pageCount, int requestedPage) {
        String apiCallAction = getApiCallAction();
        if (apiCallAction == null) {
            return;
        }
        if (requestedPage != DEFAULT_REQUESTED_PAGE) {
            FetLifeApiIntentService.startApiCall(ResourceListActivity.this, apiCallAction, Integer.toString(pageCount), Integer.toString(requestedPage));
        } else {
            FetLifeApiIntentService.startApiCall(ResourceListActivity.this, apiCallAction, Integer.toString(pageCount));
        }
    }

    protected abstract String getApiCallAction();

    protected abstract ResourceListRecyclerAdapter createRecyclerAdapter(Bundle savedInstanceState);

    public abstract void onItemClick(Resource resource);

    public abstract void onAvatarClick(Resource resource);

    @Override
    public boolean finishAtMenuNavigation() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == getApiCallAction()) {
            recyclerAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == getApiCallAction()) {
            recyclerAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == getApiCallAction()) {
            showProgress();
        }
    }


}
