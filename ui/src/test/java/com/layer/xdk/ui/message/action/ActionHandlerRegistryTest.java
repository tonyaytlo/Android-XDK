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

import com.google.gson.JsonObject;

import org.junit.Test;

public class ActionHandlerRegistryTest {

    @Test
    public void testDispatch() {
        ActionHandler mockHandler = mock(ActionHandler.class);
        when(mockHandler.getEvent()).thenReturn("event");
        ActionHandlerRegistry.registerHandler(mockHandler);
        Context context = new MockContext();
        JsonObject customData = new JsonObject();
        ActionHandlerRegistry.dispatchEvent(context, "event", customData);

        verify(mockHandler).performAction(eq(context), eq(customData));
    }

    @Test
    public void testDispatchNoEventRegistered() {
        ActionHandler mockHandler = mock(ActionHandler.class);
        when(mockHandler.getEvent()).thenReturn("event");
        ActionHandlerRegistry.registerHandler(mockHandler);
        Context context = new MockContext();
        JsonObject customData = new JsonObject();
        try {
            ActionHandlerRegistry.dispatchEvent(context, "Other event", customData);
            fail("Expecting UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Ignored
        }

        verify(mockHandler, times(0)).performAction(any(Context.class), any(JsonObject.class));
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
        JsonObject customData = new JsonObject();
        ActionHandlerRegistry.dispatchEvent(context, "event-other", customData);

        verify(mockHandler, times(0)).performAction(any(Context.class), any(JsonObject.class));
        verify(mockHandlerOther).performAction(eq(context), eq(customData));

    }
}