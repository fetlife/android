package com.bitlove.fetlife.legacy.view.dialog;

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
import com.bitlove.fetlife.legacy.view.screen.resource.ResourceListActivity;
import com.crashlytics.android.Crashlytics;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureUploadSelectionDialog extends DialogFragment {

    private static final int REQUEST_CODE_GALLERY_IMAGE = 2315;
    private static final int REQUEST_CODE_CAMERA_IMAGE = 3455;

    private static final String FRAGMENT_TAG = PictureUploadSelectionDialog.class.getSimpleName();

    private static final String STATE_PARCELABLE_PHOTOURI = "STATE_PARCELABLE_PHOTOURI";

    private Uri cameraPictureUri;

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
                onCameraUpload();
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
                onGalleryUpload();
            }
        });
        gallerySelectionView.setImageDrawable(galleryIcon);

        return view;
    }

//    private boolean cameraAppAvailable() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        return takePictureIntent.resolveActivity(getActivity().getPackageManager()) == null)
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void onGalleryUpload() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getResources().getString(R.string.title_intent_choose_media_upload)), REQUEST_CODE_GALLERY_IMAGE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_PARCELABLE_PHOTOURI, cameraPictureUri);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            cameraPictureUri = savedInstanceState.getParcelable(STATE_PARCELABLE_PHOTOURI);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY_IMAGE) {
                startActivityForResult(CropImage.activity(data.getData()).getIntent(getActivity()), CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            } else if (requestCode == REQUEST_CODE_CAMERA_IMAGE) {
                startActivityForResult(CropImage.activity(cameraPictureUri).getIntent(getActivity()), CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                dismissAllowingStateLoss();
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                PictureUploadConfirmationDialog.show(getActivity(), resultUri.toString(), false, true);
                cleanUpCameraPicture();
            }
        } else if (resultCode == Activity.RESULT_CANCELED){
            dismissAllowingStateLoss();
            cleanUpCameraPicture();
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
            cleanUpCameraPicture();
        }
    }

    private void cleanUpCameraPicture() {
        if (cameraPictureUri == null) {
            return;
        }
        try {
            getActivity().getContentResolver().delete(cameraPictureUri,null,null);
        } catch (Exception e) {
            Crashlytics.logException(new Exception("Camera Picture could not be removed"));
        }
        cameraPictureUri = null;
    }

    private void onCameraUpload() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //TODO: display toast message
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraPictureUri = FileProvider.getUriForFile(getActivity(),
                        "com.bitlove.fetlife.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUri);
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA_IMAGE);
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
        return new PictureUploadSelectionDialog();
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
