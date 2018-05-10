package com.layer.xdk.ui.conversation.adapter;

import static com.google.common.truth.Truth.assertThat;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.test.common.stub.ConversationStub;
import com.layer.xdk.test.common.stub.IdentityStub;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class ConversationItemModelTest {

    @Test
    public void testGetParticipantsMinusAuthenticatedUser() {
        // Setup
        IdentityStub alice = new IdentityStub();
        IdentityStub bob = new IdentityStub();
        IdentityStub carl = new IdentityStub();
        ConversationStub conversation = new ConversationStub();
        conversation.mParticipants = new HashSet<>(3);
        conversation.mParticipants.add(alice);
        conversation.mParticipants.add(bob);
        conversation.mParticipants.add(carl);

        // Test
        ConversationItemModel itemModel = new ConversationItemModel(conversation, null, bob);
        Set<Identity> participants = itemModel.getParticipantsMinusAuthenticatedUser();

        // Verify
        assertThat(participants).containsExactly(alice, carl);
    }

    @Test
    public void testGetParticipants() {
        // Setup
        IdentityStub alice = new IdentityStub();
        IdentityStub bob = new IdentityStub();
        IdentityStub carl = new IdentityStub();
        ConversationStub conversation = new ConversationStub();
        conversation.mParticipants = new HashSet<>(3);
        conversation.mParticipants.add(alice);
        conversation.mParticipants.add(bob);
        conversation.mParticipants.add(carl);

        // Test
        ConversationItemModel itemModel = new ConversationItemModel(conversation, null, bob);
        Set<Identity> participants = itemModel.getParticipants();

        // Verify
        assertThat(participants).containsExactly(alice, bob, carl);
    }
}