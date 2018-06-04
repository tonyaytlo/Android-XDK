package com.layer.xdk.ui.message.audio;

import android.content.Context;
import android.databinding.ViewStubProxy;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.container.StandardMessageContainer;
import com.layer.xdk.ui.message.view.MessageViewHelper;
import com.layer.xdk.ui.util.Log;

public class AudioMessageLayout extends LinearLayout {
    private MessageViewHelper mMessageViewHelper;
    private ViewStubProxy mControlButtonHolder;

    public AudioMessageLayout(Context context) {
        this(context, null, 0);
    }

    public AudioMessageLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioMessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mMessageViewHelper = new MessageViewHelper(context);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessageViewHelper.performAction();
            }
        });
    }

    public void setMessageModel(AudioMessageModel model) {
        mMessageViewHelper.setMessageModel(model);
        updatePlayPauseButton();
    }

    private void updatePlayPauseButton() {
        ViewStubProxy controlButtonHolder = getControlButtonHolder();
        if (!controlButtonHolder.isInflated()) {
            controlButtonHolder.getViewStub().setLayoutResource(R.layout.xdk_ui_audio_message_control_view);
            controlButtonHolder.getViewStub().inflate();
        }
        // Update image
        ImageButton controlButton = ((ImageButton) controlButtonHolder.getRoot());
        // TODO update to correct value (depends on how media controller/session is integrated. Hardcode for now)
        controlButton.setImageResource(R.drawable.xdk_ui_audio_pause);

    }

    @NonNull
    private ViewStubProxy getControlButtonHolder() {
        if (mControlButtonHolder == null) {
            if (getParent() instanceof StandardMessageContainer) {
                mControlButtonHolder = ((StandardMessageContainer) getParent()).getRightMetadataView();
            } else {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e("Audio Message view expects to be wrapped in a "
                            + "standard message container");
                }
                throw new UnsupportedOperationException("Audio Message view expects to be wrapped "
                        + "in a standard message container");
            }
        }
        return mControlButtonHolder;
    }
}
