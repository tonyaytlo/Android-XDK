package com.layer.ui.conversationitem;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.view.View;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;

import java.util.HashSet;
import java.util.Set;

public class ConversationItemViewModel extends BaseObservable {
    //View Logic
    protected final ConversationItemFormatter mConversationItemFormatter;
    protected Identity mAuthenticatedUser;

    // View Data
    public Conversation mConversation;
    protected Set<Identity> mParticipantsMinusAuthenticatedUser;

    // Listeners
    protected OnConversationItemClickListener mOnConversationItemClickListener;


    public ConversationItemViewModel(ConversationItemFormatter conversationItemFormatter, OnConversationItemClickListener onConversationItemClickListener) {
        mConversationItemFormatter = conversationItemFormatter;
        mOnConversationItemClickListener = onConversationItemClickListener;
        mParticipantsMinusAuthenticatedUser = new HashSet<>();
    }

    public void setConversation(@NonNull Conversation conversation, @NonNull Identity authenticatedUser) {
        mConversation = conversation;
        mParticipantsMinusAuthenticatedUser.clear();

        mParticipantsMinusAuthenticatedUser.addAll(conversation.getParticipants());
        mParticipantsMinusAuthenticatedUser.remove(authenticatedUser);
        mAuthenticatedUser = authenticatedUser;

        notifyChange();
    }

    // Getters

    @Bindable
    public Conversation getConversation() {
        return mConversation;
    }

    @Bindable
    public String getTitle() {
        return mConversationItemFormatter.getConversationTitle(mAuthenticatedUser, mConversation, mConversation.getParticipants());
    }

    @Bindable
    public String getSubtitle() {
        return mConversationItemFormatter.getLastMessagePreview(mConversation);
    }

    @Bindable
    public String getRightAccessoryText() {
        return mConversationItemFormatter.getTimeStamp(mConversation);
    }

    @Bindable
    public boolean isUnread() {
        return mConversation.getTotalUnreadMessageCount() > 0;
    }

    @Bindable
    public OnConversationItemClickListener getOnConversationItemClickListener() {
        return mOnConversationItemClickListener;
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
                if (mOnConversationItemClickListener != null) {
                    mOnConversationItemClickListener.onConversationClick(mConversation);
                }
            }
        };
    }
}
