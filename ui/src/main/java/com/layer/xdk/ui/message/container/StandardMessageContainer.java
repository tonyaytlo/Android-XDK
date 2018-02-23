package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Constraints;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiStandardMessageContainerBinding;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.view.MessageView;

public class StandardMessageContainer extends MessageContainer {

    private XdkUiStandardMessageContainerBinding mBinding;
    private MessageView mMessageView;

    public StandardMessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public StandardMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public StandardMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = XdkUiStandardMessageContainerBinding.inflate(inflater, this, true);
        mBinding.xdkUiStandardMessageContainerTitle.setVisibility(GONE);
        mBinding.xdkUiStandardMessageContainerSubtitle.setVisibility(GONE);
        setCornerRadius(context.getResources().getDimension(R.dimen.xdk_ui_standard_message_container_corner_radius));
    }

    @Override
    @Deprecated
    public void setMessageView(MessageView view) {
//        mMessageView = view;
//        view.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));

        // TODO: AND-1242 Figure out how to not have to remove all the subviews in the content view
        //       This is needed because containers in inner messageviewers have problems related to
        //       recycling due to model type mismatches
//        mBinding.xdkUiStandardMessageContainerContentView.removeAllViews();
//        mBinding.xdkUiStandardMessageContainerContentView.addView(view);
    }

    @Override
    public View inflateMessageView(@LayoutRes int messageViewLayoutId) {
        ViewStub viewStub = mBinding.xdkUiStandardMessageContainerContentView.getViewStub();
        viewStub.setLayoutResource(messageViewLayoutId);
        return viewStub.inflate();
    }

    @Override
    public <T extends MessageModel> void setMessageModel(T model) {
        View messageView = mBinding.xdkUiStandardMessageContainerContentView.getRoot();
        ViewDataBinding messageBinding = DataBindingUtil.getBinding(messageView);
        messageBinding.setVariable(BR.viewModel, model);

        mBinding.setViewModel(model);
        mBinding.getViewModel().addOnPropertyChangedCallback(
                new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        // TODO AND-1242 only check necessary properties
                        MessageModel messageModel = (MessageModel) mBinding.getViewModel();
                        View messageRoot = mBinding.xdkUiStandardMessageContainerContentView.getRoot();
                        messageRoot.setVisibility(messageModel.getHasContent() ? VISIBLE : GONE);
                        int minWidth;
                        int bottomMargin = 0;
                        if (messageModel.getHasMetadata()) {
                            minWidth = getResources().getDimensionPixelSize(R.dimen.xdk_ui_standard_message_container_min_width);
                            bottomMargin = getResources().getDimensionPixelOffset(R.dimen.xdk_ui_margin_tiny);
                        } else {
                            minWidth = getResources().getDimensionPixelSize(
                                    R.dimen.xdk_ui_standard_message_container_min_width_zero);

                        }
                        messageRoot.setMinimumWidth(minWidth);
                        Constraints.LayoutParams layoutParams =
                                (Constraints.LayoutParams) messageRoot.getLayoutParams();
                        layoutParams.bottomMargin = bottomMargin;
                        messageRoot.setLayoutParams(layoutParams);
                    }
                });


        mBinding.executePendingBindings();
        setContentBackground(model);
    }

    @Override
    protected <T extends MessageModel> void setContentBackground(T model) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.xdk_ui_standard_message_container_content_background);
        background.setColor(ContextCompat.getColor(getContext(), model.getBackgroundColor()));
        mBinding.xdkUiStandardMessageContainerContentView.getRoot().setBackgroundDrawable(background);
    }
}
