package com.layer.xdk.ui.conversation;

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
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.MessageItemsListViewModel;
import com.layer.xdk.ui.message.adapter2.MessagesDataSourceFactory;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class ConversationViewModel extends BaseObservable {
    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final int DEFAULT_PREFETCH_DISTANCE = 60;

    private Conversation mConversation;
    private MessageItemsListViewModel mMessageItemsListViewModel;
    private LayerClient mLayerClient;
    private Predicate mQueryPredicate;
    private BinderRegistry mBinderRegistry;
    private LiveData<PagedList<MessageModel>> mMessageModelList;
    private Observer<PagedList<MessageModel>> mMessageModelListObserver;


    public ConversationViewModel(Context context,
            LayerClient layerClient,
            ImageCacheWrapper imageCacheWrapper,
            DateFormatter dateFormatter,
            IdentityFormatter identityFormatter) {
        mBinderRegistry = new BinderRegistry(context, layerClient);
        mMessageItemsListViewModel = new MessageItemsListViewModel(layerClient,
                imageCacheWrapper, dateFormatter, identityFormatter);
        mLayerClient = layerClient;

    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
        createAndObserveMessageModelList();
        notifyChange();
    }

    /**
     * Set a custom predicate to use during the query for messages instead of the default.
     *
     * @param queryPredicate predicate to use for message query
     */
    @SuppressWarnings("unused")
    public void setQueryPredicate(@Nullable Predicate queryPredicate) {
        mQueryPredicate = queryPredicate;
        createAndObserveMessageModelList();
        notifyChange();
    }

    @Bindable
    public LayerClient getLayerClient() {
        return mLayerClient;
    }

    @Bindable
    public Conversation getConversation() {
        return mConversation;
    }

    public MessageItemsListViewModel getMessageItemsListViewModel() {
        return mMessageItemsListViewModel;
    }

    public void setOnItemClickListener(OnItemClickListener<Message> itemClickListener) {
        mMessageItemsListViewModel.setItemClickListener(itemClickListener);
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
                new MessagesDataSourceFactory(getLayerClient(), mBinderRegistry, mConversation,
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
                mMessageItemsListViewModel.getAdapter().submitList(messages);
            }
        };
        mMessageModelList.observeForever(mMessageModelListObserver);
    }
}
