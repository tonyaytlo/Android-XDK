package com.layer.xdk.ui.message.product;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.layer.xdk.ui.databinding.XdkUiProductMessageBinding;
import com.layer.xdk.ui.message.container.EmptyMessageContainer;
import com.layer.xdk.ui.message.container.MessageContainer;
import com.layer.xdk.ui.message.view.MessageView;

public class ProductMessageView extends MessageView<ProductMessageModel> {
    private XdkUiProductMessageBinding mBinding;

    public ProductMessageView(Context context) {
        this(context, null, 0);
    }

    public ProductMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBinding = XdkUiProductMessageBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public void setMessageModel(final ProductMessageModel model) {
        mBinding.setViewModel(model);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getActionEvent() != null) {
                    performAction(model.getActionEvent(), model.getActionData());
                }
            }
        });
    }

    @Override
    public Class<? extends MessageContainer> getContainerClass() {
        return EmptyMessageContainer.class;
    }
}
