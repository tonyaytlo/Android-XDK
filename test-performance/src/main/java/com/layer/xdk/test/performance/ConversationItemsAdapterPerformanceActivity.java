package com.layer.xdk.test.performance;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.layer.xdk.ui.conversation.ConversationItemsListView;
import com.layer.xdk.ui.conversation.adapter.ConversationItemsAdapter;

public class ConversationItemsAdapterPerformanceActivity extends Activity {

    private ConversationItemsListView mItemsListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        mItemsListView = new ConversationItemsListView(this);
        setContentView(mItemsListView);
    }

    void setAdapter(ConversationItemsAdapter adapter) {
        mItemsListView.setAdapter(adapter);
    }
}
