package com.layer.xdk.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.conversation.ConversationItemFormatter;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.model.MessageModelManager;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.Log;
import com.layer.xdk.ui.message.image.cache.ImageCacheWrapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Concrete implementation of a module to use with the XDK UI. This provides dependencies from the
 * supplied {@link ServiceLocator}.
 *
 * If using your own module instead of this class, be sure to include the
 * {@link XdkUiInternalModule} in your custom module.
 */
@Module(includes = XdkUiInternalModule.class)
public class DefaultXdkUiModule implements XdkUiModule {

    private ServiceLocator mServiceLocator;

    /**
     * Create a module that uses a {@link ServiceLocator} for providing dependencies.
     *
     * @param serviceLocator service locator to use for providing dependencies
     */
    public DefaultXdkUiModule(ServiceLocator serviceLocator) {
        mServiceLocator = serviceLocator;
    }

    @Provides
    @NonNull
    @Override
    public Context provideApplicationContext() {
        Context context = mServiceLocator.getAppContext();
        if (context == null) {
            String message = "A context was requested but the service locator does not supply one";
            if (Log.isLoggable(Log.ERROR)) {
                Log.e(message);
            }
            throw new IllegalStateException(message);
        }
        return context;
    }

    @Provides
    @NonNull
    @Override
    public LayerClient provideLayerClient() {
        LayerClient layerClient = mServiceLocator.getLayerClient();
        if (layerClient == null) {
            String message =
                    "A LayerClient was requested but the service locator does not supply one";
            if (Log.isLoggable(Log.ERROR)) {
                Log.e(message);
            }
            throw new IllegalStateException(message);
        }
        return layerClient;
    }

    @Provides
    @Singleton
    @NonNull
    @Override
    public ImageCacheWrapper provideImageCacheWrapper() {
        return mServiceLocator.getImageCacheWrapper();
    }

    @Provides
    @Singleton
    @NonNull
    @Override
    public IdentityFormatter provideIdentityFormatter() {
        return mServiceLocator.getIdentityFormatter();
    }

    @Provides
    @Singleton
    @NonNull
    @Override
    public DateFormatter provideDateFormatter() {
        return mServiceLocator.getDateFormatter();
    }

    @Provides
    @Singleton
    @NonNull
    @Override
    public ConversationItemFormatter provideConversationItemFormatter() {
        return mServiceLocator.getConversationItemFormatter();
    }

    /**
     * Provides the {@link MessageModelManager} from the {@link ServiceLocator} or creates one with
     * the appropriate dependencies if not defined. This is not defined in the {@link XdkUiModule}
     * interface as instances can be created automatically without a provider due to the annotated
     * constructor. Should a custom instance be created it would be overridden in the custom module.
     *
     * @return the {@link MessageModelManager} of the service locator or a new instance using
     * dependencies from the service locator
     */
    @Provides
    @Singleton
    @NonNull
    public MessageModelManager provideMessageModelManager() {
        if (mServiceLocator.getMessageModelManager() != null) {
            return mServiceLocator.getMessageModelManager();
        }

        if (mServiceLocator.getAppContext() == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("No Context when attempting to create a MessageModelManager");
            }
            throw new IllegalStateException(
                    "No Context when attempting to create a MessageModelManager");
        }
        if (mServiceLocator.getLayerClient() == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("No LayerClient when attempting to create a MessageModelManager");
            }
            throw new IllegalStateException(
                    "No LayerClient when attempting to create a MessageModelManager");
        }

        // Create a new object using dependencies from the service locator since there is no
        // way to automatically create the object via Dagger here.
        return new MessageModelManager(mServiceLocator.getAppContext(),
                mServiceLocator.getLayerClient(),
                mServiceLocator.getIdentityFormatter(),
                mServiceLocator.getDateFormatter(),
                mServiceLocator.getImageCacheWrapper());
    }
}
