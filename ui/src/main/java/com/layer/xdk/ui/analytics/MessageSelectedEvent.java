package com.layer.xdk.ui.analytics;

import android.support.annotation.NonNull;

import com.layer.sdk.analytics.LayerAnalyticsEvent;
import com.layer.sdk.messaging.Message;

/**
 * MessageSelected Event is raised when user performs a tap / click action on an actionable
 * item in a Message
 */
public class MessageSelectedEvent extends LayerAnalyticsEvent {
    private final Message mMessage;

    public MessageSelectedEvent(@NonNull Message message) {
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
        return "MessageSelectedEvent{" +
                "mMessage=" + mMessage +
                '}';
    }
}
