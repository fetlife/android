package com.bitlove.fetlife.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.basecamp.turbolinks.TurbolinksSession;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.EventActivity;
import com.bitlove.fetlife.view.screen.resource.TurboLinksViewActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupActivity;
import com.bitlove.fetlife.view.screen.resource.groups.GroupMessagesActivity;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.crashlytics.android.Crashlytics;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import androidx.browser.customtabs.CustomTabsIntent;

public class UrlUtil {

    private static final String QUERY_API_IDS = "api_ids";

    public static void openUrl(Context context, String link, boolean customTab) {
        if (link != null && !customTab) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            context.startActivity(intent);
        } else if (link != null) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder().setToolbarColor(ColorUtil.retrieverColor(context,R.color.color_secondary_dark));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(link));
        }
    }

//    public static boolean isDownloadLink(BaseActivity baseActivity, Uri uri) {
//        List<String> urlSegments = uri.getPathSegments();
//        return (urlSegments.size() >= 2 && "wallpapers".equals(urlSegments.get(0)) && "download".equals(urlSegments.get(1)));
//    }

    public static boolean handleInternal(BaseActivity baseActivity, Uri uri, boolean sameBaseLocation, String baseLocation) {
        if (!uri.isHierarchical()) {
            return false;
        }
        List<String> baseUriSegments = baseLocation != null ? Uri.parse(baseLocation).getPathSegments() : new ArrayList<String>();
        List<String> urlSegments = uri.getPathSegments();
        if (urlSegments.size() == 0) {
            return false;
        }
        if ("q".equals(urlSegments.get(0))) {
            if (urlSegments.size() > 1) {
                if ("review".equalsIgnoreCase(urlSegments.get(1))) {
                    if (baseUriSegments.size() > 1 && "review".equalsIgnoreCase(baseUriSegments.get(1))) {
//                        TurbolinksSession.getDefault(baseActivity).getWebView().clearHistory();
//                        TurboLinksViewActivity.startActivity(baseActivity,uri.toString(), baseActivity.getString(R.string.title_activity_review_questions), false, null, null);
//                        baseActivity.finish();
                        return false;
                    } else {
                        TurboLinksViewActivity.startActivity(baseActivity, uri.toString(), baseActivity.getString(R.string.title_activity_review_questions), false, null, null, false);
                        return true;
                    }
                } else if ("search".equalsIgnoreCase(urlSegments.get(1))) {
                    TurbolinksSession.getDefault(baseActivity).getWebView().clearHistory();
                    return false;
                } else if ("tags".equalsIgnoreCase(urlSegments.get(1))) {
                    return false;
                } else {
                    TurboLinksViewActivity.startActivity(baseActivity,uri.toString(), null, false, null, null, false);
                    return true;
                }
            } else if (baseUriSegments.size() > 1 && "review".equalsIgnoreCase(baseUriSegments.get(1))) {
                baseActivity.finish();
                return true;
            } else if (baseUriSegments.size() != 1 || !"q".equalsIgnoreCase(baseUriSegments.get(0))) {
                TurboLinksViewActivity.startActivity(baseActivity,uri.toString(),baseActivity.getString(R.string.title_activity_questions), true, null,null,true);
                return true;
            }
        }

        if ("wallpapers".equals(urlSegments.get(0))) {
            if (urlSegments.size()>1 && "download".equals(urlSegments.get(1))) {
                UrlUtil.openUrl(baseActivity,uri.toString(),true);
                return true;
            }
        }

        if (sameBaseLocation) {
            return false;
        }

        String apiIdsParam = uri.getQueryParameter("api_ids");
        String[] apiIds = apiIdsParam != null ? apiIdsParam.split(",") : new String[0];
        if ("groups".equals(urlSegments.get(0))) {
            if (urlSegments.size()<3) {
                String groupId = apiIds.length >= 1 ? apiIds[0] : ServerIdUtil.prefixServerId(uri.getLastPathSegment());
                GroupActivity.startActivity(baseActivity,groupId,null,false);
                return true;
            } else if (urlSegments.size() == 4) {
                String groupId = apiIds.length >= 2 ? apiIds[0] : ServerIdUtil.prefixServerId(urlSegments.get(1));
                String groupDiscussionId = apiIds.length >= 2 ? apiIds[1] : ServerIdUtil.prefixServerId(urlSegments.get(3));
                GroupMessagesActivity.startActivity(baseActivity,groupId,groupDiscussionId,null, null,true);
                return true;
            }
        }
        if ("events".equals(urlSegments.get(0))) {
            if (urlSegments.size()<3) {
                String eventId = apiIds.length >= 1 ? apiIds[0] : ServerIdUtil.prefixServerId(uri.getLastPathSegment());
                EventActivity.startActivity(baseActivity,eventId);
                return true;
            }
        }
        if ("users".equals(urlSegments.get(0))) {
            if (urlSegments.size()<3) {
                String memberId = apiIds.length >= 1 ? apiIds[0] : ServerIdUtil.prefixServerId(uri.getLastPathSegment());
                ProfileActivity.startActivity(baseActivity,memberId);
                return true;
            }
            if (urlSegments.size()<4) {
                return false;
            }
            if ("pictures".equals(urlSegments.get(2))) {
                return false;
            }
            if ("videos".equals(urlSegments.get(2))) {
                return false;
            }
            if ("statuses".equals(urlSegments.get(2))) {
                return false;
            }
            if ("posts".equals(urlSegments.get(2))) {
//                String memberId = apiIds.length >= 2 ? apiIds[0] : ServerIdUtil.prefixServerId(urlSegments.get(1));
//                String postId = apiIds.length >= 2 ? apiIds[1] : ServerIdUtil.prefixServerId(urlSegments.get(3));
//                WritingActivity.startActivity(baseActivity,postId,memberId);
                return false;
            }
            return false;
        }
        return false;
    }

    public static String isMediaRequested(TurboLinksViewActivity turboLinksViewActivity, Uri uri) {
        List<String> urlSegments = uri.getPathSegments();
        if (!uri.isHierarchical()) return null;
        String apiIdsParam = uri.getQueryParameter(QUERY_API_IDS);
        String[] apiIds = apiIdsParam != null ? apiIdsParam.split(",") : new String[0];
        if (urlSegments.size()>3 && "users".equals(urlSegments.get(0))) {
            if ("pictures".equals(urlSegments.get(2))) {
                String memberId = apiIds.length >= 2 ? apiIds[0] : ServerIdUtil.prefixServerId(urlSegments.get(1));
                String pictureId = apiIds.length >= 2 ? apiIds[1] : ServerIdUtil.prefixServerId(urlSegments.get(3));
                FetLifeApiIntentService.startApiCall(turboLinksViewActivity, FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURE, memberId, pictureId);
                return pictureId;
            }
            if ("videos".equals(urlSegments.get(2))) {
                String memberId = apiIds.length >= 2 ? apiIds[0] : ServerIdUtil.prefixServerId(urlSegments.get(1));
                String videId = apiIds.length >= 2 ? apiIds[1] : ServerIdUtil.prefixServerId(urlSegments.get(3));
                FetLifeApiIntentService.startApiCall(turboLinksViewActivity, FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEO, memberId, videId);
                return videId;
            }
        }
        return null;
    }

    public static String removeAppIds(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            String[] queryParams = query != null ? query.split("&"): new String[0];
            String newQueryParams = "";
            for (String queryParam : queryParams) {
                if (!queryParam.startsWith(QUERY_API_IDS)) {
                    newQueryParams += queryParam + "&";
                }
            }
            if (newQueryParams.length() > 0) {
                newQueryParams = newQueryParams.substring(0,newQueryParams.length()-1);
            }
            return new URI(uri.getScheme(),uri.getAuthority(),uri.getPath(),newQueryParams,uri.getFragment()).toString();
        } catch (URISyntaxException e) {
            Crashlytics.logException(e);
            return url;
        }
    }
}
