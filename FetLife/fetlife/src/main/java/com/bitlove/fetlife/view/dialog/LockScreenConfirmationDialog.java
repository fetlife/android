package com.bitlove.fetlife.view.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.standalone.lock.CreateLockActivity;

import androidx.annotation.Nullable;

public class LockScreenConfirmationDialog extends ConfirmationDialog {

    private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";

    public static LockScreenConfirmationDialog newInstance(int requestCode) {
        LockScreenConfirmationDialog lockScreenConfirmationDialog = new LockScreenConfirmationDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        lockScreenConfirmationDialog.setArguments(args);
        return lockScreenConfirmationDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRightButton(getString(android.R.string.yes), new OnClickListener() {
            @Override
            public void onClick(ConfirmationDialog confirmationDialog) {
                CreateLockActivity.Companion.startForResult(getActivity(),getArguments().getInt(ARG_REQUEST_CODE));
            }
        });

        setLeftButton(getString(android.R.string.yes), new OnClickListener() {
            @Override
            public void onClick(ConfirmationDialog confirmationDialog) {
                confirmationDialog.dismiss();
            }
        });

    }
}
