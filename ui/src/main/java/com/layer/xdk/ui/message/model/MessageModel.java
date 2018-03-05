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
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.adapter2.MessageGrouping;
import com.layer.xdk.ui.repository.MessageSenderRepository;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.DateFormatterImpl;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class MessageModel extends BaseObservable {

    // TODO AND-1287 Inject these and make them non static. Making static for now to reduce allocs
    private static IdentityFormatter sIdentityFormatter;
    private static DateFormatter sDateFormatter;

    private final Context mContext;
    private final LayerClient mLayerClient;

    private final Message mMessage;

    // It's safe to cache this since no model will live after a de-auth
    private final Uri mAuthenticatedUserId;
    private final Uri mSenderId;

    private int mParticipantCount;

    private String mRole;
    private MessagePart mMessagePart;
    private MessageModel mParentMessageModel;
    private List<MessagePart> mChildMessageParts;
    private List<MessageModel> mChildMessageModels;
    private MessagePart mResponseSummaryPart;

    // TODO AND-1287 Inject this
    private MessageModelManager mMessageModelManager;

    private MessageSenderRepository mMessageSenderRepository;

    private Action mAction;
    private String mMimeTypeTree;

    private EnumSet<MessageGrouping> mGrouping;

    public MessageModel(Context context, LayerClient layerClient, @NonNull Message message) {
        mContext = context.getApplicationContext();
        if (sIdentityFormatter == null) {
            sIdentityFormatter = new IdentityFormatterImpl(mContext);
        }
        if (sDateFormatter == null) {
            sDateFormatter = new DateFormatterImpl(mContext);
        }
        mLayerClient = layerClient;

        mMessage = message;
        Identity authenticatedUser = layerClient.getAuthenticatedUser();
        mAuthenticatedUserId = authenticatedUser == null ? null : authenticatedUser.getId();
        Identity sender = message.getSender();
        mSenderId = sender == null ? null : sender.getId();

        mParticipantCount = mMessage.getConversation().getParticipants().size();

        mChildMessageModels = new ArrayList<>();
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

    public final void processParts() {
        MessagePart rootMessagePart = MessagePartUtils.getMessagePartWithRoleRoot(getMessage());
        if (rootMessagePart == null) {
            mMimeTypeTree = createLegacyMimeTypeTree();
            processLegacyParts();
        } else {
            // Always download the message's root part
            if (!rootMessagePart.isContentReady()) {
                rootMessagePart.download(null);
            }

            processParts(rootMessagePart);
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
        mMessagePart = rootMessagePart;
        setRole(MessagePartUtils.getRole(rootMessagePart));
        if (mMessagePart.isContentReady()) {
            parse(mMessagePart);
        }

        // Deal with child parts
        processChildParts();

        // Set View type
        mMimeTypeTree = createMimeTypeTree();
    }

    protected void processChildParts() {
        mChildMessageParts = MessagePartUtils.getChildParts(getMessage(), mMessagePart);

        for (MessagePart childMessagePart : mChildMessageParts) {
            if (childMessagePart.isContentReady()) {
                parseChildPart(childMessagePart);
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

    protected void parseChildPart(@NonNull MessagePart childMessagePart) {
        // Standard operation is no-op
    }

    private String createMimeTypeTree() {
        StringBuilder sb = new StringBuilder();
        if (mMessagePart != null) {
            sb.append(MessagePartUtils.getMimeType(mMessagePart));
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
        if (mMessagePart != null) {
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
    public String getMimeTypeTree() {
        return mMimeTypeTree;
    }

    @NonNull
    protected MessagePart getMessagePart() {
        return mMessagePart;
    }

    @Nullable
    public MessageModel getParentMessageModel() {
        return mParentMessageModel;
    }

    public void setParentMessageModel(@NonNull MessageModel parent) {
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

    @Nullable
    public String getRole() {
        return mRole;
    }

    public void setRole(@Nullable String role) {
        mRole = role;
    }

    @NonNull
    public List<MessageModel> getChildMessageModelsWithRole(@NonNull String role) {
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
        return R.color.transparent;
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

    public void setMessageModelManager(@NonNull MessageModelManager messageModelManager) {
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

    @Nullable
    public final Uri getAuthenticatedUserId() {
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
        return sIdentityFormatter;
    }

    public void setIdentityFormatter(IdentityFormatter identityFormatter) {
        sIdentityFormatter = identityFormatter;
    }

    protected DateFormatter getDateFormatter() {
        return sDateFormatter;
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        sDateFormatter = dateFormatter;
    }

    // TODO Do we want to expose the whole set?
    public EnumSet<MessageGrouping> getGrouping() {
        return mGrouping;
    }

    public void setGrouping(EnumSet<MessageGrouping> grouping) {
        mGrouping = grouping;
    }
}
