package com.bitlove.fetlife.legacy.util;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

public class ViewUtil {

    public static void increaseTouchArea(final View view, final int padding) {
        final View parent = (View) view.getParent();
        parent.post( new Runnable() {
            // Post in the parent's message queue to make sure the parent
            // lays out its children before we call getHitRect()
            public void run() {
                final Rect r = new Rect();
                view.getHitRect(r);
                r.top -= padding;
                r.bottom += padding;
                r.left -= padding;
                r.right += padding;
                parent.setTouchDelegate( new TouchDelegate( r , view));
            }
        });
    }


}
