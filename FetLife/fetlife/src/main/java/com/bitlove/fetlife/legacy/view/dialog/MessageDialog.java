package com.bitlove.fetlife.legacy.view.dialog;

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

import androidx.annotation.Nullable;

import com.bitlove.fetlife.R;

public class MessageDialog extends DialogFragment {

    private static final String FRAGMENT_TAG = MessageDialog.class.getSimpleName();
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";

    public static MessageDialog newInstance(String title, String message) {
        MessageDialog profileConfirmationDialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        profileConfirmationDialog.setArguments(args);
        return profileConfirmationDialog;
    }

    public static void show(Activity activity, String title, String message) {
        if (activity.isFinishing()) {
            return;
        }
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = newInstance(title, message);
        newFragment.show(ft, FRAGMENT_TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_alert_generic, container);

        TextView title = (TextView) view.findViewById(R.id.dialogTitle);
        title.setText(getArguments().getString(ARG_TITLE));

        TextView message = (TextView) view.findViewById(R.id.dialogMessage);
        message.setText(getArguments().getString(ARG_MESSAGE));

        Button rightButton = (Button) view.findViewById(R.id.dialogPositiveButton);
        rightButton.setText(android.R.string.ok);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public interface OnClickListener {
        void onClick(MessageDialog profileConfirmationDialog);
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, FRAGMENT_TAG);
    }
}
