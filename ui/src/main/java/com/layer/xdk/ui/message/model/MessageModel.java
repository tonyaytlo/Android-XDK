package com.layer.xdk.ui.message.model;


import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.adapter.MessageGrouping;
import com.layer.xdk.ui.message.adapter.MessageModelAdapter;
import com.layer.xdk.ui.repository.MessageSenderRepository;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MessageModel extends BaseObservable {

    private final Context mContext;
    private final LayerClient mLayerClient;
    // These fields are set directly after instantiation by the MessageModelManager
    private MessageModelManager mMessageModelManager;
    private IdentityFormatter mIdentityFormatter;
    private DateFormatter mDateFormatter;

    private final Message mMessage;

    // It's safe to cache this since no model will live after a de-auth
    private final Uri mAuthenticatedUserId;
    private final Uri mSenderId;

    private int mParticipantCount;
    private boolean mMyNewestMessage;

    private String mRole;
    private MessagePart mRootMessagePart;
    private MessageModel mParentMessageModel;
    private List<MessagePart> mChildMessageParts;
    private List<MessageModel> mChildMessageModels;
    private MessagePart mResponseSummaryPart;

    private MessageSenderRepository mMessageSenderRepository;

    private Action mAction;
    private String mMimeTypeTree;

    private EnumSet<MessageGrouping> mGrouping;

    // Save these purely for deep equals comparisons
    private Map<Identity, Message.RecipientStatus> mRecipientStatuses;
    private Date mMessageUpdatedAt;
    private Set<MessagePart.TransferStatus> mMessagePartTransferStatus;
    private Set<Date> mMessagePartUpdatedAt;
    private byte[] mMessageLocalData;

    public MessageModel(Context context, LayerClient layerClient, @NonNull Message message) {
        mContext = context.getApplicationContext();
        mLayerClient = layerClient;

        mMessage = message;
        Identity authenticatedUser = layerClient.getAuthenticatedUser();
        mAuthenticatedUserId = authenticatedUser == null ? null : authenticatedUser.getId();
        Identity sender = message.getSender();
        mSenderId = sender == null ? null : sender.getId();

        mParticipantCount = mMessage.getConversation().getParticipants().size();
        mChildMessageModels = new ArrayList<>();
        mMessageLocalData = mMessage.getLocalData();
    }

    protected abstract void parse(@NonNull MessagePart messagePart);

    /**
     * Provides the layout resource ID of the view to inflate into the container.
     *
     * @return layout resource ID to inflate into the container. If no layout is associated, 0
     * should be returned.
     */
    @LayoutRes
    public abstract int getViewLayoutId();

    /**
     * Provides the layout resource ID of the container for this model that will be inflated into
     * a ViewHolder. If no layout is associated, 0 should be returned.
     *
     * @return layout resource ID to inflate into the ViewHolder. If no layout is associated, 0
     * should be returned.
     */
    @LayoutRes
    public abstract int getContainerViewLayoutId();

    protected abstract boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart);

    @Bindable
    public abstract boolean getHasContent();

    @Bindable
    @Nullable
    public abstract String getPreviewText();

    @Nullable
    @Bindable
    public abstract String getTitle();

    @Nullable
    @Bindable
    public abstract String getDescription();

    @Nullable
    @Bindable
    public abstract String getFooter();

    public final void processPartsFromTreeRoot() {
        MessagePart rootMessagePart = MessagePartUtils.getMessagePartWithRoleRoot(getMessage());
        if (rootMessagePart == null) {
            mMimeTypeTree = createLegacyMimeTypeTree();
            processLegacyParts();
            cacheMessageDataForDeepEquals();
        } else {
            // Always download the message's root part
            if (!rootMessagePart.isContentReady()) {
                rootMessagePart.download(null);
            }

            processParts(rootMessagePart);
            cacheMessageDataForDeepEquals();
        }
    }

    protected void processLegacyParts() {
        if (Log.isLoggable(Log.ERROR)) {
            Log.e("Message has no message part with role = root and no legacy part handling");
        }
        throw new IllegalArgumentException("Message has no message part with role = root and no"
                + " legacy part handling");
    }


    @CallSuper
    protected void processParts(@NonNull MessagePart rootMessagePart) {
        mRootMessagePart = rootMessagePart;
        setRole(MessagePartUtils.getRole(rootMessagePart));
        if (mRootMessagePart.isContentReady()) {
            parse(mRootMessagePart);
        }

        // Deal with child parts
        processChildParts();

        // Set View type
        mMimeTypeTree = createMimeTypeTree();
    }

    protected void processChildParts() {
        mChildMessageParts = MessagePartUtils.getChildParts(getMessage(), mRootMessagePart);

        for (MessagePart childMessagePart : mChildMessageParts) {
            if (childMessagePart.isContentReady()) {
                if (parseChildPart(childMessagePart)) {
                    continue;
                }
            } else if (shouldDownloadContentIfNotReady(childMessagePart)) {
                childMessagePart.download(null);
            }

            if (MessagePartUtils.isResponseSummaryPart(childMessagePart)) {
                mResponseSummaryPart = childMessagePart;
                processResponseSummaryPart(childMessagePart);
                continue;
            }

            String mimeType = MessagePartUtils.getMimeType(childMessagePart);
            if (mimeType == null) continue;
            MessageModel childModel = mMessageModelManager.getNewModel(mimeType, getMessage());
            childModel.setParentMessageModel(this);
            childModel.processParts(childMessagePart);
            mChildMessageModels.add(childModel);
        }
    }

    protected void processResponseSummaryPart(@NonNull MessagePart responseSummaryPart) {
        // Standard operation is no-op
    }

    /**
     * <p>
     *     Override this method to optionally consume the supplied MessagePart without having the
     *     framework attempt to generate a model from it.
     *
     *     The method defaults to returning false, upon which the framework will attempt to
     *     generate a MessageModel instance from the currently registered MessageModel types.
     * </p>
     *
     * @param childMessagePart a MessagePart that has declared this MessageModel as its parent
     * @return true if the implementing class has consumed this MessagePart
     */
    protected boolean parseChildPart(@NonNull MessagePart childMessagePart) {
        return false;
    }

    private String createMimeTypeTree() {
        StringBuilder sb = new StringBuilder();
        if (mRootMessagePart != null) {
            sb.append(MessagePartUtils.getMimeType(mRootMessagePart));
            sb.append("[");
        }
        boolean prependComma = false;
        if (mChildMessageParts != null) {
            for (MessagePart childPart : mChildMessageParts) {
                if (prependComma) {
                    sb.append(",");
                }
                sb.append(MessagePartUtils.getMimeType(childPart));
                prependComma = true;
            }
        }
        if (mRootMessagePart != null) {
            sb.append("]");
        }
        return sb.toString();
    }

    protected String createLegacyMimeTypeTree() {
        StringBuilder sb = new StringBuilder();
        boolean prependComma = false;
        for (MessagePart part : getMessage().getMessageParts()) {
            if (prependComma) {
                sb.append(",");
            }
            sb.append(part.getMimeType());
            sb.append("[]");
            prependComma = true;
        }
        return sb.toString();
    }

    /**
     * Provide a tree of mime types that correspond to all the message parts. Usually
     * this should not be overridden. If it is then build the tree as follows
     *  1. The root level parts should be comma separated
     *  2. If a part has children, those mime types should be comma separated and enclosed in
     *  square brackets (i.e. []).
     *
     * @return A string representing the mime type tree of all message parts
     */
    @NonNull
    public String getMimeTypeTree() {
        return mMimeTypeTree;
    }

    /**
     * Store data from the message and message parts that will need to be compared when determining
     * what data has changed. We need to cache this data because there is only one message object
     * provided by the SDK so we can't check old vs new at that time.
     */
    private void cacheMessageDataForDeepEquals() {
        mRecipientStatuses = getMessage().getRecipientStatus();
        mMessageUpdatedAt = getMessage().getUpdatedAt();
        Set<MessagePart> messageParts = getMessage().getMessageParts();
        mMessagePartTransferStatus = new HashSet<>(messageParts.size());
        mMessagePartUpdatedAt = new HashSet<>(messageParts.size());
        for (MessagePart messagePart : messageParts) {
            mMessagePartTransferStatus.add(messagePart.getTransferStatus());
            if (messagePart.getUpdatedAt() != null) {
                mMessagePartUpdatedAt.add(messagePart.getUpdatedAt());
            }
        }
    }

    /**
     * Get the {@link MessagePart} corresponding to the root node of this subtree. This may or
     * may not be the root of the entire model tree.
     *
     * @return message part corresponding to the root node of this model's subtree
     */
    @NonNull
    protected MessagePart getRootMessagePart() {
        return mRootMessagePart;
    }

    /**
     * Get the model that is a parent node to this model in the tree. If null, this model is the
     * root of the entire model tree.
     *
     * @return this models parent node or null if this model is the root of the tree
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public MessageModel getParentMessageModel() {
        return mParentMessageModel;
    }

    /**
     * Walk up the tree to find the root {@link MessageModel}.
     *
     * @return The root model for the tree or this model if it is the root of the tree
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public MessageModel getRootModelForTree() {
        MessageModel treeRoot = this;
        while (true) {
            MessageModel parent = treeRoot.getParentMessageModel();
            if (parent == null) {
                break;
            }
            treeRoot = parent;
        }
        return treeRoot;
    }

    private void setParentMessageModel(@NonNull MessageModel parent) {
        mParentMessageModel = parent;
    }

    @Nullable
    protected List<MessagePart> getChildMessageParts() {
        return mChildMessageParts;
    }

    @Nullable
    protected List<MessageModel> getChildMessageModels() {
        return mChildMessageModels;
    }

    protected void addChildMessageModel(MessageModel messageModel) {
        mChildMessageModels.add(messageModel);
    }

    @Nullable
    protected MessagePart getResponseSummaryPart() {
        return mResponseSummaryPart;
    }

    public void setAction(Action action) {
        mAction = action;
    }

    @CallSuper
    @Nullable
    public String getActionEvent() {
        return mAction != null ? mAction.getEvent() : null;
    }

    @NonNull
    @CallSuper
    public JsonObject getActionData() {
        return mAction != null ? mAction.getData() : new JsonObject();
    }

    @Bindable
    public boolean getHasMetadata() {
        return (!TextUtils.isEmpty(getTitle()))
                || !TextUtils.isEmpty(getDescription())
                || !TextUtils.isEmpty(getFooter());
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    protected String getRole() {
        return mRole;
    }

    @SuppressWarnings("WeakerAccess")
    protected void setRole(@Nullable String role) {
        mRole = role;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    protected List<MessageModel> getChildMessageModelsWithRole(@NonNull String role) {
        List<MessageModel> models = new ArrayList<>();
        if (role.equals(mRole)) {
            models.add(this);
        }

        if (mChildMessageModels != null && !mChildMessageModels.isEmpty()) {
            for (MessageModel childModel : mChildMessageModels) {
                if (role.equals(childModel.getRole())) {
                    models.add(childModel);
                }
            }
        }

        return models;
    }

    @Bindable
    @ColorRes
    public int getBackgroundColor() {
        return android.R.color.transparent;
    }

    @Bindable
    public final boolean isMessageFromMe() {
        if (mAuthenticatedUserId != null) {
            return mAuthenticatedUserId.equals(mSenderId);
        }
        if (Log.isLoggable(Log.ERROR)) {
            Log.e("Failed to check if message is from me. Authenticated user is null Message: "
                    + getMessage());
        }
        throw new IllegalStateException("Failed to check if message is from me. Authenticated "
                + "user is null Message: " + getMessage());
    }

    public final void setMessageModelManager(@NonNull MessageModelManager messageModelManager) {
        mMessageModelManager = messageModelManager;
    }

    protected Context getAppContext() {
        return mContext;
    }

    protected LayerClient getLayerClient() {
        return mLayerClient;
    }

    @NonNull
    public final Message getMessage() {
        return mMessage;
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    protected final Uri getAuthenticatedUserId() {
        return mAuthenticatedUserId;
    }

    @Nullable
    public final Uri getSenderId() {
        return mSenderId;
    }

    public final int getParticipantCount() {
        return mParticipantCount;
    }

    // TODO AND-1287 Inject this
    @NonNull
    protected MessageSenderRepository getMessageSenderRepository() {
        if (mMessageSenderRepository == null) {
            mMessageSenderRepository = new MessageSenderRepository(getAppContext(), getLayerClient());
        }
        return mMessageSenderRepository;
    }

    protected IdentityFormatter getIdentityFormatter() {
        return mIdentityFormatter;
    }

    public final void setIdentityFormatter(IdentityFormatter identityFormatter) {
        mIdentityFormatter = identityFormatter;
    }

    protected DateFormatter getDateFormatter() {
        return mDateFormatter;
    }

    public final void setDateFormatter(DateFormatter dateFormatter) {
        mDateFormatter = dateFormatter;
    }

    /**
     * Get the {@link MessageGrouping} for this model when shown in an adapter. This will only be
     * valid for root models and should be null for inner models. No grouping will be set if this
     * is not passed through a {@link com.layer.xdk.ui.message.adapter.GroupingCalculator}.
     *
     * @return set of grouping values for the root model or null if it is an inner model or none
     * has been set.
     */
    @Nullable
    public EnumSet<MessageGrouping> getGrouping() {
        return mGrouping;
    }

    /**
     * Set the {@link MessageGrouping} value for this model. This is usually used to handle
     * decoration and view state in a {@link MessageModelAdapter}.
     *
     * @param grouping set of groupings
     */
    public void setGrouping(EnumSet<MessageGrouping> grouping) {
        mGrouping = grouping;
    }

    public boolean isMyNewestMessage() {
        return mMyNewestMessage;
    }

    public void setMyNewestMessage(boolean myNewestMessage) {
        mMyNewestMessage = myNewestMessage;
    }

    /**
     * Perform an equals check on most properties. Child model equality is checked but parent
     * models are skipped as this will produce infinite recursion.
     *
     * This is primarily used for calculations with {@link android.support.v7.util.DiffUtil}.
     *
     * @param other model to compare to
     * @return true if all properties are equal
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean deepEquals(@NonNull MessageModel other) {
        if (getGrouping() == null ? other.getGrouping() != null
                : !getGrouping().containsAll(other.getGrouping())) {
            return false;
        }
        if (isMyNewestMessage() != other.isMyNewestMessage()) {
            return false;
        }
        if (getAuthenticatedUserId() == null ? other.getAuthenticatedUserId() != null
                : !getAuthenticatedUserId().equals(other.getAuthenticatedUserId())) {
            return false;
        }
        if (getSenderId() == null ? other.getSenderId() != null
                : !getSenderId().equals(other.getSenderId())) {
            return false;
        }
        if (getParticipantCount() != other.getParticipantCount()) {
            return false;
        }
        if (getRole() == null ? other.getRole() != null : !getRole().equals(other.getRole())) {
            return false;
        }
        if (!getActionData().equals(other.getActionData())) {
            return false;
        }
        if (!getMimeTypeTree().equals(other.getMimeTypeTree())) {
            return false;
        }
        if (getContainerViewLayoutId() != other.getContainerViewLayoutId()) {
            return false;
        }
        if (getViewLayoutId() != other.getViewLayoutId()) {
            return false;
        }
        if (getHasContent() != other.getHasContent()) {
            return false;
        }
        if (getPreviewText() == null ? other.getPreviewText() != null
                : !getPreviewText().equals(other.getPreviewText())) {
            return false;
        }
        if (getTitle() == null ? other.getTitle() != null
                : !getTitle().equals(other.getTitle())) {
            return false;
        }
        if (getDescription() == null ? other.getDescription() != null
                : !getDescription().equals(other.getDescription())) {
            return false;
        }
        if (getFooter() == null ? other.getFooter() != null
                : !getFooter().equals(other.getFooter())) {
            return false;
        }
        if (getHasMetadata() != other.getHasMetadata()) {
            return false;
        }
        if (getBackgroundColor() != other.getBackgroundColor()) {
            return false;
        }
        if (isMessageFromMe() != other.isMessageFromMe()) {
            return false;
        }
        if (getChildMessageModels() == null) {
            if (other.getChildMessageModels() != null) {
                return false;
            }
        } else {
            if (other.getChildMessageModels() == null) {
                return false;
            }
            if (getChildMessageModels().size() != other.getChildMessageModels().size()) {
                return false;
            }
            Iterator<MessageModel> iterator = getChildMessageModels().iterator();
            Iterator<MessageModel> otherIterator = other.getChildMessageModels().iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().deepEquals(otherIterator.next())) {
                    return false;
                }
            }
        }

        if (mMessageUpdatedAt == null ? other.mMessageUpdatedAt != null
                : !mMessageUpdatedAt.equals(other.mMessageUpdatedAt)) {
            return false;
        }
        if (mMessagePartUpdatedAt == null ? other.mMessagePartUpdatedAt != null
                : !mMessagePartUpdatedAt.equals(other.mMessagePartUpdatedAt)) {
            return false;
        }

        if (mRecipientStatuses == null ? other.mRecipientStatuses != null
                : !mRecipientStatuses.equals(other.mRecipientStatuses)) {
            return false;
        }

        if (mMessagePartTransferStatus == null ? other.mMessagePartTransferStatus != null
                : !mMessagePartTransferStatus.equals(other.mMessagePartTransferStatus)) {
            return false;
        }

        if (!Arrays.equals(mMessageLocalData, other.mMessageLocalData)) {
            return false;
        }

        // Don't bother checking parent model as that will infinitely recurse
        return true;
    }
}
