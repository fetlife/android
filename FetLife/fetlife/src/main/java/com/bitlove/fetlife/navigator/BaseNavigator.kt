package com.bitlove.fetlife.navigator

import android.view.MenuItem
import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.service.FetLifeApiIntentService
import com.bitlove.fetlife.view.screen.BaseActivity
import com.bitlove.fetlife.view.screen.resource.ExploreActivity
import com.bitlove.fetlife.view.screen.resource.NotificationHistoryActivity
import com.bitlove.fetlife.view.screen.resource.groups.GroupsActivity
import com.bitlove.fetlife.view.screen.resource.members.MembersActivity
import com.bitlove.fetlife.view.screen.standalone.LoginActivity
import com.bitlove.fetlife.view.screen.standalone.ReleaseNotesActivity
import com.bitlove.fetlife.view.screen.standalone.SettingsActivity
import com.bitlove.fetlife.webapp.screen.FetLifeWebViewActivity

open class BaseNavigator (val baseActivity: BaseActivity){

    fun onOpenUrl(location: String) {

    }

    fun onMenuItem(menuItem: MenuItem) : Boolean {
        when(menuItem?.itemId) {
            R.id.nav_logout -> {
                baseActivity.fetLifeApplication.userSessionManager.deleteCurrentUserDb()
                baseActivity.fetLifeApplication.userSessionManager.onUserLogOut()
                baseActivity.finish()
                LoginActivity.startLogin(baseActivity.fetLifeApplication)
            }
//            R.id.nav_conversations -> ConversationsActivity.startActivity(baseActivity, false)
            R.id.nav_members -> MembersActivity.startActivity(baseActivity, false)
            R.id.nav_about -> FetLifeWebViewActivity.startActivity(baseActivity,"android",true,null, false, null)
//            R.id.nav_friendrequests -> TurboLinksViewActivity.startActivity(baseActivity,"requests",baseActivity.getString(R.string.title_activity_friendrequests))
            R.id.nav_relnotes -> ReleaseNotesActivity.startActivity(baseActivity)
//            R.id.nav_notifications -> TurboLinksViewActivity.startActivity(baseActivity,"notifications",baseActivity.getString(R.string.title_activity_notifications))
            R.id.nav_app_notifications -> NotificationHistoryActivity.startActivity(baseActivity,false)
            R.id.nav_upload_pic -> {
//                if (isStoragePermissionGranted()) {
//                    PictureUploadSelectionDialog.show(menuActivity);
//                } else {
//                    requestStoragePermission(BaseActivity.PERMISSION_REQUEST_PICTURE_UPLOAD);
//                }
            }
            R.id.nav_upload_video -> {
//                if (isStoragePermissionGranted()) {
//                    VideoUploadSelectionDialog.show(menuActivity);
//                } else {
//                    requestStoragePermission(BaseActivity.PERMISSION_REQUEST_VIDEO_UPLOAD);
//                }
            }
            R.id.nav_settings -> SettingsActivity.startActivity(baseActivity)
//            R.id.nav_feed -> FeedActivity.startActivity(baseActivity)
            R.id.nav_stuff_you_love -> ExploreActivity.startActivity(baseActivity, ExploreActivity.Explore.STUFF_YOU_LOVE)
            R.id.nav_fresh_and_pervy -> ExploreActivity.startActivity(baseActivity, ExploreActivity.Explore.FRESH_AND_PERVY)
            R.id.nav_kinky_and_popular -> ExploreActivity.startActivity(baseActivity, ExploreActivity.Explore.KINKY_AND_POPULAR)
            R.id.nav_support -> FetLifeWebViewActivity.startActivity(baseActivity,"support",true,null, false, null)
            R.id.nav_ads -> FetLifeWebViewActivity.startActivity(baseActivity,"ads",true,null, false, null)
            R.id.nav_glossary -> FetLifeWebViewActivity.startActivity(baseActivity,"glossary",true,null, false, null)
            R.id.nav_team -> FetLifeWebViewActivity.startActivity(baseActivity,"team",true,null, false, null)
            R.id.nav_events -> {
//                if (isLocationPermissionGranted()) {
//                    EventsActivity.startActivity(menuActivity);
//                } else {
//                    requestLocationPermission(BaseActivity.PERMISSION_REQUEST_LOCATION);
//                }
            }
            R.id.nav_groups -> GroupsActivity.startActivity(baseActivity,false)
            R.id.nav_updates -> {
                baseActivity.showToast(baseActivity.getString(R.string.message_toast_checking_for_updates));
                FetLifeApiIntentService.startApiCall(baseActivity,FetLifeApiIntentService.ACTION_EXTERNAL_CALL_CHECK_4_UPDATES,true.toString());
            }
        }
        return false
    }

}
