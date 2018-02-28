package com.layer.xdk.ui.message.model;

import android.content.Context;
import android.databinding.Bindable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.repository.MessageSenderRepository;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageModel extends AbstractMessageModel {
    private String mRole;
    // TODO AND-1242 Rename this to just mMessagePart?
    private MessagePart mRootMessagePart;
    private MessageModel mParentMessageModel;
    private List<MessagePart> mChildMessageParts;
    private List<MessageModel> mChildMessageModels;
    private MessagePart mResponseSummaryPart;

    // TODO AND-1287 Inject this
    private MessageModelManager mMessageModelManager;

    private MessageSenderRepository mMessageSenderRepository;

    private Action mAction;
    private String mMimeTypeTree;

    public MessageModel(Context context, LayerClient layerClient, @NonNull Message message) {
        super(context, layerClient, message);
        mChildMessageModels = new ArrayList<>();
    }

    public final void processParts() {
        MessagePart rootMessagePart = MessagePartUtils.getMessagePartWithRoleRoot(getMessage());
        if (rootMessagePart == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Message has no message part with role = root");
            }
            throw new IllegalArgumentException("Message has no message part with role = root");
        }
        // Always download the message's root part
        if (!rootMessagePart.isContentReady()) {
            download(rootMessagePart);
        }

        processParts(rootMessagePart);
    }

    private void processParts(@NonNull MessagePart rootMessagePart) {
        mRootMessagePart = rootMessagePart;
        setRole(MessagePartUtils.getRole(rootMessagePart));
        if (mRootMessagePart.isContentReady()) {
            parse(mRootMessagePart);
        }

        // Deal with child parts
        processChildParts();

        // Set View type
        setMimeTypeTree();
    }

    protected void processChildParts() {
        mChildMessageParts = MessagePartUtils.getChildParts(getMessage(), mRootMessagePart);

        for (MessagePart childMessagePart : mChildMessageParts) {
            if (childMessagePart.isContentReady()) {
                parseChildPart(childMessagePart);
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
            MessageModel childModel = mMessageModelManager.getNewModel(mimeType, getMessage());
            if (childModel != null) {
                childModel.setParentMessageModel(this);
                childModel.processParts(childMessagePart);
                mChildMessageModels.add(childModel);
            }
        }
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

    @Override
    public String getMimeTypeTree() {
        return mMimeTypeTree;
    }

    protected void processResponseSummaryPart(@NonNull MessagePart responseSummaryPart) {
        // Standard operation is no-op
    }


    protected abstract void parse(@NonNull MessagePart messagePart);

    protected void parseChildPart(@NonNull MessagePart childMessagePart) {
        // Standard operation is no-op
    }

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

    @Nullable
    protected MessagePart getResponseSummaryPart() {
        return mResponseSummaryPart;
    }

    public void setMessageModelManager(@NonNull MessageModelManager messageModelManager) {
        mMessageModelManager = messageModelManager;
    }

    // TODO AND-1242 - Remove these?

//    @Override
//    public void onProgressStart(MessagePart messagePart, Operation operation) {
//        incrementDownloadingPartCounter();
//    }
//
//    @Override
//    public void onProgressUpdate(MessagePart messagePart, Operation operation, long l) {
//
//    }
//
//    @Override
//    public void onProgressComplete(MessagePart messagePart, Operation operation) {
//        decrementDownloadingPartCounter();
//        parse(messagePart);
//        notifyChange();
//    }
//
//    @Override
//    public void onProgressError(MessagePart messagePart, Operation operation, Throwable throwable) {
//        decrementDownloadingPartCounter();
//    }

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
    public boolean getHasMetadata() {
        return (!TextUtils.isEmpty(getTitle()))
                || !TextUtils.isEmpty(getDescription())
                || !TextUtils.isEmpty(getFooter());
    }

    @Bindable
    public abstract boolean getHasContent();

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

//    private void incrementDownloadingPartCounter() {
//        mDownloadingPartCounter.getAndIncrement();
//    }
//
//    private void decrementDownloadingPartCounter() {
//        if (mDownloadingPartCounter.intValue() == 0) return;
//        mDownloadingPartCounter.getAndDecrement();
//    }



//
//    protected int getNumberOfPartsCurrentlyDownloading() {
//        return mDownloadingPartCounter.intValue();
//    }



    // TODO AND-1287 Inject this
    @NonNull
    protected MessageSenderRepository getMessageSenderRepository() {
        if (mMessageSenderRepository == null) {
            mMessageSenderRepository = new MessageSenderRepository(getContext(), getLayerClient());
        }
        return mMessageSenderRepository;
    }

}
