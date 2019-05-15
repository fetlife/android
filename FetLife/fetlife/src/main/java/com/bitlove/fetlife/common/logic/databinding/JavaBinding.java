package com.bitlove.fetlife.common.logic.databinding;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.ext.tasklist.TaskListPlugin;

public class JavaBinding {

    @BindingAdapter({"items", "layout", "itemBindingId"})
    public static <T extends BindableRecyclerAdapter.Diffable> void setItems(RecyclerView recyclerView, List<T> items, int layoutId, int bindingId) {
        if (items == null) items = new ArrayList<>();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null || !(adapter instanceof BindableRecyclerAdapter)) {
            adapter = new BindableRecyclerAdapter(layoutId, bindingId);
        }
        BindableRecyclerAdapter bindableRecyclerAdapter = (BindableRecyclerAdapter)adapter;
        ((BindableRecyclerAdapter) adapter).setItems(items);
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter({"markwon"})
    public static void setMarkwon(TextView textView, String text) {
        Context context = textView.getContext();
        Markwon.builder(context).usePlugin(TaskListPlugin.create(context)).build().setMarkdown(textView, text);
    }

}
