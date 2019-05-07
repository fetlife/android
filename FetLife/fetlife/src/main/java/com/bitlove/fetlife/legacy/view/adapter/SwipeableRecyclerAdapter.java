package com.bitlove.fetlife.legacy.view.adapter;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeableRecyclerAdapter<T extends SwipeableViewHolder> extends RecyclerView.Adapter<T> {

    private boolean useSwipe = true;

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        if (!useSwipe()) {
            return;
        }
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, getSwipeDirections()) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                T swipeableViewHolder = (T) viewHolder;
                if (swipeableViewHolder.getSwipeableLayout() == null) {
                    return;
                }
                SwipeableRecyclerAdapter.this.onItemRemove(swipeableViewHolder, recyclerView, swipeDir == ItemTouchHelper.RIGHT);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    T swipeableViewHolder = (T) viewHolder;
                    if (swipeableViewHolder.getSwipeableLayout() == null) {
                        return;
                    }
                    getDefaultUIUtil().onSelected(swipeableViewHolder.getSwipeableLayout());
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                T swipeableViewHolder = (T) viewHolder;
                if (swipeableViewHolder.getSwipeableLayout() == null) {
                    return;
                }
                setSwipeRightBackgroundVisibility(swipeableViewHolder, View.GONE);
                setSwipeLeftBackgroundVisibility(swipeableViewHolder, View.GONE);
                getDefaultUIUtil().clearView(swipeableViewHolder.getSwipeableLayout());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                T swipeableViewHolder = (T) viewHolder;
                if (swipeableViewHolder.getSwipeableLayout() == null) {
                    return;
                }
                getDefaultUIUtil().onDraw(c, recyclerView, swipeableViewHolder.getSwipeableLayout(), dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                T swipeableViewHolder = (T) viewHolder;
                if (swipeableViewHolder.getSwipeableLayout() == null) {
                    return;
                }

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
                    if (dX > 0) {
                        setSwipeRightBackgroundVisibility(swipeableViewHolder, View.VISIBLE);
                        setSwipeLeftBackgroundVisibility(swipeableViewHolder, View.GONE);
                    } else if (dX < 0) {
                        setSwipeRightBackgroundVisibility(swipeableViewHolder, View.GONE);
                        setSwipeLeftBackgroundVisibility(swipeableViewHolder, View.VISIBLE);
                    } else {
                        setSwipeRightBackgroundVisibility(swipeableViewHolder, View.GONE);
                        setSwipeLeftBackgroundVisibility(swipeableViewHolder, View.GONE);
                    }
                } else {
                    setSwipeRightBackgroundVisibility(swipeableViewHolder, View.GONE);
                    setSwipeLeftBackgroundVisibility(swipeableViewHolder, View.GONE);
                }
                getDefaultUIUtil().onDrawOver(c, recyclerView, swipeableViewHolder.getSwipeableLayout(), dX, dY, actionState, isCurrentlyActive);
            }

            private void setSwipeRightBackgroundVisibility(SwipeableViewHolder swipeableViewHolder, int visibility) {
                setVisibility(swipeableViewHolder.getSwipeRightBackground(), visibility);
            }

            private void setSwipeLeftBackgroundVisibility(SwipeableViewHolder swipeableViewHolder, int visibility) {
                setVisibility(swipeableViewHolder.getSwipeLeftBackground(), visibility);
            }

            private void setVisibility(View view, int visibility) {
                if (view != null) {
                    view.setVisibility(visibility);
                }
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    protected boolean useSwipe() {
        return useSwipe;
    }

    public void setUseSwipe(boolean useSwipe) {
        this.useSwipe = useSwipe;
    }

    protected int getSwipeDirections() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    }

    protected abstract void onItemRemove(T viewHolder, RecyclerView recyclerView, boolean swipedRight);
}