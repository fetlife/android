package com.bitlove.fetlife.util;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;

public class PictureUtil {

    public static interface OnPictureOverlayClickListener {
        void onMemberClick(Member member);
        void onSharePicture(Picture picture, String url);
        void onVisitPicture(Picture picture, String url);
    }

    private static final int OVERLAY_HITREC_PADDING = 200;

    public static void setOverlayContent(View overlay, final Picture picture, final OnPictureOverlayClickListener onItemClickListener) {

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
                onItemClickListener.onVisitPicture(picture, picture.getUrl());
            }
        });

        ImageView imageShare = overlay.findViewById(R.id.overlayImageShare);
        imageShare.setColorFilter(picture.isOnShareList() ? overlay.getContext().getResources().getColor(R.color.text_color_primary) : overlay.getContext().getResources().getColor(R.color.text_color_secondary));
        ViewUtil.increaseTouchArea(imageShare, OVERLAY_HITREC_PADDING);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onSharePicture(picture, picture.getUrl());
                ((ImageView) v).setColorFilter(picture.isOnShareList() ? v.getContext().getResources().getColor(R.color.text_color_primary) : v.getContext().getResources().getColor(R.color.text_color_secondary));
            }
        });


        imageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onMemberClick(picture.getMember());
            }
        });
        imageDescription.setText(Picture.getFormattedBody(picture.getBody()));
        imageMeta.setText(picture.getMember().getMetaInfo());
        imageName.setText(picture.getMember().getNickname());
    }
}

