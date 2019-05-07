package com.bitlove.fetlife.legacy.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.common.view.ResourceActivity;
import com.bitlove.fetlife.legacy.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.legacy.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.db.EventRsvpReference;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Rsvp;
import com.bitlove.fetlife.legacy.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.legacy.util.DateUtil;
import com.bitlove.fetlife.legacy.util.UrlUtil;
import com.bitlove.fetlife.legacy.view.widget.FlingBehavior;
import com.google.android.material.appbar.AppBarLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EventActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PROFILE_MENU_HITREC_PADDING = 200;

    private static final String EXTRA_EVENTID = "EXTRA_EVENTID";
    private String eventId;
    private TextView eventSubTitle;
    private TextView eventTitle;

    private ViewPager viewPager;
    private Event event;

    public static void startActivity(Context context, String eventId) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_EVENTID, eventId);
        context.startActivity(intent);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        eventId = getIntent().getStringExtra(EXTRA_EVENTID);
        event = Event.loadEvent(eventId);

        if (event != null ) {
            setEventDetails(event);
        } else {
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_EVENT,eventId);
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return EventInfoFragment.newInstance(eventId);
                    case 1:
                        return EventRsvpsFragment.newInstance(eventId, EventRsvpReference.VALUE_RSVPTYPE_GOING);
                    case 2:
                        return EventRsvpsFragment.newInstance(eventId, EventRsvpReference.VALUE_RSVPTYPE_MAYBE);
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
                        return getString(R.string.title_fragment_event_details);
                    case 1:
                        return getString(R.string.title_fragment_event_rsvp_going);
                    case 2:
                        return getString(R.string.title_fragment_event_rsvp_maybe);
                    default:
                        return null;
                }
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        params.setBehavior(new FlingBehavior());
        appBarLayout.addOnOffsetChangedListener(this);

        findViewById(R.id.event_menu_icon_going_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoingToEvent(v);
            }
        });
        findViewById(R.id.event_menu_icon_maybe_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMaybeToEvent(v);
            }
        });
        findViewById(R.id.event_menu_icon_show_on_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowEventOnMap(v);
            }
        });
        findViewById(R.id.event_menu_icon_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddEventToCalendar(v);
            }
        });
        findViewById(R.id.event_menu_icon_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewEvent(v);
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        getSupportActionBar().setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        TextView headerTitle = (TextView) findViewById(R.id.event_title);
        toolbarTitle.setText(title);
        headerTitle.setText(title);
    }

    private void setEventDetails(Event event) {
        setTitle(event.getName());
        eventSubTitle = (TextView) findViewById(R.id.event_subtitle);
        eventSubTitle.setText(event.getTagline());
        View showOnMapAction = findViewById(R.id.event_menu_icon_show_on_map);
        showOnMapAction.setVisibility((event.getLatitude() != Event.NOT_SET && event.getLongitude() != Event.NOT_SET) ? View.VISIBLE : View.GONE);
        View goingActionContainer = findViewById(R.id.event_menu_icon_going_container);
        View maybeActionContainer = findViewById(R.id.event_menu_icon_maybe_container);
        ImageView goingActionIndicator = (ImageView) findViewById(R.id.event_menu_icon_going);
        ImageView maybeActionIndicator = (ImageView) findViewById(R.id.event_menu_icon_maybe);
        if (event.getRsvpStatus() == null) {
            goingActionContainer.setVisibility(View.INVISIBLE);
            maybeActionContainer.setVisibility(View.INVISIBLE);
        } else {
            goingActionContainer.setVisibility(View.VISIBLE);
            maybeActionContainer.setVisibility(View.VISIBLE);
            if (event.getRsvpStatus() == Rsvp.RsvpStatus.YES) {
                goingActionIndicator.setColorFilter(getResources().getColor(R.color.text_color_primary));
                maybeActionIndicator.setColorFilter(getResources().getColor(R.color.text_color_secondary));
            } else if (event.getRsvpStatus() == Rsvp.RsvpStatus.MAYBE){
                goingActionIndicator.setColorFilter(getResources().getColor(R.color.text_color_secondary));
                maybeActionIndicator.setColorFilter(getResources().getColor(R.color.text_color_primary));
            } else {
                goingActionIndicator.setColorFilter(getResources().getColor(R.color.text_color_secondary));
                maybeActionIndicator.setColorFilter(getResources().getColor(R.color.text_color_secondary));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.getServiceCallAction(), serviceCallStartedEvent.getParams())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (isRelatedCall(serviceCallFinishedEvent.getServiceCallAction(), serviceCallFinishedEvent.getParams())) {
            event = Event.loadEvent(eventId);
            setEventDetails(event);
            if (!isRelatedCall(FetLifeApiIntentService.getActionInProgress(), FetLifeApiIntentService.getInProgressActionParams())) {
                dismissProgress();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.getServiceCallAction(), serviceCallFailedEvent.getParams())) {
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction, String[] params) {
        if (params != null && params.length > 0 && eventId != null && !eventId.equals(params[0])) {
            return false;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_EVENT.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_EVENTS.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_LOCATION.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_SEARCH_EVENT_BY_TAG.equals(serviceCallAction)) {
            return true;
        }
        return false;
    }

    public void onShowEventOnMap(View v) {
        if (event != null) {
            EventsActivity.startActivity(this, event);
        }
    }

    public void onGoingToEvent(View v) {
        if (event == null) {
            return;
        }
        if (event.getRsvpStatus() != Rsvp.RsvpStatus.YES) {
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_SET_RSVP_STATUS,eventId, Rsvp.RsvpStatus.YES.toString());
            event.setRsvpStatus(Rsvp.RsvpStatus.YES);
            event.save();
            setEventDetails(event);
        } else {
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_SET_RSVP_STATUS,eventId, Rsvp.RsvpStatus.NO.toString());
            event.setRsvpStatus(Rsvp.RsvpStatus.NO);
            event.save();
            setEventDetails(event);
        }
    }

    public void onMaybeToEvent(View v) {
        if (event == null) {
            return;
        }
        if (event.getRsvpStatus() != Rsvp.RsvpStatus.MAYBE) {
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_SET_RSVP_STATUS,eventId, Rsvp.RsvpStatus.MAYBE.toString());
            event.setRsvpStatus(Rsvp.RsvpStatus.MAYBE);
            event.save();
            setEventDetails(event);
        } else {
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_SET_RSVP_STATUS,eventId, Rsvp.RsvpStatus.NO.toString());
            event.setRsvpStatus(Rsvp.RsvpStatus.NO);
            event.save();
            setEventDetails(event);
        }
    }

    public void onAddEventToCalendar(View v) {
        if (event == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateUtil.parseDate(event.getStartDateTime(),true));
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, DateUtil.parseDate(event.getEndDateTime(),true));
        String eventName = event.getName();
        if (!TextUtils.isEmpty(event.getTagline())) {
            eventName += "\n" + event.getTagline();
        }
        intent.putExtra(CalendarContract.Events.TITLE, eventName);
        String eventDescription = event.getDescription();
        if (!TextUtils.isEmpty(event.getDressCode())) {
            eventDescription += "\n\n" + getString(R.string.text_event_header_dresscodes) + " " + event.getDressCode();
        }
        if (!TextUtils.isEmpty(event.getCost())) {
            eventDescription += "\n\n" + getString(R.string.text_event_header_cost) + " " + event.getCost();
        }
        intent.putExtra(CalendarContract.Events.DESCRIPTION,eventDescription);
        String eventLocation = "";
        if (!TextUtils.isEmpty(event.getLocation())) {
            eventLocation = event.getLocation();
        }
        if (!TextUtils.isEmpty(eventLocation) && !TextUtils.isEmpty(event.getAddress())) {
            eventLocation += ", " + event.getAddress();
        }
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        startActivity(intent);
    }

    public void onViewEvent(View v) {
        if (event != null) {
            UrlUtil.openUrl(this, event.getUrl(), true, false);
        }
    }

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
    protected void onResourceStart() {
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_event);
    }

    private static final float PERCENTAGE_TO_SHOW_TITLE_DETAILS = 0.7f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final long ALPHA_ANIMATIONS_DELAY = 200l;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        setToolbarVisibility(appBarLayout, findViewById(R.id.toolbar_title), percentage);
    }

    private boolean isTitleVisible = false;

    private void setToolbarVisibility(AppBarLayout appBarLayout, View title, float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_DETAILS) {
            if (!isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                isTitleVisible = true;
            }
        } else {
            if (isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.INVISIBLE);
                isTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation(final View v, long duration, long delay, final int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(delay);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
