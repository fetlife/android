package com.bitlove.fetlife.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;

public class MediaUploadConfirmationDialog extends DialogFragment {

    private static final String ARGUMENT_MEDIA_URI = "ARGUMENT_MEDIA_URI";
    private static final String ARGUMENT_DELETE_AFTER_UPLOAD = "ARGUMENT_DELETE_AFTER_UPLOAD";
    private static final String FRAGMENT_TAG = MediaUploadConfirmationDialog.class.getSimpleName();

    public static MediaUploadConfirmationDialog newInstance(String mediaUri, boolean deleteAfterUpload) {
        MediaUploadConfirmationDialog mediaUploadConfirmationDialog = new MediaUploadConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_MEDIA_URI, mediaUri);
        args.putBoolean(ARGUMENT_DELETE_AFTER_UPLOAD, deleteAfterUpload);
        mediaUploadConfirmationDialog.setArguments(args);
        return mediaUploadConfirmationDialog;
    }

    public static void show(Activity activity, String mediaUri, boolean deleteAfterUpload) {
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = newInstance(mediaUri, deleteAfterUpload);
        newFragment.show(ft, FRAGMENT_TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_alert_generic, container);

        TextView title = (TextView) view.findViewById(R.id.dialogTitle);
        title.setText(R.string.title_media_picture_upload_confirmation);

        TextView message = (TextView) view.findViewById(R.id.dialogMessage);
        message.setText(R.string.message_media_picture_upload_confirmation);

        final TextInputEditText editText = (TextInputEditText) view.findViewById(R.id.dialogEditText);

        TextInputLayout inputLayout = (TextInputLayout) view.findViewById(R.id.dialogInputLayout);
        inputLayout.setVisibility(View.VISIBLE);
        inputLayout.setHint(getString(R.string.button_media_picture_upload_hint_caption));

        final AppCompatCheckBox checkBox = (AppCompatCheckBox) view.findViewById(R.id.dialogCheckBox);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setText(R.string.button_media_picture_upload_check_friends_only);

        Button leftButton = (Button) view.findViewById(R.id.dialogNegativeButton);
        leftButton.setText(R.string.button_media_picture_upload_cancel);
        leftButton.setVisibility(View.VISIBLE);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        Button rightButton = (Button) view.findViewById(R.id.dialogPositiveButton);
        rightButton.setText(R.string.button_media_picture_upload_confirmation);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetLifeApiIntentService.startApiCall(getActivity(), FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE, getArguments().getString(ARGUMENT_MEDIA_URI), Boolean.toString(getArguments().getBoolean(ARGUMENT_DELETE_AFTER_UPLOAD)), editText.getText().toString(), Boolean.toString(checkBox.isChecked()));
                dismissAllowingStateLoss();
            }
        });
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
