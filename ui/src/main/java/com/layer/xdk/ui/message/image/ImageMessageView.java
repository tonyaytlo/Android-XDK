package com.layer.xdk.ui.message.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.layer.xdk.ui.message.MessageViewHelper;

public class ImageMessageView extends AppCompatImageView {

    private MessageViewHelper mMessageViewHelper;

    public ImageMessageView(Context context) {
        this(context, null, 0);
    }

    public ImageMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMessageViewHelper = new MessageViewHelper(context);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessageViewHelper.performAction();
            }
        });
    }

    public void setMessageModel(@Nullable ImageMessageModel model) {
        mMessageViewHelper.setMessageModel(model);
        if (model != null) {
            setupImageViewDimensions(model);
        }
    }

    private void setupImageViewDimensions(@NonNull ImageMessageModel model) {
        ImageMessageMetadata metadata = model.getMetadata();
        if (metadata != null) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();

            int width = (metadata.getPreviewWidth() > 0 ? metadata.getPreviewWidth() : metadata.getWidth());
            width = width > 0 ? layoutParams.width : ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = metadata.getPreviewHeight() > 0 ? metadata.getPreviewHeight() : metadata.getHeight();
            height = height > 0 ? layoutParams.height : ViewGroup.LayoutParams.WRAP_CONTENT;

            layoutParams.width = width;
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }
    }
}
