package com.layer.xdk.ui.analytics;

import android.support.annotation.NonNull;

import com.layer.sdk.analytics.LayerAnalyticsEvent;
import com.layer.sdk.messaging.Message;

/**
 * MessageViewedEvent is raised when users views a message that is not their own.
 */
public class MessageViewedEvent extends LayerAnalyticsEvent {
    private final Message mMessage;

    public MessageViewedEvent(@NonNull Message message) {
        mMessage = message;
    }

    /**
     * Get access to Message event for which this event was generated.
     *
     * @return Message instance for which this event was generated
     */
    public Message getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        return "MessageViewedEvent{" +
                "mMessage=" + mMessage +
                '}';
    }
}
