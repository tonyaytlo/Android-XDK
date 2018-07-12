package com.layer.xdk.ui.message.action;

import static junit.framework.Assert.fail;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.test.mock.MockContext;

import com.layer.xdk.ui.message.model.MessageModel;

import org.junit.Test;

public class ActionHandlerRegistryTest {

    @Test
    public void testDispatch() {
        ActionHandler mockHandler = mock(ActionHandler.class);
        when(mockHandler.getEvent()).thenReturn("event");
        ActionHandlerRegistry.registerHandler(mockHandler);
        Context context = new MockContext();
        MessageModel mockModel = mock(MessageModel.class);
        ActionHandlerRegistry.dispatchEvent(context, "event", mockModel);

        verify(mockHandler).performAction(eq(context), eq(mockModel));
    }

    @Test
    public void testDispatchNoEventRegistered() {
        ActionHandler mockHandler = mock(ActionHandler.class);
        when(mockHandler.getEvent()).thenReturn("event");
        ActionHandlerRegistry.registerHandler(mockHandler);
        Context context = new MockContext();
        MessageModel mockModel = mock(MessageModel.class);
        try {
            ActionHandlerRegistry.dispatchEvent(context, "Other event", mockModel);
            fail("Expecting UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Ignored
        }

        verify(mockHandler, times(0)).performAction(any(Context.class), any(MessageModel.class));
    }

    @Test
    public void testDispatchMultipleRegistered() {
        ActionHandler mockHandler = mock(ActionHandler.class);
        when(mockHandler.getEvent()).thenReturn("event");
        ActionHandlerRegistry.registerHandler(mockHandler);

        ActionHandler mockHandlerOther = mock(ActionHandler.class);
        when(mockHandlerOther.getEvent()).thenReturn("event-other");
        ActionHandlerRegistry.registerHandler(mockHandlerOther);

        Context context = new MockContext();

        MessageModel mockModel = mock(MessageModel.class);
        ActionHandlerRegistry.dispatchEvent(context, "event-other", mockModel);

        verify(mockHandler, times(0)).performAction(any(Context.class), any(MessageModel.class));
        verify(mockHandlerOther).performAction(eq(context), eq(mockModel));

    }
}