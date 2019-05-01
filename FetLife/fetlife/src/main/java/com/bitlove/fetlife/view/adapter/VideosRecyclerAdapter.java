package com.bitlove.fetlife.view.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.VideoReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.VideoReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Video_Table;
import com.bitlove.fetlife.util.ServerIdUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class VideosRecyclerAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private String memberId;
    private List<Video> itemList;

    public VideosRecyclerAdapter(String memberId) {
        this.memberId = memberId;
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
            List<VideoReference> videoReferences = new Select().from(VideoReference.class).where(VideoReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(VideoReference_Table.date).descending()).queryList();
            List<String> videoIds = new ArrayList<>();
            for (VideoReference videoReference : videoReferences) {
                videoIds.add(videoReference.getId());
            }
            itemList = new Select().from(Video.class).where(Video_Table.id.in(videoIds)).orderBy(OrderBy.fromProperty(Video_Table.date).descending()).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_video, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position) {
        final Video video = itemList.get(position);
        holder.imageView.setImageURI(video.getThumbUrl());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoUrl = video.getVideoUrl();
                if (videoUrl == null || videoUrl.endsWith("null")) {
                    return;
                }
                Uri uri = Uri.parse(videoUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "video/*");
                holder.imageView.getContext().startActivity(intent);
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UrlUtil.openUrl(view.getContext(),video.getUrl(), true, false);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }
}

class VideoViewHolder extends RecyclerView.ViewHolder {

    SimpleDraweeView imageView;

    public VideoViewHolder(View itemView) {
        super(itemView);
        imageView = (SimpleDraweeView) itemView.findViewById(R.id.profile_video);
    }
}

