package com.layer.xdk.ui.message.adapter.viewholder;


import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.databinding.XdkUiMessageModelVhTypingIndicatorBinding;

import java.util.Set;

/**
 * Special type of {@link MessageModelVH} that holds and binds a typing indicator.
 *
 * Note that this doesn't have a {@link com.layer.xdk.ui.message.model.MessageModel}.
 */
public class TypingIndicatorVH extends
        MessageModelVH<TypingIndicatorVHModel, XdkUiMessageModelVhTypingIndicatorBinding> {

    public TypingIndicatorVH(ViewGroup parent, TypingIndicatorVHModel model) {
        super(parent, R.layout.xdk_ui_message_model_vh_typing_indicator, model);
        getBinding().setViewHolderModel(model);

        getBinding().avatar.init(new AvatarViewModelImpl(model.getImageCacheWrapper()), model.getIdentityFormatter());

        getBinding().executePendingBindings();
    }

    @Override
    void onBind() {
        // Unused
    }

    public void clear() {
        getBinding().root.removeAllViews();
    }

    public void bind(Set<Identity> users, View typingIndicatorLayout, boolean shouldAvatarBeVisible) {
        getBinding().root.addView(typingIndicatorLayout);
        getViewHolderModel().setParticipants(users);
        getViewHolderModel().setAvatarViewVisible(shouldAvatarBeVisible);
        int numberOfUsers = users.size();

        if (numberOfUsers > 2) {
            getViewHolderModel().setTypingIndicatorMessageVisible(true);
            getViewHolderModel().setAnimationVisible(false);
            String firstUser = "", secondUser = "";
            int counter = 0;

            for (Identity user : users) {
                counter++;
                if (counter == 1) {
                    firstUser = user.getDisplayName();
                } else if (counter == 2) {
                    secondUser = user.getDisplayName();
                    break;
                }
            }
            Resources resources = typingIndicatorLayout.getContext().getResources();
            int remainingUsers = numberOfUsers % 2;
            String typingIndicatorMessage = resources.getQuantityString(R.plurals.xdk_ui_typing_indicator_message,
                    remainingUsers, firstUser, secondUser, remainingUsers);
            getViewHolderModel().setTypingIndicatorMessage(typingIndicatorMessage);
        } else {
            getViewHolderModel().setTypingIndicatorMessageVisible(false);
            getViewHolderModel().setAnimationVisible(true);
        }

        getViewHolderModel().notifyChange();
        getBinding().executePendingBindings();
    }
}
