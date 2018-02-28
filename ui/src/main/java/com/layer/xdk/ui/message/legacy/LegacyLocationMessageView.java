package com.layer.xdk.ui.message.legacy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.layer.xdk.ui.message.MessageViewHelper;

import java.net.URLEncoder;

public class LegacyLocationMessageView extends AppCompatImageView {

    private LegacyLocationMessageModel mModel;

    public LegacyLocationMessageView(Context context) {
        this(context, null, 0);
    }

    public LegacyLocationMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LegacyLocationMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        MessageViewHelper messageViewHelper = new MessageViewHelper(context);
        messageViewHelper.setOnClickListener(this, new OnClickListener() {
            @Override
            public void onClick(View view) {
                LegacyLocationMessageModel.Location location = mModel.getLocation();
                String encodedLabel = (location.mLabel == null) ? URLEncoder.encode("Shared Marker") : URLEncoder.encode(location.mLabel);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + location.mLatitude + "," + location.mLongitude + "(" + encodedLabel + ")&z=16"));
                getContext().startActivity(intent);
            }
        });
    }

    public void setMessageModel(@Nullable LegacyLocationMessageModel model) {
        mModel = model;
    }
}
