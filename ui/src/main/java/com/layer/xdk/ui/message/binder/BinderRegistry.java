package com.layer.xdk.ui.message.binder;

import android.content.Context;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.MessagePartUtils;
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

    public final int VIEW_TYPE_UNKNOWN;
    public final int VIEW_TYPE_HEADER;
    public final int VIEW_TYPE_FOOTER;

    public final int VIEW_TYPE_CARD;
    public final int VIEW_TYPE_STATUS;

    protected LayerClient mLayerClient;

    // XDK Message Type Registry
    protected final MessageModelManager mMessageModelManager;

    public BinderRegistry(@NonNull Context context, LayerClient layerClient) {
        this(context, layerClient, 0, 1, -1);
    }

    public BinderRegistry(@NonNull Context context, @NonNull LayerClient layerClient, final int headerViewType, final int footerViewType, final int unknownViewType) {
        mLayerClient = layerClient;

        if ((headerViewType == footerViewType)
                || (headerViewType == unknownViewType)
                || (footerViewType == unknownViewType)) {
            throw new IllegalArgumentException("Header, Footer and Unknown View Types must be distinct");
        }

        if (unknownViewType > headerViewType || unknownViewType > footerViewType) {
            throw new IllegalArgumentException("Please use a lower integer value than headerViewType or footerViewtype for unknownViewType ");
        }

        VIEW_TYPE_UNKNOWN = unknownViewType;
        VIEW_TYPE_HEADER = headerViewType;
        VIEW_TYPE_FOOTER = footerViewType;
        VIEW_TYPE_STATUS = 1000;
        VIEW_TYPE_CARD = VIEW_TYPE_STATUS + 1;

        mMessageModelManager = new MessageModelManager(context.getApplicationContext(), layerClient);
        initMessageTypeModelRegistry();
    }

    public boolean isLegacyMessageType(Message message) {
        for (MessagePart messagePart : message.getMessageParts()) {
            if (MessagePartUtils.isRoleRoot(messagePart)) return false;
        }
        return true;
    }

    public boolean isStatusMessageType(Message message) {
        String rootMimeType = MessagePartUtils.getRootMimeType(message);
        return StatusMessageModel.MIME_TYPE.equals(rootMimeType)
                || ResponseMessageModel.MIME_TYPE.equals(rootMimeType);
    }

    public int getViewType(Message message) {
        if (isStatusMessageType(message)) {
            return VIEW_TYPE_STATUS;
        }
        if (!isLegacyMessageType(message)) {
            String rootMimeType = MessagePartUtils.getRootMimeType(message);
            if (mMessageModelManager.hasModel(rootMimeType)) {
                return rootMimeType.hashCode();
            }
        }
        return VIEW_TYPE_UNKNOWN;
    }

    //==============================================================================================
    //  XDK Message Binding related methods
    //==============================================================================================

    private void initMessageTypeModelRegistry() {
        mMessageModelManager.registerModel(TextMessageModel.ROOT_MIME_TYPE, TextMessageModel.class);
        mMessageModelManager.registerModel(TextMessageModel.LEGACY_MIME_TYPE, TextMessageModel.class);
        mMessageModelManager.registerModel(ImageMessageModel.ROOT_MIME_TYPE, ImageMessageModel.class);
        mMessageModelManager.registerModel(ImageMessageModel.LEGACY_SINGLE_PART_MIME_TYPES, ImageMessageModel.class);
        mMessageModelManager.registerModel(ImageMessageModel.LEGACY_THREE_PART_MIME_TYPES, ImageMessageModel.class);
        mMessageModelManager.registerModel(LocationMessageModel.ROOT_MIME_TYPE, LocationMessageModel.class);
        mMessageModelManager.registerModel(LocationMessageModel.LEGACY_MIME_TYPE, LocationMessageModel.class);
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
