package com.layer.ui.identity;

import android.content.Context;
import android.util.AttributeSet;

import com.layer.sdk.messaging.Identity;

import com.layer.ui.fourpartitem.FourPartItemsListView;

public class IdentityItemsListView extends FourPartItemsListView<Identity, IdentityItemsAdapter> {
    public IdentityItemsListView(Context context) {
        super(context);
    }

    public IdentityItemsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
