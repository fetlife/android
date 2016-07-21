package com.bitlove.fetlife.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.FriendRequestSendFailedEvent;
import com.bitlove.fetlife.event.FriendRequestSendSucceededEvent;
import com.bitlove.fetlife.event.FriendSuggestionAddedEvent;
import com.bitlove.fetlife.event.NotificationReceivedEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.pojos.FriendSuggestion;
import com.bitlove.fetlife.model.pojos.NotificationHistoryItem;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NotificationHistoryActivity extends ResourceListActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NotificationHistoryRecyclerAdapter notificationHistoryAdapter;

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, NotificationHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        floatingActionButton.setVisibility(View.GONE);

        notificationHistoryAdapter = new NotificationHistoryRecyclerAdapter();
        notificationHistoryAdapter.setOnNotificationHistoryItemClickListener(new NotificationHistoryRecyclerAdapter.OnNotificationHistoryItemClickListener() {
            @Override
            public void onItemClick(NotificationHistoryItem notificationHistoryItem) {
                String launchUrl = notificationHistoryItem.getLaunchUrl();
                if (launchUrl != null && launchUrl.trim().length() != 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(launchUrl));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(notificationHistoryAdapter);

        showToast(getResources().getString(R.string.notificationhistory_activity_hint));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFetLifeApplication().getEventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getFetLifeApplication().getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationReceived(NotificationReceivedEvent notificationReceivedEvent) {
        notificationHistoryAdapter.refresh();
    }

}
