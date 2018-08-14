package com.layer.xdk.ui.message.image;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

import com.google.gson.Gson;
import com.layer.xdk.test.common.stub.ConversationStub;
import com.layer.xdk.test.common.stub.IdentityStub;
import com.layer.xdk.test.common.stub.LayerClientStub;
import com.layer.xdk.test.common.stub.MessagePartStub;
import com.layer.xdk.test.common.stub.MessageStub;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashSet;

@RunWith(RobolectricTestRunner.class)
public class ImageMessageModelTest {

    private static LayerClientStub sLayerClientStub;
    private static IdentityStub sAlice;
    private static IdentityStub sBob;
    private static ConversationStub sConversationStub;
    private Context mContext;

    @BeforeClass
    public static void setUpGlobal() {
        sAlice = new IdentityStub();
        sBob = new IdentityStub();
        sLayerClientStub = new LayerClientStub();
        sLayerClientStub.mAuthenticatedUser = sAlice;
        sConversationStub = new ConversationStub();
        sConversationStub.mParticipants = new HashSet<>(2);
        sConversationStub.mParticipants.add(sAlice);
        sConversationStub.mParticipants.add(sBob);
    }

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void testSourcePartNoWidthAndHeight() {
        // Setup
        MessageStub messageStub = createMessageStub(true);
        MessagePartStub mainPartStub = new MessagePartStub();
        mainPartStub.mMimeType = "application/vnd.layer.image+json; role=root";
        mainPartStub.mData = ("{\"artist\":\"TestArtist\",\"title\":\"TestTitle\","
                + "\"subtitle\":\"TestSubtitle\"}").getBytes();
        MessagePartStub sourcePartStub = new MessagePartStub();
        sourcePartStub.mMimeType = "image/png; role=source; parent-node-id=" + mainPartStub.getId().getLastPathSegment();

        messageStub.mMessageParts.add(mainPartStub);
        messageStub.mMessageParts.add(sourcePartStub);

        // Execute
        ImageMessageModel imageMessageModel = new ImageMessageModel(mContext, sLayerClientStub,
                messageStub);
        imageMessageModel.setGson(new Gson());
        imageMessageModel.processPartsFromTreeRoot();

        // Verify
        assertThat(imageMessageModel.getSourceRequestParameters()).isNotNull();
        assertThat(imageMessageModel.getSourceRequestParameters().getTargetWidth()).isEqualTo(0);
        assertThat(imageMessageModel.getSourceRequestParameters().getTargetHeight()).isEqualTo(0);
    }

    private MessageStub createMessageStub(boolean fromMe) {
        MessageStub messageStub = new MessageStub();
        messageStub.mConversation = sConversationStub;
        messageStub.mSender = fromMe ? sAlice : sBob;
        return messageStub;
    }
}
