package com.layer.xdk.ui.message.model;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.repository.MessageSenderRepository;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.DateFormatterImpl;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MessageModel extends BaseObservable implements LayerProgressListener.Weak {
    protected static final String ROLE_ROOT = "root";

    private final AtomicInteger mDownloadingPartCounter;

    private IdentityFormatter mIdentityFormatter;
    private DateFormatter mDateFormatter;

    private final Context mContext;
    private final LayerClient mLayerClient;

    private Message mMessage;
    private String mRole;
    private MessagePart mRootMessagePart;
    private MessageModel mParentMessageModel;
    private List<MessagePart> mChildMessageParts;
    private List<MessageModel> mChildMessageModels;
    private MessagePart mResponseSummaryPart;

    private MessageModelManager mMessageModelManager;

    private MessageSenderRepository mMessageSenderRepository;

    private Action mAction;

    // TODO AND-1242 It's safe to cache this since no model will live after a de-auth
    private final Uri mAuthenticatedUserId;

    // TODO AND-1242 Are we sure we want to do this? Cache the participants since it's an expensive call. Will need to handle other changes in the data source then
    private Set<Identity> mParticipants;

    private String mMimeTypeTree;

    public MessageModel(Context context, LayerClient layerClient) {
        mIdentityFormatter = new IdentityFormatterImpl(context);
        mDateFormatter = new DateFormatterImpl(context);
        mDownloadingPartCounter = new AtomicInteger();
        mContext = context;
        mLayerClient = layerClient;
        mChildMessageModels = new ArrayList<>();

        mAuthenticatedUserId = layerClient.getAuthenticatedUser().getId();
    }

    public Object createNewViewController(ViewDataBinding binding) {
        return null;
    }

    public void setMessage(@NonNull Message message) {
        MessagePart rootMessagePart = MessagePartUtils.getMessagePartWithRoleRoot(message);
        if (rootMessagePart == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Message has no message part with role = root");
            }
            throw new IllegalArgumentException("Message has no message part with role = root");
        }

        setMessage(message, rootMessagePart);
    }

    public void setMessage(@NonNull Message message, @Nullable MessagePart rootMessagePart) {
        mMessage = message;
        mRootMessagePart = rootMessagePart;
        mParticipants = mMessage.getConversation().getParticipants();

        mChildMessageModels.clear();
        if (mChildMessageParts != null) {
            mChildMessageParts.clear();
        }
        mResponseSummaryPart = null;

        if (mRootMessagePart != null) {
            // Always download and parse the root part
            if (mRootMessagePart.isContentReady()) {
                parse(mRootMessagePart);
            } else {
                download(mRootMessagePart);
            }
        }

        setRole(ROLE_ROOT);
        // Deal with child parts
        processChildParts();

        // Set View type
        setMimeTypeTree();
    }

    private void setMimeTypeTree() {
        StringBuilder sb = new StringBuilder();
        if (mRootMessagePart != null) {
            sb.append(MessagePartUtils.getMimeType(mRootMessagePart));
            sb.append("[");
        }
        boolean prependComma = false;
        for (MessagePart childPart : mChildMessageParts) {
            if (prependComma) {
                sb.append(",");
            }
            sb.append(MessagePartUtils.getMimeType(childPart));
            prependComma = true;
        }
        if (mRootMessagePart != null) {
            sb.append("]");
        }
        mMimeTypeTree = sb.toString();
    }

    public String getMimeTypeTree() {
        return mMimeTypeTree;
    }

    protected void processChildParts() {
        if (mRootMessagePart != null) {
            mChildMessageParts = MessagePartUtils.getChildParts(mMessage, mRootMessagePart);

            for (MessagePart childMessagePart : mChildMessageParts) {
                if (childMessagePart.isContentReady()) {
                    parse(childMessagePart);
                } else if (shouldDownloadContentIfNotReady(childMessagePart)) {
                    download(childMessagePart);
                }

                if (MessagePartUtils.isResponseSummaryPart(childMessagePart)) {
                    mResponseSummaryPart = childMessagePart;
                    processResponseSummaryPart(childMessagePart);
                    continue;
                }

                String mimeType = MessagePartUtils.getMimeType(childMessagePart);
                if (mimeType == null) continue;
                MessageModel childModel = mMessageModelManager.getNewModel(mimeType);
                if (childModel != null) {
                    childModel.setParentMessageModel(this);
                    mChildMessageModels.add(childModel);
                    childModel.setMessageModelManager(mMessageModelManager);
                    childModel.setMessage(mMessage, childMessagePart);
                    String role = MessagePartUtils.getRole(childMessagePart);
                    if (role != null) {
                        childModel.setRole(role);
                    }
                }
            }
        }
    }

    protected void processResponseSummaryPart(@NonNull MessagePart responseSummaryPart) {
        // Standard operation is no-op
    }

    public abstract @LayoutRes int getViewLayoutId();

    public abstract  @LayoutRes int getContainerViewLayoutId();

    protected void download(@NonNull MessagePart messagePart) {
        messagePart.download(this);
    }

    protected abstract void parse(@NonNull MessagePart messagePart);

    protected abstract boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart);

    @NonNull
    protected MessagePart getRootMessagePart() {
        return mRootMessagePart;
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

    protected MessageModelManager getMessageModelManager() {
        return mMessageModelManager;
    }

    @Nullable
    protected MessagePart getResponseSummaryPart() {
        return mResponseSummaryPart;
    }

    public void setMessageModelManager(@NonNull MessageModelManager messageModelManager) {
        mMessageModelManager = messageModelManager;
    }

    @Override
    public void onProgressStart(MessagePart messagePart, Operation operation) {
        incrementDownloadingPartCounter();
    }

    @Override
    public void onProgressUpdate(MessagePart messagePart, Operation operation, long l) {

    }

    @Override
    public void onProgressComplete(MessagePart messagePart, Operation operation) {
        decrementDownloadingPartCounter();
        parse(messagePart);
        notifyChange();
    }

    @Override
    public void onProgressError(MessagePart messagePart, Operation operation, Throwable throwable) {
        decrementDownloadingPartCounter();
    }

    @Nullable
    @Bindable
    public abstract String getTitle();

    @Nullable
    @Bindable
    public abstract String getDescription();

    @Nullable
    @Bindable
    public abstract String getFooter();

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
    @ColorRes
    public int getBackgroundColor() {
        return R.color.transparent;
    }

    @Bindable
    public boolean getHasMetadata() {
        return (!TextUtils.isEmpty(getTitle()))
                || !TextUtils.isEmpty(getDescription())
                || !TextUtils.isEmpty(getFooter());
    }

    @Bindable
    public abstract boolean getHasContent();

    @Bindable
    @Nullable
    public abstract String getPreviewText();

    public boolean isRoleRoot() {
        return ROLE_ROOT.equals(mRole);
    }

    @Nullable
    public String getRole() {
        return mRole;
    }

    public void setRole(@NonNull String role) {
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

    private void incrementDownloadingPartCounter() {
        mDownloadingPartCounter.getAndIncrement();
    }

    private void decrementDownloadingPartCounter() {
        if (mDownloadingPartCounter.intValue() == 0) return;
        mDownloadingPartCounter.getAndDecrement();
    }

    protected Context getContext() {
        return mContext;
    }

    protected LayerClient getLayerClient() {
        return mLayerClient;
    }

    protected IdentityFormatter getIdentityFormatter() {
        return mIdentityFormatter;
    }

    public void setIdentityFormatter(IdentityFormatter identityFormatter) {
        mIdentityFormatter = identityFormatter;
    }

    protected DateFormatter getDateFormatter() {
        return mDateFormatter;
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        mDateFormatter = dateFormatter;
    }

    // TODO AND-1242 - Make this final and set in the constructor
    public Message getMessage() {
        return mMessage;
    }

    protected int getNumberOfPartsCurrentlyDownloading() {
        return mDownloadingPartCounter.intValue();
    }

    @Bindable
    public boolean isMessageFromMe() {
        return mAuthenticatedUserId.equals(getMessage().getSender().getId());
    }

    @NonNull
    protected MessageSenderRepository getMessageSenderRepository() {
        if (mMessageSenderRepository == null) {
            mMessageSenderRepository = new MessageSenderRepository(mContext, mLayerClient);
        }
        return mMessageSenderRepository;
    }

    public Set<Identity> getParticipants() {
        return mParticipants;
    }

    public Uri getAuthenticatedUserId() {
        return mAuthenticatedUserId;
    }
}
