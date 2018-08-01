package com.layer.xdk.ui.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import javax.inject.Inject;

/**
 * Asynchronously fetches a message part and updates based on changes.
 */
public class MessagePartFetcher {

    private final LayerClient mLayerClient;
    private boolean mObserveMessageChanges;

    private final MutableLiveData<MessagePart> mPart = new MutableLiveData<>();
    private final ChangeListener mChangeListener = new ChangeListener();

    /**
     * @param layerClient Layer client used for querying
     */
    @Inject
    public MessagePartFetcher(LayerClient layerClient) {
        mLayerClient = layerClient;
    }

    /**
     * Queries for a message part and registers for change events on that part and optionally, the
     * message.
     *
     * @param partId ID of the part to query for
     */
    public void fetchMessagePart(Uri partId) {
        mChangeListener.mPartId = partId;
        mLayerClient.registerEventListener(mChangeListener);

        new FetchMessagePartTask(mLayerClient, partId, mPart).execute();

        if (mObserveMessageChanges) {
            mPart.observeForever(new Observer<MessagePart>() {
                @Override
                public void onChanged(@Nullable MessagePart messagePart) {
                    if (messagePart != null) {
                        Message message = messagePart.getMessage();
                        mChangeListener.mMessageId = message.getId();
                    }
                    mPart.removeObserver(this);
                }
            });
        }
    }

    /**
     * @return LiveData representing the message part
     */
    public LiveData<MessagePart> getPart() {
        return mPart;
    }

    /**
     * Sets whether the part's observer should be notified if the message has changed. Default is
     * false.
     *
     * @param observeMessageChanges true if the part's message changes should be observed, false
     *                              otherwise
     */
    public void setObserveMessageChanges(boolean observeMessageChanges) {
        mObserveMessageChanges = observeMessageChanges;
    }

    /**
     * Unregisters listeners on the {@link LayerClient}
     */
    public void cleanUp() {
        mLayerClient.unregisterEventListener(mChangeListener);
    }

    /**
     * Listens for changes to the message and message part.
     */
    private class ChangeListener implements LayerChangeEventListener.BackgroundThread {
        private Uri mPartId;
        private Uri mMessageId;

        @Override
        public void onChangeEvent(LayerChangeEvent layerChangeEvent) {
            for (LayerChange change : layerChangeEvent.getChanges()) {
                switch (change.getObjectType()) {
                    case MESSAGE:
                        Message message = (Message) change.getObject();
                        if (message.getId().equals(mMessageId)) {
                            // Re-post the value as it should be the same object
                            mPart.postValue(mPart.getValue());
                        }
                        break;
                    case MESSAGE_PART:
                        MessagePart part = (MessagePart) change.getObject();
                        if (part.getId().equals(mPartId)) {
                            // Invalidate
                            mPart.postValue(part);
                        }
                        break;

                }
            }
        }
    }
}
