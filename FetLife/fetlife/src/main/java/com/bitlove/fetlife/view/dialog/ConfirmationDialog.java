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

import androidx.annotation.Nullable;

public class ConfirmationDialog extends DialogFragment {

    private static final String FRAGMENT_TAG = ConfirmationDialog.class.getSimpleName();
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";
    private static final String ARG_ADD_CANCEL_BUTTON = "ARG_ADD_CANCEL_BUTTON";

    private String leftButtonText = "";
    private String rightButtonText = "";
    private OnClickListener leftButtonListener = null;
    private OnClickListener rightButtonListener = null;

    public static ConfirmationDialog newInstance(String title, String message) {
        return newInstance(title, message, false);
    }

    public static ConfirmationDialog newInstance(String title, String message, boolean addCancelButton) {
        ConfirmationDialog profileConfirmationDialog = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_ADD_CANCEL_BUTTON, addCancelButton);
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
        android.app.DialogFragment newFragment = newInstance(title, message);
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
        if (rightButtonListener != null) {
            rightButton.setText(rightButtonText);
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rightButtonListener.onClick(ConfirmationDialog.this);
                }
            });
        } else {
            rightButton.setText(android.R.string.ok);
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                }
            });
        }

        if (leftButtonListener != null) {
            Button leftButton = (Button) view.findViewById(R.id.dialogNegativeButton);
            leftButton.setText(leftButtonText);
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leftButtonListener.onClick(ConfirmationDialog.this);
                }
            });
        } else if (getArguments().getBoolean(ARG_ADD_CANCEL_BUTTON,false)) {
            Button leftButton = (Button) view.findViewById(R.id.dialogNegativeButton);
            leftButton.setText(getActivity().getString(android.R.string.cancel));
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissAllowingStateLoss();
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setLeftButton(String text, final OnClickListener clickListener) {
        leftButtonText = text;
        leftButtonListener = clickListener;
    }

    public void setRightButton(String text, final OnClickListener clickListener) {
        rightButtonText = text;
        rightButtonListener = clickListener;
    }

    public interface OnClickListener {
        void onClick(ConfirmationDialog profileConfirmationDialog);
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, FRAGMENT_TAG);
    }
}
