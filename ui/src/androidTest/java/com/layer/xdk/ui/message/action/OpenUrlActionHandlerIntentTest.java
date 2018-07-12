package com.layer.xdk.ui.message.action;

import static junit.framework.Assert.fail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.ActivityNotFoundException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.mock.MockLayerClient;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class OpenUrlActionHandlerIntentTest {

    @Test
    public void testInvalidIntent() {
        OpenUrlActionHandler handler = new OpenUrlActionHandler(new MockLayerClient(), null);

        MessageModel model = mock(MessageModel.class);
        JsonObject data = new JsonObject();
        data.addProperty("url", "Incorrect url formatting www.google.com");
        when(model.getActionData()).thenReturn(data);
        try {
            handler.performAction(InstrumentationRegistry.getContext(), model);
        } catch (ActivityNotFoundException e) {
            fail("No guard against missing activity");
        }
    }
}