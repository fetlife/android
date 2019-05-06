package com.bitlove.fetlife.nativeapp.view.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;

import androidx.annotation.RequiresApi;

public class AutoAlignGridView extends GridView {

    public AutoAlignGridView(Context context) {
        super(context);
    }

    public AutoAlignGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoAlignGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AutoAlignGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
