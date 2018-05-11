package com.layer.xdk.ui.testactivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.layer.sdk.messaging.Presence;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarView;
import com.layer.xdk.ui.identity.DefaultIdentityFormatter;
import com.layer.xdk.ui.message.image.cache.ImageCacheWrapper;
import com.layer.xdk.ui.message.image.cache.PicassoImageCacheWrapper;
import com.layer.xdk.ui.mock.MockIdentity;
import com.layer.xdk.ui.presence.PresenceView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AvatarActivityTestView extends Activity implements AdapterView.OnItemSelectedListener {

    public static final int VIEW_ID_SPINNER = 1;

    private Spinner mPresenceSpinner;
    private PresenceView mPresenceView;
    private MockIdentity mMockIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenceView = new PresenceView(this);
        mPresenceView.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.xdk_ui_avatar_presence_height),
                getResources().getDimensionPixelSize(R.dimen.xdk_ui_avatar_presence_height))
        );
        AvatarView avatarView = new AvatarView(this);
        avatarView.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.xdk_ui_avatar_width),
                getResources().getDimensionPixelSize(R.dimen.xdk_ui_avatar_height))
        );
        mPresenceSpinner = new Spinner(this);
        mPresenceSpinner.setId(VIEW_ID_SPINNER);
        LinearLayout.LayoutParams spinnerLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        spinnerLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        mPresenceSpinner.setLayoutParams(spinnerLayoutParams);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(mPresenceView);
        linearLayout.addView(avatarView);
        linearLayout.addView(mPresenceSpinner);
        setContentView(linearLayout);

        mMockIdentity = new MockIdentity();

        mPresenceView.setParticipants(mMockIdentity);
        ImageCacheWrapper imageCacheWrapper = new PicassoImageCacheWrapper(Picasso.with(this));
        avatarView.setImageCacheWrapper(imageCacheWrapper);
        avatarView.setIdentityFormatter(new DefaultIdentityFormatter(getApplicationContext()));
        avatarView.setParticipants(mMockIdentity);
        setUp();
    }

    private void setUp() {
        mPresenceSpinner.setOnItemSelectedListener(this);
        List<String> presenceStates = new ArrayList<>();
        for (Presence.PresenceStatus status : Presence.PresenceStatus.values()) {
            if (status.isUserSettable()) {
                presenceStates.add(status.toString());
            }
        }
        ArrayAdapter<String> presenceSpinnerDataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, presenceStates);
        presenceSpinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPresenceSpinner.setAdapter(presenceSpinnerDataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mMockIdentity.getPresenceStatus(i);
        mPresenceView.invalidate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
