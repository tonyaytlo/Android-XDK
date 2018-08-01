package com.layer.xdk.ui.message.large;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.feedback.LargeFeedbackMessageFragment;
import com.layer.xdk.ui.util.Log;

/**
 * Activity that displays large versions of messages. Currently only supports large media messages.
 */
public class LargeMessageActivity extends AppCompatActivity {

    public static final String ARG_TITLE = "title";
    public static final String ARG_FRAGMENT_TYPE = "fragment_type";
    private static final String TAG_LARGE_MESSAGE_FRAGMENT = "large_message_fragment";

    public static final int FRAGMENT_TYPE_MEDIA = 0;
    public static final int FRAGMENT_TYPE_FEEDBACK = 1;

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
            throw new IllegalArgumentException(
                    "No extras available to start a LargeMessageActivity");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(extras.getString(ARG_TITLE));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(TAG_LARGE_MESSAGE_FRAGMENT) == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = createFragmentFromType();
            fragment.setArguments(extras);
            fragmentTransaction.add(R.id.xdk_ui_large_message_fragment_container, fragment,
                    TAG_LARGE_MESSAGE_FRAGMENT);
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

    /**
     * @return a new fragment whose class is based on the {@link #ARG_FRAGMENT_TYPE} intent extra
     */
    private Fragment createFragmentFromType() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int type = extras.getInt(ARG_FRAGMENT_TYPE, -1);
            switch (type) {
                case FRAGMENT_TYPE_MEDIA:
                    return new LargeMediaMessageFragment();
                case FRAGMENT_TYPE_FEEDBACK:
                    return new LargeFeedbackMessageFragment();
            }
        }

        if (Log.isLoggable(Log.ERROR)) {
            Log.e("No valid fragment type specified in the intent's extras. Extras: "
                    + getIntent().getExtras());
        }
        throw new IllegalArgumentException(
                "No valid fragment type specified in the intent's extras. Extras: "
                        + getIntent().getExtras());
    }
}
