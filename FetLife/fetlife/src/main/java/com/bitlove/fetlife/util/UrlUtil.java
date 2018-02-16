package com.bitlove.fetlife.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.EventActivity;
import com.bitlove.fetlife.view.screen.resource.WritingActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupActivity;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;

import java.util.List;

public class UrlUtil {

    public static void openUrl(Context context,String link) {
        if (link != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            context.startActivity(intent);
        }
    }

    public static boolean handleInternal(BaseActivity baseActivity, Uri uri) {
        List<String> urlSegments = uri.getPathSegments();
        if ("groups".equals(urlSegments.get(0))) {
            if (urlSegments.size()<3) {
                GroupActivity.startActivity(baseActivity,ServerIdUtil.prefixServerId(uri.getLastPathSegment()),null,false);
                return true;
            }
        }
        if ("events".equals(urlSegments.get(0))) {
            if (urlSegments.size()<3) {
                EventActivity.startActivity(baseActivity,ServerIdUtil.prefixServerId(uri.getLastPathSegment()));
                return true;
            }
        }
        if ("users".equals(urlSegments.get(0))) {
            if (urlSegments.size()<3) {
                ProfileActivity.startActivity(baseActivity,ServerIdUtil.prefixServerId(uri.getLastPathSegment()));
                return true;
            }
            if (urlSegments.size()<4) {
                return false;
            }
            if ("pictures".equals(urlSegments.get(2))) {
                return true;
            }
            if ("videos".equals(urlSegments.get(2))) {
                return true;
            }
            if ("posts".equals(urlSegments.get(2))) {
                WritingActivity.startActivity(baseActivity,ServerIdUtil.prefixServerId(urlSegments.get(3)),ServerIdUtil.prefixServerId(urlSegments.get(1)));
                return true;
            }
            return false;
        }
        return false;
    }
}
