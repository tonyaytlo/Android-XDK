package com.layer.xdk.ui.message;

import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.databinding.XdkUiMessageItemFooterBinding;
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import java.util.Set;

// TODO AND-1353 This used to use the MessageItemLegacyViewModel for some reason. Changed it so I can delete that class
public class MessageItemFooterViewHolder extends MessageItemViewHolder<MessageItemCardViewModel, XdkUiMessageItemFooterBinding> {

    public MessageItemFooterViewHolder(ViewGroup parent, MessageItemCardViewModel messageItemViewModel,
                                       ImageCacheWrapper imageCacheWrapper) {
        super(parent, R.layout.xdk_ui_message_item_footer, messageItemViewModel);
        getBinding().avatar.init(new AvatarViewModelImpl(imageCacheWrapper), new IdentityFormatterImpl(parent.getContext()));
    }

    public void clear() {
        getBinding().root.removeAllViews();
    }

    public void bind(Set<Identity> users, View mFooterView, boolean shouldAvatarBeVisible) {
        // TODO AND-1353 Fix this
//        getBinding().root.addView(mFooterView);
//        getViewHolderModel().setParticipants(users);
//        getViewHolderModel().setAvatarViewVisibilityType(shouldAvatarBeVisible);
//        int numberOfUsers = users.size();
//
//        if (numberOfUsers > 2) {
//            getViewHolderModel().setTypingIndicatorMessageVisibility(true);
//            getViewHolderModel().setMessageFooterAnimationVisibility(false);
//            String firstUser = "", secondUser = "";
//            int counter = 0;
//
//            for (Identity user : users) {
//                counter++;
//                if (counter == 1) {
//                    firstUser = user.getDisplayName();
//                } else if (counter == 2) {
//                    secondUser = user.getDisplayName();
//                    break;
//                }
//            }
//            Resources resources = mFooterView.getContext().getResources();
//            int remainingUsers = numberOfUsers % 2;
//            String typingIndicatorMessage = resources.getQuantityString(R.plurals.xdk_ui_typing_indicator_message,
//                    remainingUsers, firstUser, secondUser, remainingUsers);
//            getViewHolderModel().setTypingIndicatorMessage(typingIndicatorMessage);
//        } else {
//            getViewHolderModel().setTypingIndicatorMessageVisibility(false);
//            getViewHolderModel().setMessageFooterAnimationVisibility(true);
//        }
//
//        getViewHolderModel().notifyChange();
//        getBinding().setViewModel(getViewHolderModel());
    }
}
