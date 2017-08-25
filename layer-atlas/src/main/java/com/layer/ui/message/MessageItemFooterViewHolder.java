package com.layer.ui.message;

import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.ui.R;
import com.layer.ui.adapters.ItemViewHolder;
import com.layer.ui.avatar.AvatarViewModelImpl;
import com.layer.ui.databinding.UiMessageItemFooterBinding;
import com.layer.ui.identity.IdentityFormatterImpl;
import com.layer.ui.message.messagetypes.MessageStyle;
import com.layer.ui.util.imagecache.ImageCacheWrapper;

import java.util.Set;

public class MessageItemFooterViewHolder extends
        ItemViewHolder<Message, MessageItemViewModel, ViewDataBinding, MessageStyle> {

    protected ViewGroup mRoot;

    public MessageItemFooterViewHolder(UiMessageItemFooterBinding binding,
            MessageItemViewModel messageItemViewModel, ImageCacheWrapper imageCacheWrapper) {

        super(binding, messageItemViewModel);
        mRoot = binding.swipeable;
        binding.avatar.init(new AvatarViewModelImpl(imageCacheWrapper), new IdentityFormatterImpl());
    }

    public void bind(Set<Identity> users, View mFooterView, boolean shouldAvatarBeVisible) {

        mRoot.addView(mFooterView);
        super.getViewModel().setParticipants(users);
        super.getViewModel().setAvatarViewVisibilityType(shouldAvatarBeVisible);
        int numberOfUsers = users.size();

        if (numberOfUsers > 2) {
            super.getViewModel().setTypingIndicatorMessageVisibility(true);
            super.getViewModel().setMessageFooterAnimationVisibility(false);
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
            Resources resources = mFooterView.getContext().getResources();
            int remainingUsers = numberOfUsers % 2;
            String typingIndicatorMessage = resources.getQuantityString(R.plurals.layer_ui_typing_indicator_message,
                    remainingUsers, firstUser, secondUser, remainingUsers);
            super.getViewModel().setTypingIndicatorMessage(typingIndicatorMessage);
        } else {
            super.getViewModel().setTypingIndicatorMessageVisibility(false);
            super.getViewModel().setMessageFooterAnimationVisibility(true);
        }

        super.getViewModel().notifyChange();
        ((UiMessageItemFooterBinding) mBinding).setViewModel(super.getViewModel());
    }
}
