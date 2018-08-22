package com.bitlove.fetlife.view.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public abstract class SwipeableViewHolder extends RecyclerView.ViewHolder {

    public SwipeableViewHolder(View itemView) {
        super(itemView);
    }

    public abstract View getSwipeableLayout();

    public abstract View getSwipeRightBackground();

    public abstract View getSwipeLeftBackground();
}
