package com.layer.xdk.ui.message.link;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.layer.xdk.ui.databinding.XdkUiLinkMessageViewBinding;
import com.layer.xdk.ui.message.container.StandardMessageContainer;
import com.layer.xdk.ui.message.view.MessageView;

public class LinkMessageView extends MessageView<LinkMessageModel> implements View.OnClickListener {
    private XdkUiLinkMessageViewBinding mBinding;

    public LinkMessageView(Context context) {
        this(context, null, 0);
    }

    public LinkMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public LinkMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);

        mBinding = XdkUiLinkMessageViewBinding.inflate(inflater, this, true);
        TextView textView = mBinding.linkMessageTextView;
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setLinksClickable(false);

        setOnClickListener(this);
    }

    @Override
    public void setMessageModel(LinkMessageModel model) {
        mBinding.setViewModel(model);
    }

    @Override
    public void onClick(View view) {
        LinkMessageModel model = mBinding.getViewModel();
        if (model != null && model.getActionEvent() != null) {
            performAction(model.getActionEvent(), model.getActionData());
        }
    }
}
