package com.layer.ui.conversationitem;

import android.databinding.Bindable;
import android.view.View;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.ui.recyclerview.OnItemClickListener;
import com.layer.ui.viewmodel.ItemViewModel;

import java.util.HashSet;
import java.util.Set;

public class ConversationItemViewModel extends ItemViewModel<Conversation> {
    //View Logic
    protected final ConversationItemFormatter mConversationItemFormatter;
    protected Identity mAuthenticatedUser;

    // View Data
    protected Set<Identity> mParticipantsMinusAuthenticatedUser;

    // Listeners
    protected OnItemClickListener mOnItemClickListener;

    public ConversationItemViewModel(ConversationItemFormatter conversationItemFormatter, OnItemClickListener onItemClickListener, Identity authenticatedUser) {
        mConversationItemFormatter = conversationItemFormatter;
        mOnItemClickListener = onItemClickListener;
        mParticipantsMinusAuthenticatedUser = new HashSet<>();
        mAuthenticatedUser = authenticatedUser;
    }

    @Override
    public void setItem(Conversation conversation) {
        super.setItem(conversation);

        mParticipantsMinusAuthenticatedUser.clear();

        mParticipantsMinusAuthenticatedUser.addAll(conversation.getParticipants());
        mParticipantsMinusAuthenticatedUser.remove(mAuthenticatedUser);

        notifyChange();
    }

    // Getters

    @Bindable
    public String getTitle() {
        return mConversationItemFormatter.getConversationTitle(mAuthenticatedUser, getItem(), getItem().getParticipants());
    }

    @Bindable
    public String getSubtitle() {
        return mConversationItemFormatter.getLastMessagePreview(getItem());
    }

    @Bindable
    public String getRightAccessoryText() {
        return mConversationItemFormatter.getTimeStamp(getItem());
    }

    @Bindable
    public boolean isUnread() {
        return getItem().getTotalUnreadMessageCount() > 0;
    }

    @Bindable
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    @Bindable
    public Set<Identity> getParticipantsMinusAuthenticatedUser() {
        return mParticipantsMinusAuthenticatedUser;
    }

    // Actions

    public View.OnClickListener onClickConversation() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(getItem());
                }
            }
        };
    }
}
