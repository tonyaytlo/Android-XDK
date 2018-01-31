package com.layer.xdk.ui.conversation;

import android.content.Context;
import android.util.AttributeSet;

import com.layer.sdk.messaging.Conversation;
import com.layer.xdk.ui.adapters.ConversationItemsAdapter;

import com.layer.xdk.ui.fourpartitem.FourPartItemsListView;

public class ConversationItemsListView extends FourPartItemsListView<Conversation, ConversationItemsAdapter> {

    public ConversationItemsListView(Context context) {
        super(context);
    }

    public ConversationItemsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
