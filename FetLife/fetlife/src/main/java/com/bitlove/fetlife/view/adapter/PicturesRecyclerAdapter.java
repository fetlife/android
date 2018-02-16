package com.bitlove.fetlife.view.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.PictureReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.PictureReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture_Table;
import com.bitlove.fetlife.util.ServerIdUtil;
import com.bitlove.fetlife.util.ViewUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

public class PicturesRecyclerAdapter extends RecyclerView.Adapter<PictureViewHolder> {

    private static final int OVERLAY_HITREC_PADDING = 200;

    private final FetLifeApplication fetLifeApplication;

    private String memberId;
    private List<Picture> itemList;
    private ArrayList<String> displayLinks;
    private OnPictureClickListener onPictureClickListener;

    public PicturesRecyclerAdapter(String memberId, OnPictureClickListener onPictureClickListener, FetLifeApplication fetLifeApplication) {
        this.memberId = memberId;
        this.onPictureClickListener = onPictureClickListener;
        this.fetLifeApplication = fetLifeApplication;
        loadItems();
    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //TODO: think of possibility of update only specific items instead of the whole list
                loadItems();
                notifyDataSetChanged();
            }
        });
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            if (ServerIdUtil.isServerId(memberId)) {
                if (ServerIdUtil.containsServerId(memberId)) {
                    memberId = ServerIdUtil.getLocalId(memberId);
                } else {
                    return;
                }
            }
            List<PictureReference> pictureReferences = new Select().from(PictureReference.class).where(PictureReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(PictureReference_Table.date).descending()).queryList();
            List<String> pictureIds = new ArrayList<>();
            for (PictureReference pictureReference : pictureReferences) {
                pictureIds.add(pictureReference.getId());
            }
            itemList = new Select().from(Picture.class).where(Picture_Table.id.in(pictureIds)).orderBy(OrderBy.fromProperty(Picture_Table.date).descending()).queryList();
            displayLinks = new ArrayList<>();
            for (Picture picture : itemList) {
                displayLinks.add(picture.getDisplayUrl());
            }
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_picture, parent, false);
        return new PictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, final int position) {
        Picture picture = itemList.get(position);
        holder.imageView.setImageURI(picture.getThumbUrl());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();

                LayoutInflater inflater = LayoutInflater.from(context);
                final View overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null);
                setOverlayContent(overlay, itemList.get(position), onPictureClickListener);

                new ImageViewer.Builder(context, displayLinks).setStartPosition(position).setOverlayView(overlay).setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                    @Override
                    public void onImageChange(int position) {
                        try {
                            setOverlayContent(overlay, itemList.get(position), onPictureClickListener);
                        } catch (IndexOutOfBoundsException ioobe) {
                            //Rare issue when user is browsing photos while Picture list is updated.
                            //TODO: return user to picture list screen in this case
                        }
                    }
                }).show();
            }
        });

    }

    private void setOverlayContent(View overlay, final Picture picture, final OnPictureClickListener onItemClickListener) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                picture.setLastViewedAt(System.currentTimeMillis());
                picture.save();
            }
        });

        TextView imageDescription = (TextView) overlay.findViewById(R.id.feedImageOverlayDescription);
        TextView imageMeta = (TextView) overlay.findViewById(R.id.feedImageOverlayMeta);
        TextView imageName = (TextView) overlay.findViewById(R.id.feedImageOverlayName);

        final ImageView imageLove = (ImageView) overlay.findViewById(R.id.feedImageLove);
        ViewUtil.increaseTouchArea(imageLove,OVERLAY_HITREC_PADDING);

        boolean isLoved = picture.isLovedByMe();
        imageLove.setImageResource(isLoved ? R.drawable.ic_loved : R.drawable.ic_love);
        imageLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageLove = (ImageView) v;
                boolean isLoved = picture.isLovedByMe();
                boolean newIsLoved = !isLoved;
                imageLove.setImageResource(newIsLoved ? R.drawable.ic_loved : R.drawable.ic_love);
                Picture.startLoveCallWithObserver(fetLifeApplication, picture, newIsLoved);
                picture.setLovedByMe(newIsLoved);
                picture.save();
            }
        });

        View imageVisit = overlay.findViewById(R.id.feedImageVisit);
        ViewUtil.increaseTouchArea(imageVisit,OVERLAY_HITREC_PADDING);
        imageVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onVisitItem(picture, picture.getUrl());
            }
        });

        ImageView imageShare = overlay.findViewById(R.id.feedImageShare);
        imageShare.setColorFilter(picture.isOnShareList() ? overlay.getContext().getResources().getColor(R.color.text_color_primary) : overlay.getContext().getResources().getColor(R.color.text_color_secondary));
        ViewUtil.increaseTouchArea(imageShare,OVERLAY_HITREC_PADDING);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onShareItem(picture, picture.getUrl());
                ((ImageView)v).setColorFilter(picture.isOnShareList() ? v.getContext().getResources().getColor(R.color.text_color_primary) : v.getContext().getResources().getColor(R.color.text_color_secondary));
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


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnPictureClickListener {
        void onShareItem(Picture picture, String url);
        void onVisitItem(Picture picture, String url);
        void onMemberClick(Member member);
    }

}

class PictureViewHolder extends RecyclerView.ViewHolder {

    SimpleDraweeView imageView;

    public PictureViewHolder(View itemView) {
        super(itemView);
        imageView = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
    }
}
