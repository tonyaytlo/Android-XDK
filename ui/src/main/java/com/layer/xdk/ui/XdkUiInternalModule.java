package com.layer.xdk.ui;

import android.support.annotation.NonNull;

import com.layer.xdk.ui.avatar.AvatarViewModel;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.conversationitem.ConversationItemViewModel;
import com.layer.xdk.ui.identity.IdentityItemViewModel;
import com.layer.xdk.ui.message.adapter.viewholder.DefaultMessageModelVHModel;
import com.layer.xdk.ui.message.adapter.viewholder.StatusMessageModelVHModel;
import com.layer.xdk.ui.message.adapter.viewholder.TypingIndicatorVHModel;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.internal.Factory;

/**
 * Internal module to use with the XDK UI. This mostly provides factories to create models for
 * recycler view view holder classes. It also provides a default implementation of an
 * {@link AvatarViewModel}. Normally this class will not be overridden unless further customization
 * is required. Ensure this module or a module that provides equivalent objects is included in any
 * custom component/module setup.
 */
@SuppressWarnings("WeakerAccess")
@Module
public class XdkUiInternalModule {

    /**
     * Create a concrete implementation of {@link AvatarViewModel} using a
     * {@link AvatarViewModelImpl}.
     *
     * @param provider A provider that creates new instances of {@link AvatarViewModelImpl}
     * @return a {@link AvatarViewModelImpl} instance
     */
    @Provides
    @NonNull
    public AvatarViewModel provideAvatarViewModel(Provider<AvatarViewModelImpl> provider) {
        return provider.get();
    }

    /**
     * Create a {@link Factory} that creates {@link ConversationItemViewModel} instances. These
     * should be new instances as these objects are used in a
     * {@link android.support.v7.widget.RecyclerView}. The {@link Provider} is wrapped in a
     * {@link Factory} to help convey that these should be new instances.
     *
     * @param provider A provider that creates new instances of {@link ConversationItemViewModel}
     * @return a factory that produces new instances of {@link ConversationItemViewModel}
     */
    @Provides
    @Singleton
    @NonNull
    public Factory<ConversationItemViewModel> provideConversationItemViewModelFactory(
            final Provider<ConversationItemViewModel> provider) {
        return new Factory<ConversationItemViewModel>() {
            @Override
            public ConversationItemViewModel get() {
                return provider.get();
            }
        };
    }

    /**
     * Create a {@link Factory} that creates {@link IdentityItemViewModel} instances. These
     * should be new instances as these objects are used in a
     * {@link android.support.v7.widget.RecyclerView}. The {@link Provider} is wrapped in a
     * {@link Factory} to help convey that these should be new instances.
     *
     * @param provider A provider that creates new instances of {@link IdentityItemViewModel}
     * @return a factory that produces new instances of {@link IdentityItemViewModel}
     */
    @Provides
    @Singleton
    @NonNull
    public Factory<IdentityItemViewModel> provideIdentityItemViewModelFactory(
            final Provider<IdentityItemViewModel> provider) {
        return new Factory<IdentityItemViewModel>() {
            @Override
            public IdentityItemViewModel get() {
                return provider.get();
            }
        };
    }

    /**
     * Create a {@link Factory} that creates {@link DefaultMessageModelVHModel} instances. These
     * should be new instances as these objects are used in a
     * {@link android.support.v7.widget.RecyclerView}. The {@link Provider} is wrapped in a
     * {@link Factory} to help convey that these should be new instances.
     *
     * @param provider A provider that creates new instances of {@link DefaultMessageModelVHModel}
     * @return a factory that produces new instances of {@link DefaultMessageModelVHModel}
     */
    @Provides
    @Singleton
    @NonNull
    public Factory<DefaultMessageModelVHModel> provideDefaultMessageModelVHModelFactory(
            final Provider<DefaultMessageModelVHModel> provider) {
        return new Factory<DefaultMessageModelVHModel>() {
            @Override
            public DefaultMessageModelVHModel get() {
                return provider.get();
            }
        };
    }

    /**
     * Create a {@link Factory} that creates {@link StatusMessageModelVHModel} instances. These
     * should be new instances as these objects are used in a
     * {@link android.support.v7.widget.RecyclerView}. The {@link Provider} is wrapped in a
     * {@link Factory} to help convey that these should be new instances.
     *
     * @param provider A provider that creates new instances of {@link StatusMessageModelVHModel}
     * @return a factory that produces new instances of {@link StatusMessageModelVHModel}
     */
    @Provides
    @Singleton
    @NonNull
    public Factory<StatusMessageModelVHModel> provideStatusMessageModelVHModelFactory(
            final Provider<StatusMessageModelVHModel> provider) {
        return new Factory<StatusMessageModelVHModel>() {
            @Override
            public StatusMessageModelVHModel get() {
                return provider.get();
            }
        };
    }

    /**
     * Create a {@link Factory} that creates {@link TypingIndicatorVHModel} instances. These
     * should be new instances as these objects are used in a
     * {@link android.support.v7.widget.RecyclerView}. The {@link Provider} is wrapped in a
     * {@link Factory} to help convey that these should be new instances.
     *
     * @param provider A provider that creates new instances of {@link TypingIndicatorVHModel}
     * @return a factory that produces new instances of {@link TypingIndicatorVHModel}
     */
    @Provides
    @Singleton
    @NonNull
    public Factory<TypingIndicatorVHModel> provideTypingIndicatorVHModelFactory(
            final Provider<TypingIndicatorVHModel> provider) {
        return new Factory<TypingIndicatorVHModel>() {
            @Override
            public TypingIndicatorVHModel get() {
                return provider.get();
            }
        };
    }
}
