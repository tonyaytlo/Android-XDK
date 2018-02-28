package com.layer.xdk.ui.message.legacy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.layer.xdk.ui.message.MessageViewHelper;
import com.layer.xdk.ui.util.imagepopup.ImagePopupActivity;

public class LegacyThreePartImageMessageView extends AppCompatImageView {

    private LegacyThreePartImageMessageModel mModel;

    public LegacyThreePartImageMessageView(Context context) {
        this(context, null, 0);
    }

    public LegacyThreePartImageMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LegacyThreePartImageMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        MessageViewHelper messageViewHelper = new MessageViewHelper(context);
        messageViewHelper.setOnClickListener(this, new OnClickListener() {
            @Override
            public void onClick(View view) {
                LegacyThreePartImageMessageModel.Info info = mModel.getInfo();
                Intent intent = new Intent(view.getContext(), ImagePopupActivity.class);
                intent.putExtra("previewId", info.previewPartId);
                intent.putExtra("fullId", info.fullPartId);
                intent.putExtra("info", info);

                getContext().startActivity(intent);
            }
        });
    }

    public void setMessageModel(@Nullable LegacyThreePartImageMessageModel model) {
        mModel = model;
        if (model != null) {
            setupImageViewDimensions(model);
        }
    }

    private void setupImageViewDimensions(@NonNull LegacyThreePartImageMessageModel model) {
        LegacyThreePartImageMessageModel.Info info = model.getInfo();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int width = info.width > 0 ? layoutParams.width : ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = info.height > 0 ? layoutParams.height : ViewGroup.LayoutParams.WRAP_CONTENT;

        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }
}
