package com.layer.xdk.ui.message.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.generic.UnhandledMessageModel;
import com.layer.xdk.ui.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageModelManager {
    private final Map<String, Constructor<?>> mIdentifierToConstructorMap;
    private final Map<Set<String>, Constructor<?>> mMimeTypeSetToConstructorMap;

    private Context mApplicationContext;
    private LayerClient mLayerClient;


    public MessageModelManager(Context applicationContext, LayerClient layerClient) {
        mIdentifierToConstructorMap = new HashMap<>();
        mMimeTypeSetToConstructorMap = new HashMap<>();
        mApplicationContext = applicationContext;
        mLayerClient = layerClient;
    }

    public <T extends MessageModel> void registerModel(@NonNull String modelIdentifier, @NonNull Class<T> messageModelClass) {
        try {
            Constructor<?> constructor = messageModelClass.getConstructor(Context.class, LayerClient.class, Message.class);
            mIdentifierToConstructorMap.put(modelIdentifier, constructor);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class does not implement required constructor");
        }
    }

    public <T extends AbstractMessageModel> void registerLegacyModel(@NonNull Set<String> partMimeTypes, @NonNull Class<T> messageModelClass) {
        try {
            Constructor<?> constructor = messageModelClass.getConstructor(Context.class, LayerClient.class, Message.class);
            mMimeTypeSetToConstructorMap.put(partMimeTypes, constructor);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class does not implement required constructor");
        }
    }

    public boolean hasModel(@NonNull String modelIdentifier) {
        return mIdentifierToConstructorMap.containsKey(modelIdentifier);
    }

    public void remove(@NonNull String modelIdentifier) {
        if (mIdentifierToConstructorMap.containsKey(modelIdentifier)) {
            mIdentifierToConstructorMap.remove(modelIdentifier);
        }
    }

    @NonNull
    public MessageModel getNewModel(@NonNull Message message) {
        return getNewModel(getModelIdentifier(message), message);
    }

    @NonNull
    public MessageModel getNewModel(@NonNull String modelIdentifier, @NonNull Message message) {
        try {
            Constructor<? extends MessageModel> constructor =
                    (Constructor<? extends MessageModel>)
                            mIdentifierToConstructorMap.get(modelIdentifier);
            if (constructor == null) {
                return new UnhandledMessageModel(mApplicationContext, mLayerClient, message);
            } else {
                MessageModel model = constructor.newInstance(mApplicationContext, mLayerClient,
                        message);
                model.setMessageModelManager(this);
                return model;
            }
        } catch (IllegalAccessException e) {
            // Handled below
        } catch (InstantiationException e) {
            // Handled below
        } catch (InvocationTargetException e) {
            // Handled below
        }

        if (Log.isLoggable(Log.ERROR)) {
            Log.e("Failed to instantiate a new MessageModel instance. Ensure an appropriate"
                    + " constructor exists.");
        }
        throw new IllegalStateException("Failed to instantiate a new MessageModel instance."
                + " Ensure an appropriate constructor exists.");
    }

    @NonNull
    private static String getModelIdentifier(@NonNull Message message) {
        String rootMimeType = MessagePartUtils.getRootMimeType(message);
        if (rootMimeType == null) {
            // This is a legacy message
            return MessagePartUtils.getLegacyMessageMimeTypes(message);
        }
        return rootMimeType;
    }
}
