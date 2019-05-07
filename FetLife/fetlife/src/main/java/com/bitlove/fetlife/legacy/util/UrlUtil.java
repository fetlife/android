package com.bitlove.fetlife.legacy.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import com.bitlove.fetlife.common.app.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.inbound.customtabs.CustomTabLauncherActivity;

public class UrlUtil {

    private static final String QUERY_API_IDS = "api_ids";

    public static void openUrl(Context context, String link, boolean customTab, boolean finishAfterNavigation) {
        customTab = customTab && FetLifeApplication.getInstance().isCustomTabsSupported();
        if (link != null && !customTab) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            context.startActivity(intent);
        } else if (link != null) {
            if (finishAfterNavigation) {
                FetLifeApplication.getInstance().setCloseCustomTabAfterNavigation(true);
                CustomTabLauncherActivity.Companion.launchUrl(link,context);
            } else {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(FetLifeApplication.getInstance().getCustomTabsSession()).setToolbarColor(ColorUtil.retrieverColor(context,R.color.color_secondary_dark));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(link));
            }
        }
    }

}
