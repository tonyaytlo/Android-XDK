package com.layer.xdk.ui.message.text;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

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
public class TextMessageModelTest {

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

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testValidParse() {
        // Setup
        MessageStub messageStub = createMessageStub(true);
        MessagePartStub messagePartStub = new MessagePartStub();
        messagePartStub.mMimeType = "application/vnd.layer.text+json; role=root";
        messagePartStub.mData = ("{\"text\":\"hi\",\"subtitle\":\"To you\",\"author\":\"Me\",\"title\":\"Greetings\"}").getBytes();

        messageStub.mMessageParts.add(messagePartStub);

        // Execute
        TextMessageModel textMessageModel = new TextMessageModel(mContext, sLayerClientStub,
                messageStub);
        textMessageModel.processPartsFromTreeRoot();

        // Verify
        assertThat(textMessageModel.getTitle().contentEquals("Greetings")).isTrue();
        assertThat(textMessageModel.getFooter().contentEquals("Me")).isTrue();
        assertThat(textMessageModel.getDescription().contentEquals("To you")).isTrue();
        assertThat("hi".contentEquals(textMessageModel.getText())).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testValidParse_Legacy() {
        // Setup
        MessageStub messageStub = createMessageStub(true);
        MessagePartStub messagePartStub = new MessagePartStub();
        messagePartStub.mMimeType = "text/plain";
        messagePartStub.mData = ("hi").getBytes();

        messageStub.mMessageParts.add(messagePartStub);

        // Execute
        TextMessageModel textMessageModel = new TextMessageModel(mContext, sLayerClientStub,
                messageStub);
        textMessageModel.processPartsFromTreeRoot();

        // Verify
        assertThat("hi".contentEquals(textMessageModel.getText())).isTrue();
        assertThat(textMessageModel.getTitle()).isNull();
        assertThat(textMessageModel.getFooter()).isNull();
        assertThat(textMessageModel.getDescription()).isNull();
    }

    @Test
    public void testSenderColors() {
        // Setup
        MessageStub aliceMessage = createMessageStub(true);
        MessagePartStub aliceMessagePart = new MessagePartStub();
        aliceMessagePart.mMimeType = "application/vnd.layer.text+json; role=root";
        aliceMessagePart.mData = ("{\"text\":\"hi bob\"}").getBytes();

        aliceMessage.mMessageParts.add(aliceMessagePart);

        MessageStub bobMessage = createMessageStub(false);
        MessagePartStub bobMessagePart = new MessagePartStub();
        bobMessagePart.mMimeType = "application/vnd.layer.text+json; role=root";
        bobMessagePart.mData = ("{\"text\":\"hi alice\"}").getBytes();

        bobMessage.mMessageParts.add(bobMessagePart);

        // Execute
        TextMessageModel myMessageModel = new TextMessageModel(mContext, sLayerClientStub,
                aliceMessage);
        myMessageModel.processPartsFromTreeRoot();
        TextMessageModel theirMessageModel = new TextMessageModel(mContext, sLayerClientStub,
                bobMessage);
        theirMessageModel.processPartsFromTreeRoot();

        // Verify
        assertThat(myMessageModel.getBackgroundColor()).isNotEqualTo(theirMessageModel.getBackgroundColor());
        assertThat(myMessageModel.getTextColor()).isNotEqualTo(theirMessageModel.getTextColor());
        assertThat(myMessageModel.getTextColorLink()).isNotEqualTo(theirMessageModel.getTextColorLink());
    }

    private MessageStub createMessageStub(boolean fromMe) {
        MessageStub messageStub = new MessageStub();
        messageStub.mConversation = sConversationStub;
        messageStub.mSender = fromMe ? sAlice : sBob;
        return messageStub;
    }

}