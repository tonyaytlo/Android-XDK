package com.layer.xdk.ui.message.file;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.layer.xdk.ui.databinding.XdkUiFileMessageLayoutBinding;
import com.layer.xdk.ui.message.container.MessageConstraintContainer;
import com.layer.xdk.ui.message.container.StandardMessageContainer;
import com.layer.xdk.ui.message.view.MessageView;

public class FileMessageView extends MessageView<FileMessageModel> {
    private XdkUiFileMessageLayoutBinding mBinding;

    public FileMessageView(Context context) {
        this(context, null, 0);
    }

    public FileMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mBinding = XdkUiFileMessageLayoutBinding.inflate(layoutInflater, this, true);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FileMessageModel model = mBinding.getViewModel();
                if (model != null) {
                    performAction(model.getActionEvent(), model.getActionData());
                }
            }
        });
    }

    @Override
    public void setMessageModel(FileMessageModel model) {
        mBinding.setViewModel(model);
    }

    @Override
    public Class<? extends MessageConstraintContainer> getContainerClass() {
        return StandardMessageContainer.class;
    }
}
