package com.bitlove.fetlife.nativeapp.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.event.NotificationReceivedEvent;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.nativeapp.view.adapter.NotificationHistoryRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.nativeapp.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.nativeapp.view.screen.resource.groups.GroupMessagesActivity;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.navigation.NavigationView;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.bitlove.fetlife.nativeapp.inbound.onesignal.notification.OneSignalNotification.LAUNCH_URL_PARAM_SEPARATOR;
import static com.bitlove.fetlife.nativeapp.inbound.onesignal.notification.OneSignalNotification.LAUNCH_URL_PREFIX;

public class NotificationHistoryActivity extends ResourceListActivity<NotificationHistoryItem>
        implements NavigationView.OnNavigationItemSelectedListener {

    public static void startActivity(Context context, boolean newTask) {
        context.startActivity(createIntent(context, newTask));
    }

    public static Intent createIntent(Context context, boolean newTask) {
        Intent intent = new Intent(context, NotificationHistoryActivity.class);
        intent.putExtra(EXTRA_HAS_BOTTOM_NAVIGATION,true);
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
            if (launchUrl.startsWith(LAUNCH_URL_PREFIX)) {
                if (launchUrl.indexOf("com.bitlove.fetlife.notification.GroupMessageNotification") >=0) {
                    //Temp for backward compatibility
                    String[] params = launchUrl.substring(LAUNCH_URL_PREFIX.length()).split(LAUNCH_URL_PARAM_SEPARATOR);
                    GroupMessagesActivity.startActivity(this, params[1], params[2], params[3], null, true);
                    return;
                } else {
                    launchUrl = launchUrl.substring(LAUNCH_URL_PREFIX.length());
                }
            }
            //temporary fix
            if (!launchUrl.startsWith("http")) {
                int urlStart = launchUrl.indexOf("http");
                if (urlStart >= 0) {
                    launchUrl = launchUrl.substring(urlStart);
                }
            }
//            if (launchUrl.startsWith(OneSignalNotification.LAUNCH_URL_PREFIX)) {
//                handleInnerLaunchUrl(this,launchUrl);
//            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(launchUrl));
                try {
                    startActivity(intent);
                } catch (Throwable t) {
                    Crashlytics.logException(t);
                }
//            }
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

//    private void handleInnerLaunchUrl(Context context,String launchUrl) {
//        try {
//            String baseString = launchUrl.substring(OneSignalNotification.LAUNCH_URL_PREFIX.length());
//            String className = baseString.substring(0,baseString.indexOf(OneSignalNotification.LAUNCH_URL_PARAM_SEPARATOR));
//            Method method = Class.forName(className).getMethod("handleInnerLaunchUrl", Context.class, String.class);
//            method.invoke(null,context,launchUrl);
//        } catch (Throwable t) {
//            Crashlytics.logException(t);
//        }
//    }
}
