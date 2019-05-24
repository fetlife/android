package com.bitlove.fetlife.common.logic.databinding;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//TODO: investigate why it does not work as kotlin file/class
public class BindingAdapters {

    @BindingAdapter({"items", "itemHandler", "layout", "itemBindingId", "handlerBindingId"})
    public static <T extends BindableRecyclerAdapter.Diffable, H> void setItems(RecyclerView recyclerView, List<T> items, H itemHandler, int layoutId, int itemBindingId, int handlerBindingId) {
        if (items == null) return;
        BindableRecyclerAdapter bindableRecyclerAdapter = new BindableRecyclerAdapter(layoutId, itemHandler, itemBindingId, handlerBindingId);
        bindableRecyclerAdapter.setItems(items);
        recyclerView.setAdapter(bindableRecyclerAdapter);
    }
}
