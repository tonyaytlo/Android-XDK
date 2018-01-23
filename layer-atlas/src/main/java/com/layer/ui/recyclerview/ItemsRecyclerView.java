package com.layer.ui.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.layer.sdk.query.Queryable;
import com.layer.ui.adapters.ItemRecyclerViewAdapter;

public class ItemsRecyclerView<ITEM extends Queryable> extends RecyclerView {

    protected ItemRecyclerViewAdapter mAdapter;
    protected ItemTouchHelper mSwipeItemTouchHelper;

    public ItemsRecyclerView(Context context) {
        super(context);
    }

    public ItemsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemsRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void refresh() {
        if (mAdapter != null) mAdapter.refresh();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof ItemRecyclerViewAdapter) {
            super.setAdapter(adapter);
            mAdapter = (ItemRecyclerViewAdapter) adapter;
            refresh();
        } else {
            throw new IllegalArgumentException("Adapter must be of type ItemRecyclerViewAdapter");
        }
    }

    /**
     * Performs cleanup when the Activity/Fragment using the adapter is destroyed.
     */
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.onDestroy();
        }
    }

    /**
     * Automatically refresh on resume
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) return;
        refresh();
    }

    /**
     * Convenience pass-through to this list's ItemAdapter.
     *
     * @see ItemRecyclerViewAdapter#setItemClickListener(OnItemClickListener)
     */
    public void setItemClickListener(OnItemClickListener<ITEM> listener) {
        mAdapter.setItemClickListener(listener);
    }
}
