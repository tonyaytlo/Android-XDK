package com.layer.xdk.ui.message.adapter;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.xdk.test.common.stub.AnnouncementStub;
import com.layer.xdk.test.common.stub.ConversationStub;
import com.layer.xdk.test.common.stub.MessagePartStub;
import com.layer.xdk.ui.message.model.MessageModelManager;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

public class MessageModelDataSourceTest {

    @Test
    public void testReceiveAnnouncementChangeNotInvalidated() {
        LayerClient mockLayerClient = mock(LayerClient.class);
        ConversationStub conversationStub = new ConversationStub();
        MessageModelManager mockMessageModelManager = mock(MessageModelManager.class);
        GroupingCalculator mockGroupingCalculator = mock(GroupingCalculator.class);

        MessageModelDataSource messageModelDataSource = new MessageModelDataSource(mockLayerClient,
                conversationStub,
                null,
                mockMessageModelManager,
                mockGroupingCalculator);

        // Get the change listener
        ArgumentCaptor<LayerChangeEventListener> argument = ArgumentCaptor.forClass(LayerChangeEventListener.class);
        verify(mockLayerClient).registerEventListener(argument.capture());

        // Create a change for an announcement and pass it to the listener
        MessagePartStub messagePartStub = new MessagePartStub();
        AnnouncementStub announcementStub = new AnnouncementStub();
        messagePartStub.mMessage = announcementStub;
        announcementStub.mMessageParts.add(messagePartStub);

        LayerChange insertChange = new LayerChange(LayerChange.Type.INSERT, messagePartStub, null,
                null, null);
        argument.getValue().onChangeEvent(new LayerChangeEvent(mockLayerClient, Collections.singletonList(insertChange)));

        // Ensure it is not invalidated
        assertThat(messageModelDataSource.isInvalid()).isFalse();
    }
}
