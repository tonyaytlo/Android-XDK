package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.UiTitledMessageContainerBinding;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.view.MessageView;

public class TitledMessageContainer extends MessageContainer {
    private UiTitledMessageContainerBinding mBinding;
    private MessageView mMessageView;

    public TitledMessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public TitledMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitledMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = UiTitledMessageContainerBinding.inflate(inflater, this, true);
        mBinding.uiTitledMessageContainerTitle.setVisibility(GONE);
        setCornerRadius(context.getResources().getDimension(R.dimen.ui_titled_message_container_corner_radius));
    }

    @Override
    public void setMessageView(MessageView view) {
        mMessageView = view;
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // TODO: AND-1242 Figure out how to not have to remove all the subviews in the content view
        //       This is needed because containers in inner messageviewers have problems related to
        //       recycling due to model type mismatches
        mBinding.uiTitledMessageContainerContentView.removeAllViews();
        mBinding.uiTitledMessageContainerContentView.addView(view);
    }

    @Override
    public <T extends MessageModel> void setMessageModel(T model) {
        mMessageView.setMessageModel(model);
        mBinding.setViewModel(model);
        mBinding.executePendingBindings();
        setContentBackground(model);
    }

    @Override
    protected <T extends MessageModel> void setContentBackground(T model) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ui_titled_message_container_background);
        background.setColor(ContextCompat.getColor(getContext(), model.getBackgroundColor()));
        mBinding.uiTitledMessageContainerContentView.setBackgroundDrawable(background);
    }
}
