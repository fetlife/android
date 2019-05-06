package com.bitlove.fetlife.nativeapp.util;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.nativeapp.view.screen.BaseActivity;
import com.bitlove.fetlife.nativeapp.view.screen.resource.profile.ProfileActivity;

public class PictureUtil {

    public static interface OnPictureOverlayClickListener {
        void onMemberClick(Member member);
        void onSharePicture(Picture picture, String url);
        void onVisitPicture(Picture picture, String url);
    }

    private static final int OVERLAY_HITREC_PADDING = 200;

    public static void setOverlayContent(View overlay, final Picture picture) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                picture.setLastViewedAt(System.currentTimeMillis());
                picture.save();
            }
        });

        TextView imageDescription = (TextView) overlay.findViewById(R.id.overlayDescription);
        TextView imageMeta = (TextView) overlay.findViewById(R.id.overlayImageMeta);
        TextView imageName = (TextView) overlay.findViewById(R.id.overlayImageName);

        final ImageView imageLove = (ImageView) overlay.findViewById(R.id.overlayImageLove);
        ViewUtil.increaseTouchArea(imageLove, OVERLAY_HITREC_PADDING);

        boolean isLoved = picture.isLovedByMe();
        imageLove.setImageResource(isLoved ? R.drawable.ic_loved : R.drawable.ic_love);
        imageLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageLove = (ImageView) v;
                boolean isLoved = picture.isLovedByMe();
                boolean newIsLoved = !isLoved;
                imageLove.setImageResource(newIsLoved ? R.drawable.ic_loved : R.drawable.ic_love);
                Picture.startLoveCallWithObserver(FetLifeApplication.getInstance(), picture, newIsLoved);
                picture.setLovedByMe(newIsLoved);
                picture.save();
            }
        });

        View imageVisit = overlay.findViewById(R.id.overlayImageVisit);
        ViewUtil.increaseTouchArea(imageVisit, OVERLAY_HITREC_PADDING);
        imageVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlUtil.openUrl(v.getContext(),picture.getUrl(), true, false);
            }
        });

        ImageView imageShare = overlay.findViewById(R.id.overlayImageShare);
        imageShare.setColorFilter(picture.isOnShareList() ? overlay.getContext().getResources().getColor(R.color.text_color_primary) : overlay.getContext().getResources().getColor(R.color.text_color_secondary));
        ViewUtil.increaseTouchArea(imageShare, OVERLAY_HITREC_PADDING);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picture.isOnShareList()) {
                    Picture.unsharePicture(picture);
                } else {
                    Picture.sharePicture(picture);
                }
                ((ImageView) v).setColorFilter(picture.isOnShareList() ? v.getContext().getResources().getColor(R.color.text_color_primary) : v.getContext().getResources().getColor(R.color.text_color_secondary));
            }
        });


        imageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.startActivity((BaseActivity) v.getContext(), picture.getMemberId());
            }
        });
        imageDescription.setText(Picture.getFormattedBody(picture.getBody()));
        imageMeta.setText(picture.getMember().getMetaInfo());
        imageName.setText(picture.getMember().getNickname());
    }
}

