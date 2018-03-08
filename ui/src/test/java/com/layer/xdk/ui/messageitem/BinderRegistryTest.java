package com.layer.xdk.ui.messageitem;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;

import android.content.Context;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.message.LegacyMimeTypes;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.button.ButtonMessageModel;
import com.layer.xdk.ui.message.carousel.CarouselMessageModel;
import com.layer.xdk.ui.message.choice.ChoiceMessageModel;
import com.layer.xdk.ui.message.file.FileMessageModel;
import com.layer.xdk.ui.message.image.ImageMessageModel;
import com.layer.xdk.ui.message.link.LinkMessageModel;
import com.layer.xdk.ui.message.location.LocationMessageModel;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.product.ProductMessageModel;
import com.layer.xdk.ui.message.receipt.ReceiptMessageModel;
import com.layer.xdk.ui.message.response.ResponseMessageModel;
import com.layer.xdk.ui.message.status.StatusMessageModel;
import com.layer.xdk.ui.message.text.TextMessageModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class BinderRegistryTest {

    @Mock
    LayerClient layerClient;

    @Mock
    Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDefaultModels() {
        BinderRegistry binderRegistry = new BinderRegistry(context, layerClient);
        assertThat(binderRegistry.hasModel(TextMessageModel.ROOT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(LegacyMimeTypes.LEGACY_TEXT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(ImageMessageModel.ROOT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(LegacyMimeTypes.LEGACY_SINGLE_PART_MIME_TYPES)).isTrue();
        assertThat(binderRegistry.hasModel(LegacyMimeTypes.LEGACY_THREE_PART_MIME_TYPES)).isTrue();
        assertThat(binderRegistry.hasModel(LocationMessageModel.ROOT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(LegacyMimeTypes.LEGACY_LOCATION_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(LinkMessageModel.ROOT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(FileMessageModel.ROOT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(ButtonMessageModel.ROOT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(ChoiceMessageModel.MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(CarouselMessageModel.MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(ProductMessageModel.MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(StatusMessageModel.MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(ReceiptMessageModel.MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(ResponseMessageModel.MIME_TYPE)).isTrue();
    }

    @Test
    public void testModelRegistration() {
        BinderRegistry binderRegistry = new BinderRegistry(context, layerClient);
        MessageModel mockModel = mock(MessageModel.class);
        String modelIdentifier = "SampleIdentifier";
        binderRegistry.registerModel(modelIdentifier, mockModel.getClass());

        assertThat(binderRegistry.hasModel(modelIdentifier)).isTrue();
    }

    @Test
    public void testModelRemoval() {
        BinderRegistry binderRegistry = new BinderRegistry(context, layerClient);
        MessageModel mockModel = mock(MessageModel.class);
        String modelIdentifier = "SampleIdentifier";
        binderRegistry.registerModel(modelIdentifier, mockModel.getClass());

        assertThat(binderRegistry.hasModel(TextMessageModel.ROOT_MIME_TYPE)).isTrue();
        assertThat(binderRegistry.hasModel(modelIdentifier)).isTrue();

        binderRegistry.remove(TextMessageModel.ROOT_MIME_TYPE);
        binderRegistry.remove(modelIdentifier);

        assertThat(binderRegistry.hasModel(TextMessageModel.ROOT_MIME_TYPE)).isFalse();
        assertThat(binderRegistry.hasModel(modelIdentifier)).isFalse();

        // Ensure a default is still there
        assertThat(binderRegistry.hasModel(LegacyMimeTypes.LEGACY_TEXT_MIME_TYPE)).isTrue();
    }
}
