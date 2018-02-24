package com.layer.xdk.ui.message.location;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.layer.xdk.ui.databinding.XdkUiLocationMessageViewBinding;
import com.layer.xdk.ui.message.container.StandardMessageContainer;
import com.layer.xdk.ui.message.view.MessageView;

public class LocationMessageView extends MessageView<LocationMessageModel> {
    private XdkUiLocationMessageViewBinding mBinding;

    public LocationMessageView(Context context) {
        this(context, null, 0);
    }

    public LocationMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mBinding = XdkUiLocationMessageViewBinding.inflate(layoutInflater, this, true);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationMessageModel model = mBinding.getViewModel();
                performAction(model.getActionEvent(), model.getActionData());
            }
        });
    }

    @Override
    public void setMessageModel(LocationMessageModel model) {
        mBinding.setViewModel(model);
    }

    public void hideMap(boolean hideMap) {
        mBinding.setHideMap(hideMap);
    }

    public boolean isMapHidden() {
        return mBinding.getHideMap();
    }
}
