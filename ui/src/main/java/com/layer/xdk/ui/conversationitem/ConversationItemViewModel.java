package com.layer.xdk.ui.conversationitem;

import android.databinding.Bindable;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewModel;
import com.layer.xdk.ui.identity.IdentityFormatter;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class ConversationItemViewModel extends FourPartItemViewModel<Conversation> {
    //View Logic
    protected ConversationItemFormatter mConversationItemFormatter;
    protected Identity mAuthenticatedUser;

    // View Data
    protected Set<Identity> mParticipantsMinusAuthenticatedUser;

    @Inject
    public ConversationItemViewModel(IdentityFormatter identityFormatter,
            ConversationItemFormatter conversationItemFormatter) {
        super(identityFormatter);
        mConversationItemFormatter = conversationItemFormatter;
        mParticipantsMinusAuthenticatedUser = new HashSet<>();
    }

    @Override
    public void setItem(Conversation conversation) {
        super.setItem(conversation);

        mParticipantsMinusAuthenticatedUser.clear();

        mParticipantsMinusAuthenticatedUser.addAll(conversation.getParticipants());
        mParticipantsMinusAuthenticatedUser.remove(mAuthenticatedUser);

        notifyChange();
    }

    public void setAuthenticatedUser(Identity authenticatedUser) {
        mAuthenticatedUser = authenticatedUser;
    }

    @Bindable
    public String getTitle() {
        return mConversationItemFormatter.getConversationTitle(getItem());
    }

    @Bindable
    public String getSubtitle() {
        return mConversationItemFormatter.getLastMessagePreview(getItem());
    }

    @Override
    public String getAccessoryText() {
        return mConversationItemFormatter.getTimeStamp(getItem());
    }

    @Override
    public boolean isSecondaryState() {
        return getItem().getTotalUnreadMessageCount() > 0;
    }

    @Override
    public Set<Identity> getIdentities() {
        return mParticipantsMinusAuthenticatedUser;
    }
}
