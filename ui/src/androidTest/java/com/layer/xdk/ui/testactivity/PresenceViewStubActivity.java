package com.layer.xdk.ui.testactivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.presence.PresenceView;

public class PresenceViewStubActivity extends Activity {

    private PresenceView mPresenceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenceView = new PresenceView(this);
        mPresenceView.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.xdk_ui_avatar_presence_height),
                getResources().getDimensionPixelSize(R.dimen.xdk_ui_avatar_presence_height))
        );
        setContentView(mPresenceView);
    }

    public PresenceView getPresenceView() {
        return mPresenceView;
    }
}
