package com.layer.xdk.ui.recyclerview;

import com.layer.sdk.query.Queryable;

/**
 * Listens for clicks on an item in ItemsListAdapter.
 */
public interface OnItemClickListener<ITEM extends Queryable> {
    /**
     * Alerts the listener to item clicks.
     *
     * @param item The item clicked.
     */
    void onItemClick(ITEM item);

    /**
     * Alerts the listener to long item clicks.
     *
     * @param item The item long-clicked.
     * @return true if the long-click was handled, false otherwise.
     */
    boolean onItemLongClick(ITEM item);
}
