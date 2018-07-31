package com.layer.xdk.ui.media;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.layer.xdk.ui.util.Log;

import javax.inject.Inject;

/**
 * Provides a {@link MediaControllerCompat} for use with handling media playback from several
 * different message parts. Uses a {@link MultiPlaybackCallback} instance.
 */
public class MultiPlaybackMediaControllerProvider implements MediaControllerProvider, LifecycleObserver {

    private Context mAppContext;
    private MutableLiveData<MediaControllerCompat> mMediaController;
    private boolean mServiceBound;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBound = true;
            MediaPlaybackService.MediaPlaybackBinder binder =
                    (MediaPlaybackService.MediaPlaybackBinder) service;
            if (binder.getSessionToken() == null) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e("Session token should be non-null when the service is started");
                }
                throw new IllegalStateException("Session token should be non-null when the service "
                        + "is started");
            }
            try {
                mMediaController.postValue(new MediaControllerCompat(mAppContext, binder.getSessionToken()));
            } catch (RemoteException e) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e("Cannot obtain media controller", e);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            mMediaController.postValue(null);
        }
    };

    @Inject
    public MultiPlaybackMediaControllerProvider(Context appContext) {
        mAppContext = appContext;
    }

    /**
     * Called when the lifecycle owner is paused. Pauses playback if it is currently happening
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (mMediaController == null) {
            return;
        }
        MediaControllerCompat controller = mMediaController.getValue();
        if (controller != null && controller.getPlaybackState() != null) {
            switch (controller.getPlaybackState().getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                case PlaybackStateCompat.STATE_BUFFERING:
                    controller.getTransportControls().pause();
                    break;
            }
        }
    }

    /**
     * Called when the lifecycle owner is destroyed. Releases objects associated with media playback
     * if they have been created.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (mServiceBound) {
            mAppContext.unbindService(mServiceConnection);
        }
    }

    /**
     * Starts a service that handles the playback. Once that service is created a media controller
     * will be created with the appropriate token. This uses the {@link MultiPlaybackCallback} to
     * handle playback between multiple messages with the same player/session/controller.
     *
     * @return a LiveData object that encapsulates the controller instance managed by this provider.
     * This will be null until the service has been started.
     */
    @NonNull
    public LiveData<MediaControllerCompat> getMediaController() {
        if (mMediaController == null) {
            mMediaController = new MutableLiveData<>();

            Intent intent = new Intent(mAppContext, MediaPlaybackService.class);
            mAppContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mAppContext.startService(intent);
        }
        return mMediaController;
    }

    @Override
    public void addLifecycleObserver(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }
}
