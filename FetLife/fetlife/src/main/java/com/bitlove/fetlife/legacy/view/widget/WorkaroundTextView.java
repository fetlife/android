package com.bitlove.fetlife.legacy.view.widget;

import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Custom TextView to workaround issues with some Android Custom OS that occur when a text is selected within a scrollabe view
 */
public class WorkaroundTextView extends TextView {

    public WorkaroundTextView(Context context) {
        super(context);
    }

    public WorkaroundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkaroundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (selStart == -1 || selEnd == -1) {
            // FIXME @hack : https://code.google.com/p/android/issues/detail?id=137509
            CharSequence text = getText();
            if (text instanceof Spannable) {
                Selection.setSelection((Spannable) text, 0, 0);
            }
        } else {
            super.onSelectionChanged(selStart, selEnd);
        }
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        // FIXME simple workaround to https://code.google.com/p/android/issues/detail?id=191430
        int startSelection = getSelectionStart();
        int endSelection = getSelectionEnd();
        if (startSelection < 0 || endSelection < 0){
            Selection.setSelection((Spannable) getText(), getText().length());
        } else if (startSelection != endSelection) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                final CharSequence text = getText();
                setText(null);
                setText(text);
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
