package com.layer.xdk.ui.media;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.layer.xdk.ui.util.Log;

/**
 * Service that contains a {@link MediaSessionCompat} and player to handle media playback. This is
 * a bound service so if not using as such, ensure to stop the service when finished using it.
 */
public class MediaPlaybackService extends Service {

    private final IBinder mBinder = new MediaPlaybackBinder();
    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSession;
    private int mBoundCounter;

    public MediaPlaybackService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        initializeSession();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            if (Log.isLoggable(Log.VERBOSE)) {
                Log.v("Releasing media player");
            }
            mMediaPlayer.release();
        }
        if (mMediaSession != null) {
            if (Log.isLoggable(Log.VERBOSE)) {
                Log.v("Releasing media session");
            }
            mMediaSession.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBoundCounter++;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBoundCounter--;
        if (mBoundCounter == 0) {
            stopSelf();
        }
        return false;
    }

    private void initializeSession() {
        mMediaSession = new MediaSessionCompat(this, "MessageItemsListSession");
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);

        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SEEK_TO)
                .build());

        mMediaSession.setCallback(new MultiPlaybackCallback(this, mMediaPlayer, mMediaSession));
    }

    /**
     * Return the token associated with the {@link MediaSessionCompat} so controllers can interact
     * with it.
     *
     * @return the token for the session or null if the session hasn't been created yet
     */
    @Nullable
    public MediaSessionCompat.Token getSessionToken() {
        if (mMediaSession != null) {
            return mMediaSession.getSessionToken();
        }
        return null;
    }

    /**
     * Return the instance of this service so clients can interact with it.
     */
    public class MediaPlaybackBinder extends Binder {
        @NonNull
        MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }
}
