package com.layer.xdk.ui.message.model;


import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.text.TextMessageModel;
import com.layer.xdk.ui.util.DateFormatter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class MessageModelManagerTest {

    private Context mContext;
    private MessageModelManager mMessageModelManager;

    @Mock
    private LayerClient mLayerClient;

    @Mock
    IdentityFormatter mIdentityFormatter;

    @Mock
    DateFormatter mDateFormatter;

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getTargetContext();
        mMessageModelManager = new MessageModelManager(mContext, mLayerClient, mIdentityFormatter,
                mDateFormatter);
    }

    @Test
    public void testTextMessageModelRegistration() {
        mMessageModelManager.registerModel("TextMessageModel", TextMessageModel.class);
        assertThat(mMessageModelManager.hasModel("TextMessageModel")).isTrue();
    }
}
