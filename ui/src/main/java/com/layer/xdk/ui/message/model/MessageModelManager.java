package com.layer.xdk.ui.message.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;

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

    @Nullable
    public <T extends MessageModel> T getNewModel(@NonNull String modelIdentifier, @NonNull Message message) {
        try {
            Constructor<T> constructor;
            if (mIdentifierToConstructorMap.containsKey(modelIdentifier)) {
                constructor = (Constructor<T>) mIdentifierToConstructorMap.get(modelIdentifier);
                T model = constructor.newInstance(mApplicationContext, mLayerClient, message);
                model.setMessageModelManager(this);
                return model;
            }
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        } catch (InvocationTargetException e) {
        }

        return null;
    }

    @Nullable
    // TODO AND-1242 Extend a legacy model? Depends if we end up with a shared one or not
    public <T extends AbstractMessageModel> T getNewLegacyModel(@NonNull Set<String> partMimeTypes, @NonNull Message message) {
        try {
            Constructor<T> constructor;
            if (mMimeTypeSetToConstructorMap.containsKey(partMimeTypes)) {
                constructor = (Constructor<T>) mMimeTypeSetToConstructorMap.get(partMimeTypes);
                return constructor.newInstance(mApplicationContext, mLayerClient, message);
            }
        } catch (IllegalAccessException e) {
        } catch (InstantiationException e) {
        } catch (InvocationTargetException e) {
        }

        return null;
    }
}
