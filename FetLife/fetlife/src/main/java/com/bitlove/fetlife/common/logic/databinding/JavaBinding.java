package com.bitlove.fetlife.common.logic.databinding;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bitlove.fetlife.R;
import com.commonsware.cwac.anddown.AndDown;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.List;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.InternalStyleSheet;
import br.tiagohm.markdownview.css.styles.Github;
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
        Markwon.create(context).setMarkdown(textView, text);
    }

    @BindingAdapter({"markdown"})
    public static void setMarkDown(MarkdownView markdownView, String markDownText) {
        markdownView.setBackgroundColor(Color.TRANSPARENT);
        InternalStyleSheet css = new Github();
        css.addRule("body", "font-size: 16px", "color: white", "background-color: #222222", "margin: 0", "padding: 0");
        markdownView.addStyleSheet(css);
        markdownView.loadMarkdown(markDownText);
    }

    @BindingAdapter({"anddown"})
    public static void setAndDown(TextView textView, String markDownText) {
        AndDown andDown=new AndDown();


        String result=andDown.markdownToHtml(markDownText).replaceAll("<ul>","<flul>").replaceAll("<li>","<flli>");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(result, Html.FROM_HTML_MODE_COMPACT, null, new UlTagHandler()));
        } else {
            textView.setText(Html.fromHtml(result, null, new UlTagHandler()));
        }
    }

    public static class UlTagHandler implements Html.TagHandler{
        @Override
        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {
            if(tag.equalsIgnoreCase("flul") && !opening) output.append("\n");
            if(tag.equalsIgnoreCase("flli") && opening) output.append("\n\t• ");
        }
    }

}
