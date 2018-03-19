package com.layer.xdk.ui.message.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.LegacyMimeTypes;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.button.ButtonMessageModel;
import com.layer.xdk.ui.message.carousel.CarouselMessageModel;
import com.layer.xdk.ui.message.choice.ChoiceMessageModel;
import com.layer.xdk.ui.message.file.FileMessageModel;
import com.layer.xdk.ui.message.generic.UnhandledMessageModel;
import com.layer.xdk.ui.message.image.ImageMessageModel;
import com.layer.xdk.ui.message.link.LinkMessageModel;
import com.layer.xdk.ui.message.location.LocationMessageModel;
import com.layer.xdk.ui.message.product.ProductMessageModel;
import com.layer.xdk.ui.message.receipt.ReceiptMessageModel;
import com.layer.xdk.ui.message.response.ResponseMessageModel;
import com.layer.xdk.ui.message.status.StatusMessageModel;
import com.layer.xdk.ui.message.text.TextMessageModel;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class MessageModelManager {
    private final Map<String, Constructor<?>> mIdentifierToConstructorMap;

    private final Context mApplicationContext;
    private final LayerClient mLayerClient;
    private final IdentityFormatter mIdentityFormatter;
    private final DateFormatter mDateFormatter;

    @Inject
    public MessageModelManager(Context applicationContext, LayerClient layerClient,
            IdentityFormatter identityFormatter, DateFormatter dateFormatter) {
        mIdentifierToConstructorMap = new HashMap<>();
        mApplicationContext = applicationContext;
        mLayerClient = layerClient;
        mIdentityFormatter = identityFormatter;
        mDateFormatter = dateFormatter;
        registerDefaultModels();
    }

    private void registerDefaultModels() {
        registerModel(TextMessageModel.ROOT_MIME_TYPE, TextMessageModel.class);
        registerModel(LegacyMimeTypes.LEGACY_TEXT_MIME_TYPE, TextMessageModel.class);
        registerModel(ImageMessageModel.ROOT_MIME_TYPE, ImageMessageModel.class);
        registerModel(LegacyMimeTypes.LEGACY_SINGLE_PART_MIME_TYPES, ImageMessageModel.class);
        registerModel(LegacyMimeTypes.LEGACY_THREE_PART_MIME_TYPES, ImageMessageModel.class);
        registerModel(LocationMessageModel.ROOT_MIME_TYPE, LocationMessageModel.class);
        registerModel(LegacyMimeTypes.LEGACY_LOCATION_MIME_TYPE, LocationMessageModel.class);
        registerModel(LinkMessageModel.ROOT_MIME_TYPE, LinkMessageModel.class);
        registerModel(FileMessageModel.ROOT_MIME_TYPE, FileMessageModel.class);
        registerModel(ButtonMessageModel.ROOT_MIME_TYPE, ButtonMessageModel.class);
        registerModel(ChoiceMessageModel.MIME_TYPE, ChoiceMessageModel.class);
        registerModel(CarouselMessageModel.MIME_TYPE, CarouselMessageModel.class);
        registerModel(ProductMessageModel.MIME_TYPE, ProductMessageModel.class);
        registerModel(StatusMessageModel.MIME_TYPE, StatusMessageModel.class);
        registerModel(ReceiptMessageModel.MIME_TYPE, ReceiptMessageModel.class);
        registerModel(ResponseMessageModel.MIME_TYPE, ResponseMessageModel.class);
    }

    public <T extends MessageModel> void registerModel(@NonNull String modelIdentifier, @NonNull Class<T> messageModelClass) {
        try {
            Constructor<?> constructor = messageModelClass.getConstructor(Context.class, LayerClient.class, Message.class);
            mIdentifierToConstructorMap.put(modelIdentifier, constructor);
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
        Throwable exception;
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
                model.setIdentityFormatter(mIdentityFormatter);
                model.setDateFormatter(mDateFormatter);
                return model;
            }
        } catch (IllegalAccessException e) {
            // Handled below
            exception = e;
        } catch (InstantiationException e) {
            // Handled below
            exception = e;
        } catch (InvocationTargetException e) {
            // Handled below
            exception = e;
        }

        if (Log.isLoggable(Log.ERROR)) {
            Log.e("Failed to instantiate a new MessageModel instance. Ensure an appropriate"
                    + " constructor exists.", exception);
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
