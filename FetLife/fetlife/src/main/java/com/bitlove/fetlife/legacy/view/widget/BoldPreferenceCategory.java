package com.bitlove.fetlife.legacy.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * UI widget to make a preference in a Prefence Screen displayed as bold
 */
public class BoldPreferenceCategory extends PreferenceCategory {
    public BoldPreferenceCategory(Context context) {
        super(context);
    }

    public BoldPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoldPreferenceCategory(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTypeface(null, Typeface.BOLD);
    }
}
