package com.layer.xdk.ui.stub;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.listeners.LayerConnectionListener;
import com.layer.sdk.listeners.LayerObjectExceptionListener;
import com.layer.sdk.listeners.LayerPolicyListener;
import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.listeners.LayerSyncListener;
import com.layer.sdk.listeners.LayerTypingIndicatorListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.ConversationOptions;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessageOptions;
import com.layer.sdk.messaging.MessagePart;
import com.layer.sdk.messaging.Presence;
import com.layer.sdk.policy.Policy;
import com.layer.sdk.query.ListViewController;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.Queryable;
import com.layer.sdk.query.RecyclerViewController;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class LayerClientStub extends LayerClient {

    public Identity mAuthenticatedUser;


    // LayerClient stubs

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public Conversation newConversationWithUserIds(@NonNull ConversationOptions conversationOptions,
            @NonNull Set<String> set) {
        return null;
    }

    @Override
    public Conversation newConversation(@NonNull ConversationOptions conversationOptions,
            @NonNull Set<Identity> set) {
        return null;
    }

    @Override
    public List<Uri> getConversationIds() {
        return null;
    }

    @Override
    public Conversation getConversation(Uri uri) {
        return null;
    }

    @Override
    public List<Conversation> getConversations(List<Uri> list) {
        return null;
    }

    @Override
    public List<Conversation> getConversations() {
        return null;
    }

    @Override
    public List<Conversation> getConversationsWithParticipants(List<String> list) {
        return null;
    }

    @Override
    public Message newMessage(MessageOptions messageOptions, Set<MessagePart> set) {
        return null;
    }

    @Override
    public MessagePart newMessagePart(@NonNull String s, byte[] bytes) {
        return null;
    }

    @Override
    public MessagePart newMessagePart(@NonNull String s, InputStream inputStream, long l) {
        return null;
    }

    @Override
    public List<Uri> getMessageIds(Conversation conversation) {
        return null;
    }

    @Override
    public Message getMessage(Uri uri) {
        return null;
    }

    @Override
    public List<Message> getMessages(List<Uri> list) {
        return null;
    }

    @Override
    public List<Message> getMessages(Conversation conversation) {
        return null;
    }

    @Override
    public LayerClient registerProgressListener(MessagePart messagePart,
            LayerProgressListener layerProgressListener) {
        return this;
    }

    @Override
    public LayerClient unregisterProgressListener(MessagePart messagePart,
            LayerProgressListener layerProgressListener) {
        return this;
    }

    @Override
    public Queryable get(Uri uri) {
        return null;
    }

    @Override
    public List executeQuery(Query<? extends Queryable> query, Query.ResultType resultType) {
        return null;
    }

    @Override
    public List<Uri> executeQueryForIds(Query<? extends Queryable> query) {
        return null;
    }

    @Override
    public List<? extends Queryable> executeQueryForObjects(Query<? extends Queryable> query) {
        return null;
    }

    @Override
    public Long executeQueryForCount(Query<? extends Queryable> query) {
        return null;
    }

    @Override
    public <T extends Queryable> RecyclerViewController<T> newRecyclerViewController(Query<T> query,
            Collection<String> collection, RecyclerViewController.Callback callback) {
        return null;
    }

    @Override
    public <T extends Queryable> ListViewController<T> newListViewController(Query<T> query,
            Collection<String> collection, ListViewController.Callback callback) {
        return null;
    }

    @Override
    public void follow(@NonNull String... strings) {

    }

    @Override
    public void follow(@NonNull Identity... identities) {

    }

    @Override
    public void unFollow(@NonNull Identity... identities) {

    }

    @Override
    public void setPresenceStatus(Presence.PresenceStatus presenceStatus) {

    }

    @Override
    public Presence.PresenceStatus getPresenceStatus() {
        return null;
    }

    @Override
    public LayerClient registerEventListener(LayerChangeEventListener layerChangeEventListener) {
        return null;
    }

    @Override
    public LayerClient unregisterEventListener(LayerChangeEventListener layerChangeEventListener) {
        return null;
    }

    @Nullable
    @Override
    public Identity getAuthenticatedUser() {
        return mAuthenticatedUser;
    }

    @Override
    public boolean isAuthenticated() {
        return mAuthenticatedUser != null;
    }

    @Override
    public LayerClient authenticate() {
        return this;
    }

    @Override
    public LayerClient deauthenticate() {
        return this;
    }

    @Override
    public LayerClient deauthenticate(DeauthenticationAction deauthenticationAction) {
        return this;
    }

    @Override
    public void answerAuthenticationChallenge(@NonNull String s) {

    }

    @Override
    public LayerClient registerAuthenticationListener(
            LayerAuthenticationListener layerAuthenticationListener) {
        return this;
    }

    @Override
    public LayerClient unregisterAuthenticationListener(
            LayerAuthenticationListener layerAuthenticationListener) {
        return this;
    }

    @Override
    public LayerClient connect() {
        return this;
    }

    @Override
    public LayerClient disconnect() {
        return this;
    }

    @Override
    public LayerClient registerConnectionListener(LayerConnectionListener layerConnectionListener) {
        return this;
    }

    @Override
    public LayerClient unregisterConnectionListener(
            LayerConnectionListener layerConnectionListener) {
        return this;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public LayerClient registerSyncListener(LayerSyncListener layerSyncListener) {
        return this;
    }

    @Override
    public LayerClient unregisterSyncListener(LayerSyncListener layerSyncListener) {
        return this;
    }

    @Override
    public LayerClient setAutoDownloadSizeThreshold(long l) {
        return this;
    }

    @Override
    public long getAutoDownloadSizeThreshold() {
        return 0;
    }

    @Override
    public LayerClient setAutoDownloadMimeTypes(Collection<String> collection) {
        return this;
    }

    @Override
    public Set<String> getAutoDownloadMimeTypes() {
        return null;
    }

    @Override
    public LayerClient setDiskCapacity(long l) {
        return this;
    }

    @Override
    public long getDiskCapacity() {
        return 0;
    }

    @Override
    public long getDiskUtilization() {
        return 0;
    }

    @Override
    public LayerClient registerTypingIndicator(
            LayerTypingIndicatorListener layerTypingIndicatorListener) {
        return this;
    }

    @Override
    public LayerClient unregisterTypingIndicator(
            LayerTypingIndicatorListener layerTypingIndicatorListener) {
        return this;
    }

    @Override
    public Uri getAppId() {
        return null;
    }

    @Override
    public List<Policy> getPolicies() {
        return null;
    }

    @Override
    public boolean validatePolicy(Policy policy) {
        return false;
    }

    @Override
    public boolean addPolicy(Policy policy) {
        return false;
    }

    @Override
    public boolean insertPolicy(Policy policy, int i) {
        return false;
    }

    @Override
    public boolean removePolicy(Policy policy) {
        return false;
    }

    @Override
    public LayerClient registerPolicyListener(LayerPolicyListener layerPolicyListener) {
        return this;
    }

    @Override
    public LayerClient unregisterPolicyListener(LayerPolicyListener layerPolicyListener) {
        return this;
    }

    @Override
    public LayerClient registerObjectExceptionListener(
            LayerObjectExceptionListener layerObjectExceptionListener) {
        return this;
    }

    @Override
    public LayerClient unregisterObjectExceptionListener(
            LayerObjectExceptionListener layerObjectExceptionListener) {
        return this;
    }

    @Override
    public void waitForContent(@NonNull Uri uri,
            @NonNull ContentAvailableCallback contentAvailableCallback) {

    }

    @Override
    public void waitForContent(@NonNull Uri uri, long l,
            @NonNull ContentAvailableCallback contentAvailableCallback) {

    }
}
