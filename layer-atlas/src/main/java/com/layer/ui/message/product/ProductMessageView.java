package com.layer.ui.message.product;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.layer.ui.databinding.UiProductMessageBinding;
import com.layer.ui.message.container.EmptyMessageContainer;
import com.layer.ui.message.container.MessageContainer;
import com.layer.ui.message.view.MessageView;

public class ProductMessageView extends MessageView<ProductMessageModel> {
    private UiProductMessageBinding mBinding;

    public ProductMessageView(Context context) {
        this(context, null, 0);
    }

    public ProductMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBinding = UiProductMessageBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public void setMessageModel(final ProductMessageModel model) {
        mBinding.setViewModel(model);
        mBinding.getRoot().setOnClickListener(new OnClickListener() {
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
