package com.layer.xdk.ui.message.receipt;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.layer.xdk.ui.databinding.XdkUiReceiptMessageBinding;
import com.layer.xdk.ui.message.container.MessageConstraintContainer;
import com.layer.xdk.ui.message.container.TitledMessageContainer;
import com.layer.xdk.ui.message.location.LocationMessageModel;
import com.layer.xdk.ui.message.product.ProductMessageModel;
import com.layer.xdk.ui.message.view.MessageView;

import java.util.List;

public class ReceiptMessageView extends MessageView<ReceiptMessageModel> {
    private XdkUiReceiptMessageBinding mBinding;

    public ReceiptMessageView(Context context) {
        this(context, null, 0);
    }

    public ReceiptMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReceiptMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);
        mBinding = XdkUiReceiptMessageBinding.inflate(inflater, this, true);
    }

    @Override
    public void setMessageModel(ReceiptMessageModel model) {
        mBinding.setViewModel(model);
        List<ProductMessageModel> products = model.getProductItemModels();
        if (!products.isEmpty()) {
            mBinding.productsLayout.setVisibility(VISIBLE);
            mBinding.productsLayout.removeAllViews();

            for (ProductMessageModel product : products) {
                ReceiptMessageProductItemView view = new ReceiptMessageProductItemView(getContext());
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mBinding.productsLayout.addView(view);
                view.setProductModel(product);
            }

        } else {
            mBinding.productsLayout.setVisibility(View.GONE);
        }

        mBinding.shippingAddressValue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickShippingAddress();
            }
        });

        mBinding.billingAddressValue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBillingAddress();
            }
        });
    }

    @Override
    public Class<? extends MessageConstraintContainer> getContainerClass() {
        return TitledMessageContainer.class;
    }

    public void onClickBillingAddress() {
        LocationMessageModel model = mBinding.getViewModel().getBillingAddressLocationModel();
        if (model != null) {
            performAction(model.getActionEvent(), model.getActionData());
        }
    }

    public void onClickShippingAddress() {
        LocationMessageModel model = mBinding.getViewModel().getShippingAddressLocationModel();
        if (model != null) {
            performAction(model.getActionEvent(), model.getActionData());
        }
    }
}
