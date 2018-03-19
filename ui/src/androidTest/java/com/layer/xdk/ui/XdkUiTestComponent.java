package com.layer.xdk.ui;


import com.layer.xdk.ui.conversationitem.ConversationItemViewModel;
import com.layer.xdk.ui.identity.IdentityItemViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = FakeXdkUiModule.class)
public interface XdkUiTestComponent extends DefaultXdkUiComponent {

    ConversationItemViewModel conversationItemViewModel();
    IdentityItemViewModel identityItemViewModel();
}
