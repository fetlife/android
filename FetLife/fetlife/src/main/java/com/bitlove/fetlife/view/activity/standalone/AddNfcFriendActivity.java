package com.bitlove.fetlife.view.activity.standalone;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AddNfcFriendActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AddNfcFriendActivity.class);
        return intent;
    }

    NfcAdapter mNfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nfc_friend);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(this, getString(R.string.message_android_version_not_sufficient), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, getString(R.string.message_nfc_not_available), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.message_nfc_turned_off), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);

        Toast.makeText(this, getString(R.string.nfc_tap_your_device), Toast.LENGTH_LONG).show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        try {
            NdefMessage msg = new NdefMessage(
                    new NdefRecord[] { NdefRecord.createMime(
                            "application/vnd.com.bitlove.fetlife", getFetLifeApplication().getUserSessionManager().getCurrentUser().toJsonString().getBytes())
                    });
            return msg;
        } catch (JsonProcessingException e) {
            //Should not happen, force a crash to get a report if it did.
            throw new RuntimeException(e);
        }
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplication();
    }

}
