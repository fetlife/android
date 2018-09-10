package com.bitlove.fetlife.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NotificationReceivedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.notification.AnonymNotification;
import com.bitlove.fetlife.notification.CommentNotification;
import com.bitlove.fetlife.notification.InfoNotification;
import com.bitlove.fetlife.notification.LoveNotification;
import com.bitlove.fetlife.notification.MentionNotification;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.bitlove.fetlife.view.adapter.NotificationHistoryRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NotificationHistoryActivity extends ResourceListActivity<NotificationHistoryItem>
        implements NavigationView.OnNavigationItemSelectedListener {

    public static void startActivity(Context context, boolean newTask) {
        context.startActivity(createIntent(context, newTask));
    }

    public static Intent createIntent(Context context, boolean newTask) {
        Intent intent = new Intent(context, NotificationHistoryActivity.class);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onResourceCreate(savedInstanceState);

        swipeRefreshLayout.setEnabled(false);

        showToast(getResources().getString(R.string.notificationhistory_activity_hint));
    }

    @Override
    protected void onResourceStart() {
        super.onResourceStart();
        CommentNotification.clearNotifications();
        InfoNotification.clearNotifications();
        LoveNotification.clearNotifications();
        MentionNotification.clearNotifications();
        AnonymNotification.clearNotifications();
    }

    @Override
    protected String getApiCallAction() {
        return null;
    }

    @Override
    protected ResourceListRecyclerAdapter createRecyclerAdapter(Bundle savedInstanceState) {
        return new NotificationHistoryRecyclerAdapter();
    }

    @Override
    public void onItemClick(NotificationHistoryItem notificationHistoryItem) {
        String launchUrl = notificationHistoryItem.getLaunchUrl();
        if (launchUrl != null && launchUrl.trim().length() != 0) {
            if (launchUrl.startsWith(OneSignalNotification.LAUNCH_URL_PREFIX)) {
                OneSignalNotification.handleInnerLaunchUrl(this,launchUrl);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(launchUrl));
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAvatarClick(NotificationHistoryItem notificationHistoryItem) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationReceived(NotificationReceivedEvent notificationReceivedEvent) {
        recyclerAdapter.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_notificationhistory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_clear_notification_history:
                //TODO: think of moving it to a db thread
                new Delete().from(NotificationHistoryItem.class).query();
                recyclerAdapter.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
