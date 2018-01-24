package com.layer.ui.message.model;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.identity.IdentityFormatter;
import com.layer.ui.identity.IdentityFormatterImpl;
import com.layer.ui.message.MessagePartUtils;
import com.layer.ui.message.view.MessageView;
import com.layer.ui.repository.MessageSenderRepository;
import com.layer.ui.util.DateFormatter;
import com.layer.ui.util.DateFormatterImpl;
import com.layer.ui.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MessageModel extends BaseObservable implements LayerProgressListener.Weak {
    private final AtomicInteger mDownloadingPartCounter;

    private IdentityFormatter mIdentityFormatter;
    private DateFormatter mDateFormatter;

    private final Context mContext;
    private final LayerClient mLayerClient;

    private Message mMessage;
    private MessagePart mRootMessagePart;
    private List<MessagePart> mChildMessageParts;
    private List<MessageModel> mChildMessageModels;
    private MessagePart mResponseSummaryPart;

    private MessageModelManager mMessageModelManager;

    private MessageSenderRepository mMessageSenderRepository;

    private Action mAction;

    public MessageModel(Context context, LayerClient layerClient) {
        mIdentityFormatter = new IdentityFormatterImpl(context);
        mDateFormatter = new DateFormatterImpl(context);
        mDownloadingPartCounter = new AtomicInteger();
        mContext = context;
        mLayerClient = layerClient;
        mChildMessageModels = new ArrayList<>();
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
        if (!message.equals(mMessage)) {
            mMessage = message;
            mRootMessagePart = rootMessagePart;

            if (mRootMessagePart != null) {
                // Always download and parse the root part
                if (mRootMessagePart.isContentReady()) {
                    parse(mRootMessagePart);
                } else {
                    download(mRootMessagePart);
                }
            }

            // Deal with child parts
            processChildParts();
        }
    }

    protected void processChildParts() {
        if (mRootMessagePart != null) {
            mChildMessageParts = MessagePartUtils.getChildParts(mMessage, mRootMessagePart);

            if (mChildMessageParts != null) {
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
                        mChildMessageModels.add(childModel);
                        childModel.setMessageModelManager(mMessageModelManager);
                        childModel.setMessage(mMessage, childMessagePart);
                    }
                }
            }
        } else {
            mChildMessageModels.clear();
            mChildMessageParts.clear();
        }
    }

    protected void processResponseSummaryPart(@NonNull MessagePart responseSummaryPart) {
        // Standard operation is no-op
    }

    public abstract Class<? extends MessageView> getRendererType();

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
        return  mAction!=null ? mAction.getData() : new JsonObject();
    }

    @Bindable
    @ColorRes
    public abstract int getBackgroundColor();

    @Bindable
    public boolean getHasMetadata() {
        return (!TextUtils.isEmpty(getTitle()))
                || !TextUtils.isEmpty(getDescription())
                || !TextUtils.isEmpty(getFooter());
    }

    @Bindable
    public abstract boolean getHasContent();

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

    public Message getMessage() {
        return mMessage;
    }

    protected int getNumberOfPartsCurrentlyDownloading() {
        return mDownloadingPartCounter.intValue();
    }

    @Bindable
    public boolean isMessageFromMe() {
        return getLayerClient().getAuthenticatedUser().equals(getMessage().getSender());
    }

    @NonNull
    protected MessageSenderRepository getMessageSenderRepository() {
        if (mMessageSenderRepository == null) {
            mMessageSenderRepository = new MessageSenderRepository(mContext, mLayerClient);
        }
        return mMessageSenderRepository;
    }
}
