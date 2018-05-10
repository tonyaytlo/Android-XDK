package com.layer.xdk.test.performance.benchmark.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.widget.RecyclerView;

import com.layer.sdk.messaging.Conversation;
import com.layer.xdk.test.performance.benchmark.BenchmarkComponentManager;
import com.layer.xdk.ui.DefaultXdkUiComponent;
import com.layer.xdk.ui.conversation.ConversationView;
import com.layer.xdk.ui.conversation.ConversationViewModel;

public class ConversationBenchmarkActivity extends Activity {
    public static final String CONVERSATION_KEY = "extra_conversation";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null) {
            throw new IllegalStateException("Must pass the conversation ID in the intent");
        }

        final CountingIdlingResource messageLoadResource = new CountingIdlingResource("MessageLoad");
        messageLoadResource.increment();
        IdlingRegistry.getInstance().register(messageLoadResource);

        ConversationView conversationView = new ConversationView(this);
        setContentView(conversationView);

        // Get conversation and set on the view
        Uri conversationId = getIntent().getExtras().getParcelable(CONVERSATION_KEY);
        DefaultXdkUiComponent component = BenchmarkComponentManager.INSTANCE.getComponent();

        Conversation conversation = component.layerClient().getConversation(conversationId);
        ConversationViewModel conversationViewModel = component.conversationViewModel();
        conversationViewModel.setConversation(conversation);
        ConversationView.setConversation(conversationView, conversation, component.layerClient(),
                conversationViewModel.getMessageItemsListViewModel());

        conversationViewModel.getMessageItemsListViewModel()
                .getAdapter()
                .registerAdapterDataObserver(
                        new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onItemRangeInserted(int positionStart, int itemCount) {
                                if (itemCount > 0 && !messageLoadResource.isIdleNow()) {
                                    messageLoadResource.decrement();
                                    IdlingRegistry.getInstance().unregister(messageLoadResource);
                                }
                            }
                        });
    }
}
