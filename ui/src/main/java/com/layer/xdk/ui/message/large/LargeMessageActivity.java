package com.layer.xdk.ui.message.large;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.util.Log;

/**
 * Activity that displays large versions of messages. Currently only supports large audio messages.
 */
public class LargeMessageActivity extends AppCompatActivity {

    public static final String ARG_TITLE = "title";
    private static final String TAG_LARGE_MESSAGE_FRAGMENT = "large_message_fragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xdk_ui_activity_large_message);

        getWindow().setBackgroundDrawableResource(R.color.xdk_ui_color_primary_gray);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("No extras available to start a LargeMessageActivity");
            }
            throw new IllegalArgumentException("No extras available to start a LargeMessageActivity");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(extras.getInt(ARG_TITLE));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(TAG_LARGE_MESSAGE_FRAGMENT) == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            LargeAudioMessageFragment fragment = new LargeAudioMessageFragment();
            fragment.setArguments(extras);
            fragmentTransaction.add(R.id.xdk_ui_large_message_fragment_container, fragment, TAG_LARGE_MESSAGE_FRAGMENT);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
