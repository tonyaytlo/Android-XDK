package com.layer.xdk.ui.message.audio;

import android.content.Context;

import com.google.common.truth.Truth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.layer.xdk.test.common.stub.ConversationStub;
import com.layer.xdk.test.common.stub.IdentityStub;
import com.layer.xdk.test.common.stub.LayerClientStub;
import com.layer.xdk.test.common.stub.MessagePartStub;
import com.layer.xdk.test.common.stub.MessageStub;
import com.layer.xdk.ui.R;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.ByteArrayInputStream;
import java.util.HashSet;

@RunWith(RobolectricTestRunner.class)
public class AudioMessageModelTest {

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
    public void testFullSlots() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("artist", "An artist");
        root.addProperty("album", "An album");
        root.addProperty("genre", "A genre");
        root.addProperty("duration", 100);
        root.addProperty("size", 1024);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("An artist");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("01:40");
    }

    @Test
    public void testSlotBNoTitle() {
        JsonObject root = new JsonObject();
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("artist", "An artist");
        root.addProperty("album", "An album");
        root.addProperty("genre", "A genre");
        root.addProperty("duration", 100);
        root.addProperty("size", 1024);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("file.mp3");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("An artist");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("01:40");
    }

    @Test
    public void testSlotBDefault() {
        JsonObject root = new JsonObject();
        root.addProperty("artist", "An artist");
        root.addProperty("album", "An album");
        root.addProperty("genre", "A genre");
        root.addProperty("duration", 100);
        root.addProperty("size", 1024);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo(mContext.getString(R.string.xdk_ui_audio_message_model_default_title));
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("An artist");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("01:40");
    }

    @Test
    public void testSlotCAlbum() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("album", "An album");
        root.addProperty("genre", "A genre");
        root.addProperty("duration", 100);
        root.addProperty("size", 1024);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("An album");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("01:40");
    }

    @Test
    public void testSlotCGenre() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("genre", "A genre");
        root.addProperty("duration", 100);
        root.addProperty("size", 1024);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("A genre");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("01:40");
    }

    @Test
    public void testSlotDSize() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("artist", "An artist");
        root.addProperty("size", 1024);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("An artist");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("1.0 kB");
    }

    @Test
    public void testSlotDPromotion() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("duration", 100);
        root.addProperty("size", 1024);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("01:40");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("1.0 kB");
    }

    @Test
    public void testSlotDPromotionEmptyD() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("duration", 100);

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("01:40");
        Truth.assertThat(audioMessage.getFooter()).isNull();
    }

    @Test
    public void testSlotBDemotedToC() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("file.mp3");
        Truth.assertThat(audioMessage.getFooter()).isNull();
    }


    @Test
    public void testSlotCDemotedToD() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("artist", "An artist");
        root.addProperty("album", "An album");
        root.addProperty("genre", "A genre");

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("An artist");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("An album");
    }

    @Test
    public void testSlotBDemotedToD() {
        JsonObject root = new JsonObject();
        root.addProperty("title", "A title");
        root.addProperty("source_url", "http://www.server.com/file.mp3");
        root.addProperty("artist", "An artist");
        root.addProperty("album", "An album");

        AudioMessageModel audioMessage = createAudioMessage(root.toString());

        Truth.assertThat(audioMessage.getTitle()).isEqualTo("A title");
        Truth.assertThat(audioMessage.getDescription()).isEqualTo("An artist");
        Truth.assertThat(audioMessage.getFooter()).isEqualTo("An album");
    }

    private AudioMessageModel createAudioMessage(String data) {
        MessageStub messageStub = createMessageStub(true);
        MessagePartStub messagePartStub = new MessagePartStub();
        messagePartStub.mMimeType = "application/vnd.layer.audio+json; role=root";
        messagePartStub.mDataStream = new ByteArrayInputStream(data.getBytes());

        messageStub.mMessageParts.add(messagePartStub);

        AudioMessageModel model = new AudioMessageModel(mContext, sLayerClientStub,
                messageStub);
        model.setGson(new Gson());
        model.processPartsFromTreeRoot();
        return model;
    }

    private MessageStub createMessageStub(boolean fromMe) {
        MessageStub messageStub = new MessageStub();
        messageStub.mConversation = sConversationStub;
        messageStub.mSender = fromMe ? sAlice : sBob;
        return messageStub;
    }
}
