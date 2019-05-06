package com.bitlove.fetlife.nativeapp.view.screen.resource;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.nativeapp.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.nativeapp.view.screen.BaseFragment;
import com.bitlove.fetlife.nativeapp.view.screen.resource.profile.ProfileActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class LoadFragment extends BaseFragment {

    public static final int PAGE_COUNT = 25;

    protected static final String ARG_REFERENCE_ID = "ARG_REFERENCE_ID";
    protected static final String ARG_REFERENCE_TYPE = "ARG_REFERENCE_TYPE";

    protected RecyclerView recyclerView;

    protected int requestedItems = 0;
    protected int requestedPage = 1;

    protected ProgressBar progressView;

    //TODO: replace this with a more sophisticated solution od checking queue of FetLife Intent service
    private boolean onCreateCallInProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
        onCreateCallInProgress = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!onCreateCallInProgress) {
            refresh();
        } else {
            onCreateCallInProgress = false;
        }
    }

    public void refresh() {
//        if (!isVisible()) {
//            return;
//        }
        requestedPage = 1;
        requestedItems = getPageCount();
        startResourceCall(getPageCount(),requestedPage);
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        refresh();
//    }

    @Override
    public void onPause() {
        super.onPause();
        //Workaround for Android OS crash issue
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                        int lastVisiblePosition = visibleItemCount + pastVisibleItems;

                        if (lastVisiblePosition >= requestedItems) {
                            requestedItems += getPageCount();
                            startResourceCall(getPageCount(), ++requestedPage);
                        }
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(getApiCallAction())) {
            int countIncrease = serviceCallFinishedEvent.getItemCount();
            if (countIncrease != Integer.MIN_VALUE) {
                //One Item we already expected at the call
                requestedItems += countIncrease - getPageCount();
            }
            refreshUi();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction().equals(getApiCallAction())) {
            refreshUi();
        }
    }

    private ProfileActivity getProfileActivity() {
        return (ProfileActivity) getActivity();
    }

    protected int getPageCount() {
        return PAGE_COUNT;
    }

    protected int getReferenceArg() {
        return getArguments().getInt(ARG_REFERENCE_TYPE,Integer.MIN_VALUE);
    }

    public abstract String getApiCallAction();

    public String getReferenceId() {
        return getArguments().getString(ARG_REFERENCE_ID);
    }

    public void startResourceCall(int pageCount, int requestedPage) {
        if (getApiCallAction() == null) {
            return;
        }
        int referenceArg = getReferenceArg();
        if (referenceArg != Integer.MIN_VALUE) {
            FetLifeApiIntentService.startApiCall(getActivity(),getApiCallAction(), getReferenceId(),Integer.toString(referenceArg),Integer.toString(pageCount),Integer.toString(requestedPage));
        } else {
            FetLifeApiIntentService.startApiCall(getActivity(),getApiCallAction(), getReferenceId(),Integer.toString(pageCount),Integer.toString(requestedPage));
        }
    }

    public abstract void refreshUi();

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem progressItem = menu.findItem(R.id.action_progress);
        if (progressItem != null) {
            progressView = (ProgressBar) progressItem.getActionView().findViewById(R.id.menu_progress_indicator);
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showProgress() {
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismissProgress() {
        if (progressView != null) {
            progressView.setVisibility(View.INVISIBLE);
        }
    }

}
