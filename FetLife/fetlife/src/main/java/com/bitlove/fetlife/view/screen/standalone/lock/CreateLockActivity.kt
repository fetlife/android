package com.bitlove.fetlife.view.screen.standalone.lock

import android.app.Activity
import android.os.Bundle
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper
import com.bitlove.fetlife.R
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration



class CreateLockActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_fragment)
        PFFingerprintPinCodeHelper.getInstance().delete();

        val fragment = PFLockScreenFragment()
        val builder = PFFLockScreenConfiguration.Builder(this)
                .setMode(PFFLockScreenConfiguration.MODE_CREATE)
                .setUseFingerprint(false)
                .setLeftButton(getString(android.R.string.cancel))
        fragment.setConfiguration(builder.build())
        fragment.setCodeCreateListener {
            Prefe
            setResult(RESULT_OK,)
        }
    }

    companion object {
        fun startForResult(activity: Activity, requestCode: Int) {

        }
    }



}