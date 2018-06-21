package com.layer.xdk.ui.message;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v4.media.session.MediaControllerCompat;

/**
 * Lazily provides a {@link MediaControllerCompat}.
 */
public interface MediaControllerProvider extends LifecycleObserver {

    /**
     * Retrieve a controller for media playback.
     *
     * @return controller to use for media playback
     */
    MediaControllerCompat getMediaController();

    /**
     * Register this observer with a lifecycle owner. This allows the provider to know if it has
     * been registered or not. If it has not been registered then a warning will be logged when
     * retrieving the controller via {@link #getMediaController()}.
     *
     * @param lifecycleOwner the owner to register this observer with
     */
    void addLifecycleObserver(LifecycleOwner lifecycleOwner);

}
