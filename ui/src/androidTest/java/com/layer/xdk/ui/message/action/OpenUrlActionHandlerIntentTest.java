package com.layer.xdk.ui.message.action;

import static junit.framework.Assert.fail;

import android.content.ActivityNotFoundException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.layer.xdk.ui.mock.MockLayerClient;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class OpenUrlActionHandlerIntentTest {

    @Test
    public void testInvalidIntent() {
        OpenUrlActionHandler handler = new OpenUrlActionHandler(new MockLayerClient(), null);

        JsonObject data = new JsonObject();
        data.addProperty("url", "Incorrect url formatting www.google.com");
        try {
            handler.performAction(InstrumentationRegistry.getContext(), data);
        } catch (ActivityNotFoundException e) {
            fail("No guard against missing activity");
        }
    }
}