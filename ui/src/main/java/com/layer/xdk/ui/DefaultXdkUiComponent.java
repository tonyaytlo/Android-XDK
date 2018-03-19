package com.layer.xdk.ui;

import android.content.Context;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.conversationitem.ConversationItemFormatter;
import com.layer.xdk.ui.conversationitem.DefaultConversationItemFormatter;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.message.model.MessageModelManager;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.DateFormatterImpl;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Default Dagger component that uses the default XDK Dagger module. This allows access to
 * additional, non-critical dependencies that were provided by the service locator or were created
 * due to the service locator not providing an implementation.
 */
@Singleton
@Component(modules = {DefaultXdkUiModule.class})
public interface DefaultXdkUiComponent extends XdkUiComponent {

    Context applicationContext();
    LayerClient layerClient();
    ImageCacheWrapper imageCacheWrapper();
    IdentityFormatter identityFormatter();
    IdentityFormatterImpl identityFormatterImpl();
    DateFormatter dateFormatter();
    DateFormatterImpl dateFormatterImpl();
    ConversationItemFormatter conversationItemFormatter();
    DefaultConversationItemFormatter defaultConversationItemFormatter();
    MessageModelManager messageModelManager();
}
