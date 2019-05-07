package com.bitlove.fetlife.legacy.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.common.app.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public abstract class MembersRecyclerAdapter extends ResourceListRecyclerAdapter<Member, MemberViewHolder> {

    protected List<Member> itemList = new ArrayList<>();

    public MembersRecyclerAdapter(FetLifeApplication fetLifeApplication) {
        super(fetLifeApplication);
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

    protected abstract void loadItems();

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Member getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friend, parent, false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder relationViewHolder, int position) {

        final Member member = itemList.get(position);

        relationViewHolder.headerText.setText(member.getNickname());
        relationViewHolder.upperText.setText(member.getMetaInfo());

//        relationViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(friend.getRoughtDate())));

        relationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onItemClick(member);
                }
            }
        });

        relationViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onAvatarClick(member);
                }
            }
        });

        relationViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = member.getAvatarLink();
        relationViewHolder.avatarImage.setImageURI(avatarUrl);
//        imageLoader.loadImage(relationViewHolder.itemView.getContext(), avatarUrl, relationViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

    @Override
    protected void onItemRemove(MemberViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {

    }
}

class MemberViewHolder extends SwipeableViewHolder {

    SimpleDraweeView avatarImage;
    TextView headerText, upperText, dateText;

    public MemberViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.friend_header);
        upperText = (TextView) itemView.findViewById(R.id.friend_upper);
        dateText = (TextView) itemView.findViewById(R.id.friend_right);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.friend_icon);
    }

    @Override
    public View getSwipeableLayout() {
        return null;
    }

    @Override
    public View getSwipeRightBackground() {
        return null;
    }

    @Override
    public View getSwipeLeftBackground() {
        return null;
    }
}


