package com.layer.xdk.ui.message.text;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.JsonObject;
import com.layer.xdk.ui.databinding.XdkUiTextMessageViewBinding;
import com.layer.xdk.ui.message.action.ActionHandlerRegistry;

public class TextMessageViewController {

    private Context mContext;

    public TextMessageViewController(final XdkUiTextMessageViewBinding binding) {
        mContext = binding.getRoot().getContext();
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getViewModel() != null) {
                    TextMessageModel model = binding.getViewModel();
                    performAction(model.getActionEvent(), model.getActionData());
                }
            }
        });
    }

//    @Override
    public void performAction(String event, JsonObject customData) {
        if (!TextUtils.isEmpty(event)) {
            ActionHandlerRegistry.dispatchEvent(mContext, event, customData);
        }
    }
}
