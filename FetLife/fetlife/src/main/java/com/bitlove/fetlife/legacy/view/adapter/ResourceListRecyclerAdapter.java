package com.bitlove.fetlife.legacy.view.adapter;

import com.bitlove.fetlife.common.app.FetLifeApplication;

public abstract class ResourceListRecyclerAdapter<Resource, ResourceViewHolder extends SwipeableViewHolder> extends SwipeableRecyclerAdapter<ResourceViewHolder> {

    protected FetLifeApplication fetLifeApplication;

    public ResourceListRecyclerAdapter() {

    }

    public ResourceListRecyclerAdapter(FetLifeApplication fetLifeApplication) {
        this.fetLifeApplication = fetLifeApplication;
    }

    public abstract void refresh();

    public interface OnResourceClickListener<Resource> {
        void onItemClick(Resource resource);
        void onAvatarClick(Resource resource);
    }

    protected OnResourceClickListener<Resource> onResourceClickListener;

    public void setOnItemClickListener(OnResourceClickListener<Resource> onResourceClickListener) {
        this.onResourceClickListener = onResourceClickListener;
    }

    public OnResourceClickListener<Resource> getOnItemClickListener() {
        return onResourceClickListener;
    }
}
