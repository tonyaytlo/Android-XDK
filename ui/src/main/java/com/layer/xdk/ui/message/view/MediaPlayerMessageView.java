package com.layer.xdk.ui.message.view;

import com.layer.xdk.ui.media.MediaControllerProvider;

/**
 * Allows a message view to use a provided media controller for audio/video playback when used
 * with a {@link com.layer.xdk.ui.message.adapter.MessageModelAdapter} instance.
 */
public interface MediaPlayerMessageView {

    /**
     * This will be called after inflation to set the provider of the media controller. A media
     * controller can be fetched from this provider any time after instantiation.
     *
     * @param provider provider that supplies a media controller
     */
    void setMediaControllerProvider(MediaControllerProvider provider);
}
