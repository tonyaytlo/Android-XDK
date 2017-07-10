package com.layer.ui.conversationitem;

import com.layer.sdk.messaging.Conversation;

/**
 * Listens for item clicks on an IntegrationConversationsAdapter.
 */
public interface OnConversationItemClickListener {
    /**
     * Alerts the listener to item clicks.
     *
     * @param conversation The item clicked.
     */
    void onConversationClick(Conversation conversation);

    /**
     * Alerts the listener to long item clicks.
     *
     * @param conversation The item long-clicked.
     * @return true if the long-click was handled, false otherwise.
     */
    boolean onConversationLongClick(Conversation conversation);
}
