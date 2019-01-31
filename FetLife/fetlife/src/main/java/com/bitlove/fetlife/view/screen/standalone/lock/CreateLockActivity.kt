package com.bitlove.fetlife.view.screen.standalone.lock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.InputFilter
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper
import com.bitlove.fetlife.R
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration

class CreateLockActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_fragment)

        if (savedInstanceState == null) {

            PFFingerprintPinCodeHelper.getInstance().delete();

            val fragment = PFLockScreenFragment()
            val builder = PFFLockScreenConfiguration.Builder(this)
                    .setMode(PFFLockScreenConfiguration.MODE_CREATE)
                    .setUseFingerprint(false)
                    .setLeftButton(getString(android.R.string.cancel)) {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
            fragment.setConfiguration(builder.build())
            fragment.setCodeCreateListener {
                val mainPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                mainPrefs.edit().putString(PREF_LOCK_KEY,it).apply()
                Toast.makeText(this,R.string.message_toast_lock_created,Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit()
        }

    }

    companion object {
        const val PREF_LOCK_KEY = "PREF_LOCK_KEY"
        fun startForResult(activity: Activity, requestCode: Int) {
            val intent = Intent(activity,CreateLockActivity::class.java)
            activity.startActivityForResult(intent,requestCode)
        }
    }

}