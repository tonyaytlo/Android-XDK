package com.layer.xdk.test.performance;

import com.layer.xdk.ui.DefaultXdkUiModule;
import com.layer.xdk.ui.conversation.adapter.ConversationItemsAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DefaultXdkUiModule.class)
public interface PerformanceTestComponent {

    ConversationItemsAdapter conversationItemsAdapter();
}
