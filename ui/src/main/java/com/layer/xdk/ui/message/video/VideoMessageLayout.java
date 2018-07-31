package com.layer.xdk.ui.message.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.view.MessageViewHelper;

public class VideoMessageLayout extends FrameLayout {

    private MessageViewHelper mMessageViewHelper;
    private AppCompatImageView mPreviewImage;

    public VideoMessageLayout(@NonNull Context context) {
        this(context, null);
    }

    public VideoMessageLayout(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoMessageLayout(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPreviewImage = new AppCompatImageView(context, attrs, defStyleAttr);
        mPreviewImage.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mPreviewImage.setMaxWidth(getResources().getDimensionPixelSize(R.dimen.xdk_ui_message_default_max_width));
        mPreviewImage.setMaxHeight(getResources().getDimensionPixelSize(R.dimen.xdk_ui_video_message_maximum_height));
        mPreviewImage.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.xdk_ui_video_message_minimum_width));
        mPreviewImage.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.xdk_ui_video_message_minimum_height));
        mPreviewImage.setAdjustViewBounds(true);
        mPreviewImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(mPreviewImage);

        AppCompatImageView overlay = new AppCompatImageView(context, attrs, defStyleAttr);
        int overlaySize = getResources().getDimensionPixelSize(
                R.dimen.xdk_ui_video_message_overlay_size);
        overlay.setLayoutParams(new LayoutParams(overlaySize, overlaySize, Gravity.CENTER));
        overlay.setImageResource(R.drawable.xdk_ui_ic_media_play);
        addView(overlay);

        mMessageViewHelper = new MessageViewHelper(context);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageViewHelper.performAction();
            }
        });
    }

    public void setMessageModel(@Nullable VideoMessageModel model) {
        mMessageViewHelper.setMessageModel(model);
        if (model != null) {
            ViewGroup.LayoutParams layoutParams = mPreviewImage.getLayoutParams();
            if (model.getPreviewRequestParameters() != null) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                model.getImageCacheWrapper().loadImage(model.getPreviewRequestParameters(), mPreviewImage);
            } else {
                layoutParams.height =
                        getResources().getDimensionPixelSize(R.dimen.xdk_ui_video_message_default_height);
                layoutParams.width =
                        getResources().getDimensionPixelSize(R.dimen.xdk_ui_video_message_default_width);
            }
            mPreviewImage.setLayoutParams(layoutParams);
        }
    }
}
