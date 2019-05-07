package com.bitlove.fetlife.legacy.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.legacy.view.screen.resource.profile.EventsFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class EventsActivity extends ResourceActivity {

    static final String ARG_EVENT_ID = "ARG_EVENT_ID";
    static final String ARG_EVENT_LONGITUDE = "ARG_EVENT_LONGITUDE";
    static final String ARG_EVENT_LATITUDE = "ARG_EVENT_LATITUDE";

    private ViewPager viewPager;
    private WeakReference<Fragment> currentFragmentReference;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, EventsActivity.class);
        intent.putExtra(EXTRA_HAS_BOTTOM_NAVIGATION,true);
        return intent;
    }

    public static void startActivity(Context context, Event event) {
        context.startActivity(createIntent(context,event));
    }

    public static Intent createIntent(Context context, Event event) {
        Intent intent = new Intent(context, EventsActivity.class);
        intent.putExtra(EXTRA_HAS_BOTTOM_NAVIGATION,true);
        intent.putExtra(ARG_EVENT_ID,event.getId());
        intent.putExtra(ARG_EVENT_LONGITUDE,event.getLongitude());
        intent.putExtra(ARG_EVENT_LATITUDE,event.getLatitude());
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setVisibility(View.GONE);
        showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

                    @Override
                    public void setPrimaryItem(ViewGroup container, int position, Object object) {
                        currentFragmentReference = object != null ? new WeakReference<Fragment>((Fragment) object) : null;
                        super.setPrimaryItem(container, position, object);
                    }

                    @Override
                    public Fragment getItem(int position) {
                        switch (position) {
                            case 0:
                                return new EventMapFragment();
                            case 1:
                                Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
                                return EventsFragment.newInstance(currentUser.getId());
                            case 2:
                                return SearchEventFragment.newInstance(null);
                            default:
                                return null;
                        }
                    }

                    @Override
                    public int getCount() {
                        return 3;
                    }

                    @Override
                    public CharSequence getPageTitle(int position) {
                        switch (position) {
                            case 0:
                                return getString(R.string.title_fragment_event_map);
                            case 1:
                                return getString(R.string.title_fragment_event_myevents);
                            case 2:
                                return getString(R.string.title_fragment_event_search);
                            default:
                                return null;
                        }
                    }
                });
                viewPager.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.getServiceCallAction(), serviceCallStartedEvent.getParams())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (isRelatedCall(serviceCallFinishedEvent.getServiceCallAction(),serviceCallFinishedEvent.getParams()) && !isRelatedCall(FetLifeApiIntentService.getActionInProgress(),FetLifeApiIntentService.getInProgressActionParams())) {
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.getServiceCallAction(), serviceCallFailedEvent.getParams())) {
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction, String[] params) {
        return false;
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_events);
    }

    @Override
    protected void onResourceStart() {
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    //Map events


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {
        super.showProgress();
        Fragment page = getCurrentFragment();
        if (page != null && page instanceof LoadFragment) {
            ((LoadFragment)page).showProgress();
        }
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
        Fragment page = getCurrentFragment();
        if (page != null && page instanceof LoadFragment) {
            ((LoadFragment)page).dismissProgress();
        }
    }

    private Fragment getCurrentFragment() {
        if (currentFragmentReference == null) {
            return null;
        }
        return currentFragmentReference.get();
    }

}
