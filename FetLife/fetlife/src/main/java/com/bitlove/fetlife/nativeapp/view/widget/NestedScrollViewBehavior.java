package com.bitlove.fetlife.nativeapp.view.widget;

import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.ref.WeakReference;

public class NestedScrollViewBehavior extends AppBarLayout.Behavior {

    // Lower value means fling action is more easily triggered
    static final int MIN_DY_DELTA = 4;
    // Lower values mean less velocity, higher means higher velocity
    static final int FLING_FACTOR = 20;

    int mTotalDy;
    int mPreviousDy;
    WeakReference<AppBarLayout> mPreScrollChildRef;

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                  View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        // Reset the total fling delta distance if the user starts scrolling back up
        if(dy < 0) {
            mTotalDy = 0;
        }
        // Only track move distance if the movement is positive (since the bug is only present
        // in upward flings), equal to the consumed value and the move distance is greater
        // than the minimum difference value
        if(dy > 0 && consumed[1] == dy && MIN_DY_DELTA < Math.abs(mPreviousDy - dy)) {
            mPreScrollChildRef = new WeakReference<>(child);
            mTotalDy += dy * FLING_FACTOR;
        }
        mPreviousDy = dy;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        // Stop any previous fling animations that may be running
        onNestedFling(parent, child, target, 0, 0, false);
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout parent, AppBarLayout abl, View target) {
        if(mTotalDy > 0 && mPreScrollChildRef != null && mPreScrollChildRef.get() != null) {
            // Programmatically trigger fling if all conditions are met
            onNestedFling(parent, mPreScrollChildRef.get(), target, 0, mTotalDy, false);
            mTotalDy = 0;
            mPreviousDy = 0;
            mPreScrollChildRef = null;
        }
        super.onStopNestedScroll(parent, abl, target);
    }
}
