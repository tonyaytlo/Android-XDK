package com.layer.xdk.ui.analytics;

import android.support.annotation.NonNull;

import com.layer.sdk.analytics.LayerAnalyticsEvent;
import com.layer.sdk.messaging.Message;

/**
 * CarouselScrolledEvent is raised when @CarouselMessageModel is scrolled.
 */
public class CarouselScrolledEvent extends LayerAnalyticsEvent {
    private final Message mMessage;

    private final int mScrollX;

    public CarouselScrolledEvent(@NonNull Message message, int scrollX) {
        mMessage = message;
        mScrollX = scrollX;
    }

    /**
     * Get access to Message event for which this event was generated.
     *
     * @return Message instance for which this event was generated
     */
    public Message getMessage() {
        return mMessage;
    }

    /**
     * Get access to the horizontal scroll distance. This is the value of @ScrollView.getScrollX
     * @return int value of ScrollX
     */
    public int getmScrollX() {
        return mScrollX;
    }

    @Override
    public String toString() {
        return "CarouselScrolledEvent{" +
                "mMessage=" + mMessage +
                ", mScrollX=" + mScrollX +
                '}';
    }
}
