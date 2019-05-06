package com.bitlove.fetlife.nativeapp.view.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.view.screen.resource.ResourceListActivity;
import com.crashlytics.android.Crashlytics;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoUploadSelectionDialog extends DialogFragment {

    private static final int REQUEST_CODE_GALLERY_VIDEO = 2415;
    private static final int REQUEST_CODE_CAMERA_VIDEO = 3555;

    private static final String FRAGMENT_TAG = VideoUploadSelectionDialog.class.getSimpleName();

    private static final String STATE_PARCELABLE_VIDEOURI = "STATE_PARCELABLE_VIDEOURI";

    private Uri cameraVideoUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Drawable cameraIcon = getResources().getDrawable(android.R.drawable.ic_menu_camera);
//        cameraIcon.setColorFilter(getResources().getColor(R.color.text_color_primary), PorterDuff.Mode.MULTIPLY);
        View view = inflater.inflate(R.layout.dialogfragment_mediaupload_selection, container);
        ImageView cameraSelectionView = (ImageView) view.findViewById(R.id.mediaUploadCameraSelectionView);
        cameraSelectionView.setImageDrawable(cameraIcon);
        cameraSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoUpload();
            }
        });

//        if (!cameraAppAvailable()) {
//            cameraSelectionView.setVisibility(View.GONE);
//        }

        Drawable galleryIcon = getResources().getDrawable(android.R.drawable.ic_menu_gallery);
        //galleryIcon.setColorFilter(getResources().getColor(R.color.text_color_primary), PorterDuff.Mode.MULTIPLY);
        ImageView gallerySelectionView = (ImageView) view.findViewById(R.id.mediaUploadGallerySelectionView);
        gallerySelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoGalleryUpload();
            }
        });
        gallerySelectionView.setImageDrawable(galleryIcon);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

//    private boolean cameraAppAvailable() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        return takePictureIntent.resolveActivity(getActivity().getPackageManager()) == null)
//    }

    private void onVideoGalleryUpload() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getResources().getString(R.string.title_intent_choose_media_upload)), REQUEST_CODE_GALLERY_VIDEO);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_PARCELABLE_VIDEOURI, cameraVideoUri);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            cameraVideoUri = savedInstanceState.getParcelable(STATE_PARCELABLE_VIDEOURI);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY_VIDEO) {
                dismissAllowingStateLoss();
                VideoUploadConfirmationDialog.show(getActivity(), data.getData().toString(), true, false);
            } else if (requestCode == REQUEST_CODE_CAMERA_VIDEO) {
                dismissAllowingStateLoss();
                VideoUploadConfirmationDialog.show(getActivity(), cameraVideoUri.toString(), true, true);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                dismissAllowingStateLoss();
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                VideoUploadConfirmationDialog.show(getActivity(), resultUri.toString(), false, true);
                cleanUpCameraVideo();
            }
        } else if (resultCode == Activity.RESULT_CANCELED){
            dismissAllowingStateLoss();
            cleanUpCameraVideo();
        } else {
            Exception exception;
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                exception = result.getError();
            } else {
                exception = new Exception("Image selection failed");
            }
            dismissAllowingStateLoss();
            Crashlytics.logException(exception);
            cleanUpCameraVideo();
        }
    }

    private void cleanUpCameraVideo() {
        if (cameraVideoUri == null) {
            return;
        }
        try {
            getActivity().getContentResolver().delete(cameraVideoUri,null,null);
        } catch (Exception e) {
            Crashlytics.logException(new Exception("Camera Picture could not be removed"));
        }
        cameraVideoUri = null;
    }

    private void onVideoUpload() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = createImageFile();
            } catch (IOException ex) {
                //TODO: display toast message
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                cameraVideoUri = FileProvider.getUriForFile(getActivity(),
                        "com.bitlove.fetlife.fileprovider",
                        videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraVideoUri);
                startActivityForResult(takeVideoIntent, REQUEST_CODE_CAMERA_VIDEO);
            }
        } else {
            //TODO: display toast message
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static void show(Activity activity) {
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
        DialogFragment newFragment = newInstance();
        newFragment.show(ft, FRAGMENT_TAG);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        ResourceListActivity activity = getResourceActivity();
        if (getResourceActivity() != null) {
            activity.onWaitingForResult();
        }
        super.startActivityForResult(intent, requestCode);
    }

    private static DialogFragment newInstance() {
        return new VideoUploadSelectionDialog();
    }

    private ResourceListActivity getResourceActivity() {
        Activity activity = getActivity();
        if (activity instanceof ResourceListActivity) {
            return (ResourceListActivity) activity;
        } else {
            return null;
        }
    }
}
