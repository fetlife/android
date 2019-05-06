package com.bitlove.fetlife.nativeapp.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.service.FetLifeApiIntentService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PictureUploadConfirmationDialog extends DialogFragment {

    private static final String ARGUMENT_MEDIA_URI = "ARGUMENT_MEDIA_URI";
    private static final String ARGUMENT_DELETE_AFTER_UPLOAD = "ARGUMENT_DELETE_AFTER_UPLOAD";
    private static final String ARGUMENT_IS_VIDEO = "ARGUMENT_IS_VIDEO";
    private static final String FRAGMENT_TAG = PictureUploadConfirmationDialog.class.getSimpleName();

    public static PictureUploadConfirmationDialog newInstance(String mediaUri, boolean isVideo, boolean deleteAfterUpload) {
        PictureUploadConfirmationDialog pictureUploadConfirmationDialog = new PictureUploadConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_MEDIA_URI, mediaUri);
        args.putBoolean(ARGUMENT_IS_VIDEO, isVideo);
        args.putBoolean(ARGUMENT_DELETE_AFTER_UPLOAD, deleteAfterUpload);
        pictureUploadConfirmationDialog.setArguments(args);
        return pictureUploadConfirmationDialog;
    }

    public static void show(Activity activity, String mediaUri, boolean isVideo, boolean deleteAfterUpload) {
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
        DialogFragment newFragment = newInstance(mediaUri, isVideo, deleteAfterUpload);
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
        checkBox.setText(R.string.button_media_upload_check_friends_only);

        Button leftButton = (Button) view.findViewById(R.id.dialogNegativeButton);
        leftButton.setText(R.string.button_media_upload_cancel);
        leftButton.setVisibility(View.VISIBLE);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        Button rightButton = (Button) view.findViewById(R.id.dialogPositiveButton);
        rightButton.setText(R.string.button_media_upload_confirmation);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments().getBoolean(ARGUMENT_IS_VIDEO, false)) {
                    FetLifeApiIntentService.startApiCall(getActivity(), FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO, getArguments().getString(ARGUMENT_MEDIA_URI), Boolean.toString(getArguments().getBoolean(ARGUMENT_DELETE_AFTER_UPLOAD)), editText.getText().toString(), Boolean.toString(checkBox.isChecked()));
                } else {
                    FetLifeApiIntentService.startApiCall(getActivity(), FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE, getArguments().getString(ARGUMENT_MEDIA_URI), Boolean.toString(getArguments().getBoolean(ARGUMENT_DELETE_AFTER_UPLOAD)), editText.getText().toString(), Boolean.toString(checkBox.isChecked()));
                }
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
