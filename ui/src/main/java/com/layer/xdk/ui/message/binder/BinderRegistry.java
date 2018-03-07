package com.layer.xdk.ui.message.binder;

import android.content.Context;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.message.LegacyMimeTypes;
import com.layer.xdk.ui.message.button.ButtonMessageModel;
import com.layer.xdk.ui.message.carousel.CarouselMessageModel;
import com.layer.xdk.ui.message.choice.ChoiceMessageModel;
import com.layer.xdk.ui.message.file.FileMessageModel;
import com.layer.xdk.ui.message.image.ImageMessageModel;
import com.layer.xdk.ui.message.link.LinkMessageModel;
import com.layer.xdk.ui.message.location.LocationMessageModel;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.model.MessageModelManager;
import com.layer.xdk.ui.message.product.ProductMessageModel;
import com.layer.xdk.ui.message.receipt.ReceiptMessageModel;
import com.layer.xdk.ui.message.response.ResponseMessageModel;
import com.layer.xdk.ui.message.status.StatusMessageModel;
import com.layer.xdk.ui.message.text.TextMessageModel;

public class BinderRegistry {

    protected LayerClient mLayerClient;

    // XDK Message Type Registry
    protected final MessageModelManager mMessageModelManager;

    public BinderRegistry(@NonNull Context context, LayerClient layerClient) {
        mLayerClient = layerClient;

        mMessageModelManager = new MessageModelManager(context.getApplicationContext(), layerClient);
        initMessageTypeModelRegistry();
    }

    //==============================================================================================
    //  XDK Message Binding related methods
    //==============================================================================================

    private void initMessageTypeModelRegistry() {
        mMessageModelManager.registerModel(TextMessageModel.ROOT_MIME_TYPE, TextMessageModel.class);
        mMessageModelManager.registerModel(LegacyMimeTypes.LEGACY_TEXT_MIME_TYPE, TextMessageModel.class);
        mMessageModelManager.registerModel(ImageMessageModel.ROOT_MIME_TYPE, ImageMessageModel.class);
        mMessageModelManager.registerModel(LegacyMimeTypes.LEGACY_SINGLE_PART_MIME_TYPES, ImageMessageModel.class);
        mMessageModelManager.registerModel(LegacyMimeTypes.LEGACY_THREE_PART_MIME_TYPES, ImageMessageModel.class);
        mMessageModelManager.registerModel(LocationMessageModel.ROOT_MIME_TYPE, LocationMessageModel.class);
        mMessageModelManager.registerModel(LegacyMimeTypes.LEGACY_LOCATION_MIME_TYPE, LocationMessageModel.class);
        mMessageModelManager.registerModel(LinkMessageModel.ROOT_MIME_TYPE, LinkMessageModel.class);
        mMessageModelManager.registerModel(FileMessageModel.ROOT_MIME_TYPE, FileMessageModel.class);
        mMessageModelManager.registerModel(ButtonMessageModel.ROOT_MIME_TYPE, ButtonMessageModel.class);
        mMessageModelManager.registerModel(ChoiceMessageModel.MIME_TYPE, ChoiceMessageModel.class);
        mMessageModelManager.registerModel(CarouselMessageModel.MIME_TYPE, CarouselMessageModel.class);
        mMessageModelManager.registerModel(ProductMessageModel.MIME_TYPE, ProductMessageModel.class);
        mMessageModelManager.registerModel(StatusMessageModel.MIME_TYPE, StatusMessageModel.class);
        mMessageModelManager.registerModel(ReceiptMessageModel.MIME_TYPE, ReceiptMessageModel.class);
        mMessageModelManager.registerModel(ResponseMessageModel.MIME_TYPE, ResponseMessageModel.class);
    }

    @SuppressWarnings("unused")
    public <T extends MessageModel> void registerModel(@NonNull String modelIdentifier, @NonNull Class<T> messageModelClass) {
        mMessageModelManager.registerModel(modelIdentifier, messageModelClass);
    }

    @SuppressWarnings("unused")
    public boolean hasModel(@NonNull String modelIdentifier) {
        return mMessageModelManager.hasModel(modelIdentifier);
    }

    public void remove(@NonNull String modelIdentifier) {
        mMessageModelManager.remove(modelIdentifier);
    }

    public MessageModelManager getMessageModelManager() {
        return mMessageModelManager;
    }
}
