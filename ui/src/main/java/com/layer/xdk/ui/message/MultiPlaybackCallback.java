package com.layer.xdk.ui.message;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
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
 * of states (playback state/position/duration) for the lifetime of an instance.
 */
public class MultiPlaybackCallback extends MediaSessionCompat.Callback {
    public static final String EXTRA_KEY_ACTIVE_MESSAGE_ID = "com.layer.xdk.ui.ACTIVE_MESSAGE_ID";
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
    private String mActiveMessageId;

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

        mStateExtras = new Bundle();
        mPlaybackStateBuilder.setExtras(mStateExtras);
    }

    @Override
    public void onPrepareFromUri(Uri uri, Bundle extras) {
        String messageId = extras.getString(EXTRA_KEY_ACTIVE_MESSAGE_ID);

        if (messageId != null && messageId.equals(mActiveMessageId)) {
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
        mCurrentPlaybackSavedState = mStateExtras.getParcelable(messageId);
        if (mCurrentPlaybackSavedState == null) {
            mCurrentPlaybackSavedState = new PlaybackSavedState();
        }

        // Set new active message
        mActiveMessageId = messageId;
        mStateExtras.putString(EXTRA_KEY_ACTIVE_MESSAGE_ID, messageId);

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
        mStateExtras.putParcelable(mActiveMessageId, mCurrentPlaybackSavedState);
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

    /**
     * Container class to add relevant playback details to a Bundle.
     */
    public static class PlaybackSavedState implements Parcelable {
        /**
         * Duration of the audio
         */
        public int mDuration;

        /**
         * Current playback position
         */
        public int mPlaybackPosition;

        /**
         * Current playback state
         */
        @PlaybackStateCompat.State
        public int mPlaybackState = PlaybackStateCompat.STATE_NONE;

        private PlaybackSavedState() {
        }

        private PlaybackSavedState(Parcel in) {
            mDuration = in.readInt();
            mPlaybackPosition = in.readInt();
            mPlaybackState = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mDuration);
            dest.writeInt(mPlaybackPosition);
            dest.writeInt(mPlaybackState);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<PlaybackSavedState> CREATOR = new Parcelable.Creator<PlaybackSavedState>() {
            @Override
            public PlaybackSavedState createFromParcel(Parcel in) {
                return new PlaybackSavedState(in);
            }

            @Override
            public PlaybackSavedState[] newArray(int size) {
                return new PlaybackSavedState[size];
            }
        };
    }
}
