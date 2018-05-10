package com.layer.xdk.ui.message.adapter;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import android.test.mock.MockContext;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.test.common.stub.ConversationStub;
import com.layer.xdk.test.common.stub.IdentityStub;
import com.layer.xdk.test.common.stub.LayerClientStub;
import com.layer.xdk.test.common.stub.MessageStub;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.text.TextMessageModel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class GroupingCalculatorTest {

    private static LayerClientStub sLayerClientStub;
    private static Context sContext;
    private static IdentityStub sAlice;
    private static IdentityStub sBob;
    private static IdentityStub sCarl;
    private ConversationStub mConversationStub;
    private GroupingCalculator mGroupingCalculator;

    @BeforeClass
    public static void setUpGlobal() {
        sContext = new MockContext();
        sAlice = new IdentityStub();
        sBob = new IdentityStub();
        sCarl = new IdentityStub();
        sLayerClientStub = new LayerClientStub();
        sLayerClientStub.mAuthenticatedUser = sAlice;
    }

    @Before
    public void setUp() {
        mConversationStub = new ConversationStub();
        mConversationStub.mParticipants = new HashSet<>(3);
        mConversationStub.mParticipants.add(sAlice);
        mConversationStub.mParticipants.add(sBob);
        mConversationStub.mParticipants.add(sCarl);
        mGroupingCalculator = new GroupingCalculator();
    }

    @Test
    public void testOldestMessage_without() {
        // Setup
        MessageStub first = new MessageStub();
        first.mConversation = mConversationStub;
        first.mSender = sAlice;
        first.mReceivedAt = new Date(0);
        MessageStub second = new MessageStub();
        second.mConversation = mConversationStub;
        second.mSender = sBob;
        second.mReceivedAt = new Date(1);
        MessageStub third = new MessageStub();
        third.mConversation = mConversationStub;
        third.mSender = sAlice;
        third.mReceivedAt = new Date(2);

        mConversationStub.mHistoricSyncStatus = Conversation.HistoricSyncStatus.MORE_AVAILABLE;

        List<MessageModel> models = createMessageModels(first, second, third);

        // Execute
        mGroupingCalculator.calculateGrouping(models);

        // Verify
        for (MessageModel model : models) {
            assertThat(model.getGrouping()).doesNotContain(MessageGrouping.OLDEST_MESSAGE);
        }
    }
    
    @Test
    public void testOldestMessage_with() {
        // Setup
        MessageStub first = new MessageStub();
        first.mConversation = mConversationStub;
        first.mSender = sAlice;
        first.mReceivedAt = new Date(0);
        MessageStub second = new MessageStub();
        second.mConversation = mConversationStub;
        second.mSender = sBob;
        second.mReceivedAt = new Date(1);
        MessageStub third = new MessageStub();
        third.mConversation = mConversationStub;
        third.mSender = sAlice;
        third.mReceivedAt = new Date(2);

        mConversationStub.mHistoricSyncStatus = Conversation.HistoricSyncStatus.NO_MORE_AVAILABLE;

        List<MessageModel> models = createMessageModels(first, second, third);

        // Execute
        mGroupingCalculator.calculateGrouping(models);

        // Verify
        assertThat(models.get(2).getGrouping()).contains(MessageGrouping.OLDEST_MESSAGE);
        assertThat(models.get(1).getGrouping()).doesNotContain(MessageGrouping.OLDEST_MESSAGE);
        assertThat(models.get(0).getGrouping()).doesNotContain(MessageGrouping.OLDEST_MESSAGE);
    }

    @Test
    public void testNewestMessage() {
        // Setup
        MessageStub first = new MessageStub();
        first.mConversation = mConversationStub;
        first.mSender = sAlice;
        first.mReceivedAt = new Date(0);
        MessageStub second = new MessageStub();
        second.mConversation = mConversationStub;
        second.mSender = sBob;
        second.mReceivedAt = new Date(1);
        MessageStub third = new MessageStub();
        third.mConversation = mConversationStub;
        third.mSender = sAlice;
        third.mReceivedAt = new Date(2);

        mConversationStub.mHistoricSyncStatus = Conversation.HistoricSyncStatus.MORE_AVAILABLE;

        List<MessageModel> models = createMessageModels(first, second, third);

        // Execute
        mGroupingCalculator.calculateGrouping(models);

        // Verify
        assertThat(models.get(2).getGrouping()).doesNotContain(MessageGrouping.NEWEST_MESSAGE);
        assertThat(models.get(1).getGrouping()).doesNotContain(MessageGrouping.NEWEST_MESSAGE);
        assertThat(models.get(0).getGrouping()).contains(MessageGrouping.NEWEST_MESSAGE);
    }

    @Test
    public void testSingleMessage_moreAvailable() {
        // Setup
        MessageStub first = new MessageStub();
        first.mConversation = mConversationStub;
        first.mSender = sAlice;
        first.mReceivedAt = new Date(0);

        mConversationStub.mHistoricSyncStatus = Conversation.HistoricSyncStatus.MORE_AVAILABLE;

        List<MessageModel> models = createMessageModels(first);

        // Execute
        mGroupingCalculator.calculateGrouping(models);

        // Verify
        assertThat(models.get(0).getGrouping())
                .containsExactly(
                        MessageGrouping.NEWEST_MESSAGE,
                        MessageGrouping.GROUP_START,
                        MessageGrouping.SUB_GROUP_END,
                        MessageGrouping.SUB_GROUP_START);
    }

    @Test
    public void testSingleMessage_noMoreAvailable() {
        // Setup
        MessageStub first = new MessageStub();
        first.mConversation = mConversationStub;
        first.mSender = sAlice;
        first.mReceivedAt = new Date(0);

        mConversationStub.mHistoricSyncStatus = Conversation.HistoricSyncStatus.NO_MORE_AVAILABLE;

        List<MessageModel> models = createMessageModels(first);

        // Execute
        mGroupingCalculator.calculateGrouping(models);

        // Verify
        assertThat(models.get(0).getGrouping())
                .containsExactly(
                        MessageGrouping.NEWEST_MESSAGE,
                        MessageGrouping.OLDEST_MESSAGE,
                        MessageGrouping.GROUP_START,
                        MessageGrouping.SUB_GROUP_END,
                        MessageGrouping.SUB_GROUP_START);
    }


    private MessageModel createMessageModel(Message message) {
        return new TextMessageModel(sContext, sLayerClientStub, message);
    }

    private List<MessageModel> createMessageModels(Message... messages) {
        // Add in reverse order as that's how the calculator expects them
        List<MessageModel> models = new ArrayList<>(messages.length);
        for (Message message : messages) {
            models.add(0, createMessageModel(message));
        }
        return models;
    }

}