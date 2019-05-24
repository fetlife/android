package com.bitlove.fetlife.github.logic

import android.view.View
import com.bitlove.fetlife.github.dto.Release
import com.bitlove.fetlife.inbound.onesignal.update.UpdatePermissionActivity

class ReleaseHandler {

    fun onDownloadVersion(view: View, release: Release) {
        UpdatePermissionActivity.startActivity(view.context, release.releaseUrl)
    }

}