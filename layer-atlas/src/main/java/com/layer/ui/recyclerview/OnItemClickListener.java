package com.layer.ui.recyclerview;

import com.layer.sdk.query.Queryable;

/**
 * Listens for item clicks on an IntegrationConversationsAdapter.
 */
public interface OnItemClickListener<ITEM extends Queryable> {
    /**
     * Alerts the listener to item clicks.
     *
     * @param conversation The item clicked.
     */
    void onItemClick(ITEM conversation);

    /**
     * Alerts the listener to long item clicks.
     *
     * @param conversation The item long-clicked.
     * @return true if the long-click was handled, false otherwise.
     */
    boolean onItemLongClick(ITEM conversation);
}
