package com.layer.xdk.ui.message;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.action.ActionHandlerRegistry;
import com.layer.xdk.ui.message.action.GoogleMapsOpenMapActionHandler;
import com.layer.xdk.ui.message.action.OpenFileActionHandler;
import com.layer.xdk.ui.message.action.OpenUrlActionHandler;
import com.layer.xdk.ui.message.adapter.MessageModelAdapter;
import com.layer.xdk.ui.message.adapter.MessageModelDataSourceFactory;
import com.layer.xdk.ui.message.image.cache.ImageCacheWrapper;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.recyclerview.OnItemLongClickListener;
import com.layer.xdk.ui.util.Log;

import javax.inject.Inject;

/**
 * A ViewModel to drive a list of {@link com.layer.sdk.messaging.Message} objects
 */
public class MessageItemsListViewModel extends BaseObservable implements LifecycleObserver {
    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final int DEFAULT_PREFETCH_DISTANCE = 60;

    private MessageModelAdapter mAdapter;
    private Conversation mConversation;
    private Predicate mQueryPredicate;
    private LiveData<PagedList<MessageModel>> mMessageModelList;
    private Observer<PagedList<MessageModel>> mMessageModelListObserver;
    private MessageModelDataSourceFactory mDataSourceFactory;
    private IdentityFormatter mIdentityFormatter;
    private boolean mInitialLoadComplete;

    private MediaPlayer mMediaPlayer;

    private MediaControllerCompat mMediaController;
    private MediaSessionCompat mMediaSession;

    @Inject
    public MessageItemsListViewModel(@NonNull LayerClient layerClient,
            @NonNull MessageModelAdapter messageModelAdapter,
            @NonNull MessageModelDataSourceFactory dataSourceFactory,
            @NonNull IdentityFormatter identityFormatter,
            @NonNull ImageCacheWrapper imageCacheWrapper) {
        mAdapter = messageModelAdapter;
        mAdapter.setMediaControllerProvider(new MediaControllerProvider() {
            @Override
            public MediaControllerCompat getMediaController(Context context) {
                return MessageItemsListViewModel.this.getMediaController(context);
            }
        });
        mDataSourceFactory = dataSourceFactory;
        mIdentityFormatter = identityFormatter;

        ActionHandlerRegistry.registerHandler(new OpenUrlActionHandler(layerClient, imageCacheWrapper));
        ActionHandlerRegistry.registerHandler(new GoogleMapsOpenMapActionHandler(layerClient));
        ActionHandlerRegistry.registerHandler(new OpenFileActionHandler(layerClient));
    }

    /**
     * Sets the {@link Conversation} from which {@link com.layer.sdk.messaging.Message}s are
     * displayed
     *
     * @param conversation a {@link Conversation} instance to drive this list
     */
    public void setConversation(Conversation conversation) {
        mConversation = conversation;
        if (conversation != null) {
            mAdapter.setOneOnOneConversation(conversation.getParticipants().size() == 2);
            mAdapter.setReadReceiptsEnabled(conversation.isReadReceiptsEnabled());
            createAndObserveMessageModelList();
        }
        notifyChange();
    }

    /**
     * Gets the {@link MessageModelAdapter} instance set on this object
     *
     * @return the {@link MessageModelAdapter} instance set on this object
     */
    @Bindable
    public MessageModelAdapter getAdapter() {
        return mAdapter;
    }

    @Bindable
    public IdentityFormatter getIdentityFormatter() {
        return mIdentityFormatter;
    }

    /**
     * Called when the lifecycle owner is paused. Pauses playback if it is currently happening
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (mMediaController != null && mMediaController.getPlaybackState() != null) {
            switch (mMediaController.getPlaybackState().getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                case PlaybackStateCompat.STATE_BUFFERING:
                    mMediaController.getTransportControls().pause();
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
        mMediaPlayer = null;
        mMediaSession = null;
        mMediaController = null;
    }

    /**
     * Set an {@link OnItemLongClickListener} to be fired when items in the conversation list are
     * long clicked
     *
     * @param listener the {@link OnItemLongClickListener} instance to be used
     */
    public void setItemLongClickListener(OnItemLongClickListener<MessageModel> listener) {
        mAdapter.setItemLongClickListener(listener);
    }

    /**
     * Set a custom predicate to use during the query for messages instead of the default.
     *
     * @param queryPredicate predicate to use for message query
     */
    @SuppressWarnings("unused")
    public void setQueryPredicate(@Nullable Predicate queryPredicate) {
        mQueryPredicate = queryPredicate;
        // Only re-create the list if the conversation has already been set. Else just rely on the
        // initial creation to happen when the conversation is set.
        if (mConversation != null) {
            createAndObserveMessageModelList();
            notifyChange();
        }
    }

    /**
     * Creates the {@link PagedList} and observes for changes so the adapter can be updated. If a
     * {@link PagedList} already exists then the observer will be removed before creating a new one.
     */
    private void createAndObserveMessageModelList() {
        // Remove observer if this is an update
        if (mMessageModelList != null) {
            mMessageModelList.removeObserver(mMessageModelListObserver);
        }
        mDataSourceFactory.setConversation(mConversation, mQueryPredicate);

        mMessageModelList = new LivePagedListBuilder<>(mDataSourceFactory,
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setPageSize(DEFAULT_PAGE_SIZE)
                        .setPrefetchDistance(DEFAULT_PREFETCH_DISTANCE)
                        .build()
        ).build();

        mMessageModelListObserver = new Observer<PagedList<MessageModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<MessageModel> messages) {
                if (!mInitialLoadComplete) {
                    mInitialLoadComplete = true;
                    notifyPropertyChanged(BR.initialLoadComplete);
                }
                mAdapter.submitList(messages);
            }
        };
        mMessageModelList.observeForever(mMessageModelListObserver);
    }

    /**
     * @return true if the initial loading is complete
     */
    @Bindable
    public boolean isInitialLoadComplete() {
        return mInitialLoadComplete;
    }

    /**
     * Creates a {@link MediaPlayer}, {@link MediaSessionCompat} and {@link MediaControllerCompat}
     * if none have been created. This uses the {@link MultiPlaybackCallback} to handle playback
     * between multiple messages with the same player/session/controller.
     *
     * @param context the Context to use for the session and controller
     * @return the controller instance managed by this view model
     */
    @NonNull
    private MediaControllerCompat getMediaController(Context context) {
        if (mMediaController == null) {

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaSession = new MediaSessionCompat(context,
                    "MessageItemsListSession");
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mMediaSession.setMediaButtonReceiver(null);

            mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_PLAY_PAUSE)
                    .build());

            mMediaSession.setCallback(new MultiPlaybackCallback(context, mMediaPlayer, mMediaSession));

            mMediaController = new MediaControllerCompat(context, mMediaSession);

            if (context instanceof LifecycleOwner) {
                ((LifecycleOwner) context).getLifecycle().addObserver(this);
            } else if (Log.isLoggable(Log.INFO)) {
                Log.i("Encapsulating activity/fragment is not a lifecycle owner. Ensure this "
                        + MessageItemsListViewModel.class.getSimpleName() + " is manually registered.");
            }
        }

        return mMediaController;
    }

    /**
     * Provides a {@link MediaControllerCompat} given a {@link Context}, ideally from an
     * {@link android.app.Activity}
     */
    public interface MediaControllerProvider {

        /**
         * Retrieve a controller for media playback.
         *
         * @param context the context for the session and controller, usually an Activity
         * @return controller to use for media playback
         */
        MediaControllerCompat getMediaController(Context context);
    }
}
