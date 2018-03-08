package com.layer.xdk.ui.message;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;

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
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.recyclerview.OnItemLongClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class MessageItemsListViewModel extends BaseObservable {
    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final int DEFAULT_PREFETCH_DISTANCE = 60;

    private LayerClient mLayerClient;
    private MessageModelAdapter mAdapter;
    private Conversation mConversation;
    private Predicate mQueryPredicate;
    private BinderRegistry mBinderRegistry;
    private LiveData<PagedList<MessageModel>> mMessageModelList;
    private Observer<PagedList<MessageModel>> mMessageModelListObserver;
    private boolean mInitialLoadComplete;

    public MessageItemsListViewModel(Context context, LayerClient layerClient,
                                     ImageCacheWrapper imageCacheWrapper,
                                     DateFormatter dateFormatter,
                                     IdentityFormatter identityFormatter) {
        mLayerClient = layerClient;
        mBinderRegistry = new BinderRegistry(context, layerClient);
        mAdapter = new MessageModelAdapter(layerClient, imageCacheWrapper, dateFormatter,
                identityFormatter);

        ActionHandlerRegistry.registerHandler(new OpenUrlActionHandler(layerClient));
        ActionHandlerRegistry.registerHandler(new GoogleMapsOpenMapActionHandler(layerClient));
        ActionHandlerRegistry.registerHandler(new OpenFileActionHandler(layerClient));

    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
        if (conversation != null) {
            mAdapter.setOneOnOneConversation(conversation.getParticipants().size() == 2);
            mAdapter.setReadReceiptsEnabled(conversation.isReadReceiptsEnabled());
            createAndObserveMessageModelList();
        }
        notifyChange();
    }

    @Bindable
    public MessageModelAdapter getAdapter() {
        return mAdapter;
    }

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

        mMessageModelList = new LivePagedListBuilder<>(
                new MessageModelDataSourceFactory(mLayerClient, mBinderRegistry, mConversation,
                        mQueryPredicate),
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

    @Bindable
    public boolean isInitialLoadComplete() {
        return mInitialLoadComplete;
    }
}
