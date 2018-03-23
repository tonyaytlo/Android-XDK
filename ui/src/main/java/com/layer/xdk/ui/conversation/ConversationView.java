package com.layer.xdk.ui.conversation;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.typingindicator.TypingIndicatorLayout;
import com.layer.xdk.ui.composebar.ComposeBar;
import com.layer.xdk.ui.databinding.XdkUiConversationViewBinding;
import com.layer.xdk.ui.message.MessageItemsListView;
import com.layer.xdk.ui.message.MessageItemsListViewModel;
import com.layer.xdk.ui.typingindicator.BubbleTypingIndicatorFactory;

import java.util.Set;

public class ConversationView extends ConstraintLayout {

    private MessageItemsListView mMessageItemListView;
    private ComposeBar mComposeBar;
    private TypingIndicatorLayout mTypingIndicator;

    private XdkUiConversationViewBinding mBinding;

    public ConversationView(Context context) {
        this(context, null);
    }

    public ConversationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConversationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBinding = XdkUiConversationViewBinding.inflate(LayoutInflater.from(context), this, true);

        mMessageItemListView = mBinding.messagesList;
        mComposeBar = mBinding.composeBar;

        mTypingIndicator = new TypingIndicatorLayout(context);
        mTypingIndicator.setTypingIndicatorFactory(new BubbleTypingIndicatorFactory());
        mTypingIndicator.setTypingActivityListener(new TypingIndicatorLayout.TypingActivityListener() {
            @Override
            public void onTypingActivityChange(TypingIndicatorLayout typingIndicator, boolean active, Set<Identity> users) {
              mMessageItemListView.setTypingIndicatorLayout(active ? typingIndicator : null, users);
            }
        });
    }

    @BindingAdapter(value = {"app:conversation", "app:layerClient", "app:messageItemsListViewModel"})
    public static void setConversation(ConversationView view, Conversation conversation,
                                       LayerClient layerClient, MessageItemsListViewModel viewModel) {
        view.mMessageItemListView.setConversation(layerClient, conversation);

        view.mBinding.setViewModel(viewModel);
        view.mBinding.executePendingBindings();

        view.mComposeBar.setConversation(layerClient, conversation);
        view.mTypingIndicator.setConversation(layerClient, conversation);
    }

    public MessageItemsListView getMessageItemListView() {
        return mMessageItemListView;
    }

    public ComposeBar getComposeBar() {
        return mComposeBar;
    }

    public TypingIndicatorLayout getTypingIndicator() {
        return mTypingIndicator;
    }

    public void setTypingIndicator(TypingIndicatorLayout typingIndicator) {
        mTypingIndicator = typingIndicator;
    }
}
