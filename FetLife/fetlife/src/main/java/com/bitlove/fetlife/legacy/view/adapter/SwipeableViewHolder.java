package com.bitlove.fetlife.legacy.view.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeableViewHolder extends RecyclerView.ViewHolder {

    public SwipeableViewHolder(View itemView) {
        super(itemView);
    }

    public abstract View getSwipeableLayout();

    public abstract View getSwipeRightBackground();

    public abstract View getSwipeLeftBackground();
}
