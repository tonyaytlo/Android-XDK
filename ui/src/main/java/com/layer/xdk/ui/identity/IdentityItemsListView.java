package com.layer.xdk.ui.identity;

import android.content.Context;
import android.util.AttributeSet;

import com.layer.sdk.messaging.Identity;

import com.layer.xdk.ui.fourpartitem.FourPartItemsListView;

public class IdentityItemsListView extends FourPartItemsListView<Identity, IdentityItemsAdapter> {
    public IdentityItemsListView(Context context) {
        super(context);
    }

    public IdentityItemsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
