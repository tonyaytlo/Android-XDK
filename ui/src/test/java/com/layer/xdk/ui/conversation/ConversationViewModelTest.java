package com.layer.xdk.ui.conversation;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.databinding.Observable;

import com.layer.xdk.test.common.stub.ConversationStub;
import com.layer.xdk.test.common.stub.LayerClientStub;
import com.layer.xdk.ui.message.MessageItemsListViewModel;

import org.junit.Test;

public class ConversationViewModelTest {

    @Test
    public void testConversationChangedNotify() {
        // Setup
        ConversationViewModel conversationViewModel = new ConversationViewModel(
                new LayerClientStub(), mock(MessageItemsListViewModel.class));
        Observable.OnPropertyChangedCallback mockCallback = mock(
                Observable.OnPropertyChangedCallback.class);
        conversationViewModel.addOnPropertyChangedCallback(mockCallback);

        // Test
        conversationViewModel.setConversation(new ConversationStub());

        // Verify
        verify(mockCallback).onPropertyChanged(eq(conversationViewModel), anyInt());
    }

}