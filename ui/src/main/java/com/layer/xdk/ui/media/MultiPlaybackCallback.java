package com.layer.xdk.ui.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.layer.xdk.ui.util.Log;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A callback for a media session that shares a player with multiple messages. This will keep track
 * of states (playback state/position/duration) for the lifetime of this instance. When playing
 * media from another message part, the part that is playing, if any, will be paused and the
 * new part will be played. The progress for part that was playing prior to the new part will be
 * saved in the extras on the {@link PlaybackStateCompat}.
 */
public class MultiPlaybackCallback extends MediaSessionCompat.Callback {
    public static final String EXTRA_KEY_ACTIVE_MESSAGE_PART_ID = "com.layer.xdk.ui.ACTIVE_MESSAGE_PART_ID";
    private static final String CONTENT_URI_SCHEME = "content";

    private static final float PLAYBACK_SPEED = 1.0f;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private Context mContext;
    private final MediaPlayer mPlayer;
    private final MediaSessionCompat mSession;
    private ScheduledFuture<?> mScheduledFuture;

    private PlaybackStateCompat.Builder mPlaybackStateBuilder = new PlaybackStateCompat.Builder();

    private Bundle mStateExtras;
    private boolean mPreparationRequired = true;
    private String mActiveMessagePartId;

    private PlaybackSavedState mCurrentPlaybackSavedState;

    /**
     * @param context Context to use when using content URIs
     * @param player the player that performs playback
     * @param session the session used for playback
     */
    MultiPlaybackCallback(Context context, MediaPlayer player,
            MediaSessionCompat session) {
        mContext = context;
        mPlayer = player;
        mSession = session;

        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e("Media playback failed. Error type: " + what + ". Extra code: " + extra);
                }

                updateCurrentPlaybackState(PlaybackStateCompat.STATE_ERROR, 0);
                return true;
            }
        });

        mStateExtras = new Bundle();
        mPlaybackStateBuilder.setExtras(mStateExtras)
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO);
    }

    @Override
    public void onPrepareFromUri(Uri uri, Bundle extras) {
        String messagePartId = extras.getString(EXTRA_KEY_ACTIVE_MESSAGE_PART_ID);

        if (messagePartId != null && messagePartId.equals(mActiveMessagePartId)) {
            // No need to prepare, we're already playing this
            mPreparationRequired = false;
            return;
        }
        mPreparationRequired = true;

        // Stop other playing if necessary and update state
        if (mSession.isActive()) {
            mSession.setActive(false);
            mPlayer.pause();
            updateCurrentPlaybackState(PlaybackStateCompat.STATE_PAUSED,
                    mPlayer.getCurrentPosition());
            mPlayer.reset();
        }

        // See if there is a saved state for the new message or create a new one
        mCurrentPlaybackSavedState = mStateExtras.getParcelable(messagePartId);
        if (mCurrentPlaybackSavedState == null) {
            mCurrentPlaybackSavedState = new PlaybackSavedState();
        }

        // Clear the buffering listener it requires the media to already be prepared
        mPlayer.setOnBufferingUpdateListener(null);

        // Set new active message
        mActiveMessagePartId = messagePartId;
        mStateExtras.putString(EXTRA_KEY_ACTIVE_MESSAGE_PART_ID, messagePartId);

        // Set new data source
        try {
            if (CONTENT_URI_SCHEME.equals(uri.getScheme())) {
                mPlayer.setDataSource(mContext, uri);
            } else {
                mPlayer.setDataSource(uri.toString());
            }
        } catch (IOException e) {
            Log.e("Failed to set data source: " + uri, e);
        }
    }

    @Override
    public void onPlay() {
        mSession.setActive(true);

        if (mPreparationRequired) {
            // Prepare new source
            updateCurrentPlaybackState(PlaybackStateCompat.STATE_BUFFERING, mCurrentPlaybackSavedState.mPlaybackPosition);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.setOnBufferingUpdateListener(new BufferingListener());
                    updateCurrentPlaybackState(PlaybackStateCompat.STATE_BUFFERING, mCurrentPlaybackSavedState.mPlaybackPosition, mp.getDuration());
                    mPreparationRequired = false;
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            updateCurrentPlaybackState(PlaybackStateCompat.STATE_STOPPED, mPlayer.getDuration(), mPlayer.getDuration());
                            stopProgressUpdating();
                        }
                    });

                    play();
                }
            });
            mPlayer.prepareAsync();
        } else {
            // Resume playing
            play();
        }
    }

    @Override
    public void onPause() {
        mPlayer.pause();
        stopProgressUpdating();
        updateCurrentPlaybackState(PlaybackStateCompat.STATE_PAUSED, mPlayer.getCurrentPosition());
    }

    @Override
    public void onStop() {
        mSession.setActive(false);
        mPlayer.stop();
        stopProgressUpdating();

        updateCurrentPlaybackState(PlaybackStateCompat.STATE_STOPPED, mPlayer.getCurrentPosition());
    }

    @Override
    public void onSeekTo(long pos) {
        mPlayer.seekTo((int) pos);
        updateCurrentPlaybackState(mCurrentPlaybackSavedState.mPlaybackState, ((int) pos));
    }

    private void stopProgressUpdating() {
        if (mScheduledFuture != null) {
            mScheduledFuture.cancel(true);
            mHandler.removeCallbacks(null);
        }
    }

    private void play() {
        if (mCurrentPlaybackSavedState.mPlaybackPosition == mCurrentPlaybackSavedState.mDuration) {
            // Start over if currently at the end
            mCurrentPlaybackSavedState.mPlaybackPosition = 0;
        }
        updateCurrentPlaybackState(PlaybackStateCompat.STATE_PLAYING, mCurrentPlaybackSavedState.mPlaybackPosition);

        mPlayer.seekTo(mCurrentPlaybackSavedState.mPlaybackPosition);
        mPlayer.start();
        startProgressUpdating();

    }

    private void startProgressUpdating() {
        if (!mExecutorService.isShutdown()) {
            mScheduledFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateCurrentPlaybackState(
                                            mCurrentPlaybackSavedState.mPlaybackState,
                                            mPlayer.getCurrentPosition());
                                }
                            });
                        }
                    }, 100,
                    1000, TimeUnit.MILLISECONDS);
        }
    }

    private void updateCurrentPlaybackState(@PlaybackStateCompat.State int state, int position) {
        updateCurrentPlaybackState(state, position, mCurrentPlaybackSavedState.mDuration);
    }

    private void updateCurrentPlaybackState(@PlaybackStateCompat.State int state, int position, int duration) {
        mCurrentPlaybackSavedState.mPlaybackState = state;
        if (position > duration) {
            // Never let the position be greater than the duration
            position = duration;
        }
        mCurrentPlaybackSavedState.mPlaybackPosition = position;
        mCurrentPlaybackSavedState.mDuration = duration;
        mStateExtras.putParcelable(mActiveMessagePartId, mCurrentPlaybackSavedState);
        notifyNewPlaybackState();
    }

    private void notifyNewPlaybackState() {
        PlaybackStateCompat state = mPlaybackStateBuilder
                .setState(
                        mCurrentPlaybackSavedState.mPlaybackState,
                        mCurrentPlaybackSavedState.mPlaybackPosition,
                        PLAYBACK_SPEED)
                .build();
        mSession.setPlaybackState(state);
    }

    private class BufferingListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentPlaybackSavedState.mBufferedPosition =
                    (int) (mp.getDuration() * ((double) percent / 100));
            notifyNewPlaybackState();
        }
    }
}
