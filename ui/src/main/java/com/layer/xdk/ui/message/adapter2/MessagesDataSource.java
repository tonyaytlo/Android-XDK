package com.layer.xdk.ui.message.adapter2;


import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.internal.lsdkd.lsdka.i;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.LayerObject;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.Queryable;
import com.layer.sdk.query.SortDescriptor;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesDataSource extends PositionalDataSource<MessageModel> {

    static int ID_SOURCE = 0;
    private final int id = ID_SOURCE++;

    private LayerClient mLayerClient;
    private Conversation mConversation;
    private LayerChangeEventListener.BackgroundThread.Weak listener;
    private BinderRegistry mBinderRegistry;


    public MessagesDataSource(LayerClient layerClient, final Conversation conversation,
            BinderRegistry binderRegistry) {
        mLayerClient = layerClient;
        mConversation = conversation;
        mBinderRegistry = binderRegistry;

        Log.d("ZZZZ Creating datasource: " + id);

        listener = new LayerChangeEventListener.BackgroundThread.Weak() {
            @Override
            public void onChangeEvent(LayerChangeEvent layerChangeEvent) {
                List<LayerChange> changes = layerChangeEvent.getChanges();
                boolean needsInvalidation = false;
                for (LayerChange change : changes) {
                    if (change.getObjectType() == LayerObject.Type.MESSAGE) {
                        Message message = (Message) change.getObject();
                        if (message.getConversation().equals(conversation)) {
                            needsInvalidation = true;
                        }
                    }
                }
                if (needsInvalidation) {
                    mLayerClient.unregisterEventListener(listener);

                    // TODO these are too quick and it's still picking up the old changes. Delay?
                    Log.d("ZZZZ Invalidating datasource: " + id);
                    invalidate();
                }
            }
        };

        mLayerClient.registerEventListener(listener);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<MessageModel> callback) {
        int count = (int) computeCount();
        if (count == 0) {
            callback.onResult(Collections.<MessageModel>emptyList(), 0, 0);
        } else {
            int position = computeInitialLoadPosition(params, count);
            int size = computeInitialLoadSize(params, position, count);
            Log.d("ZZZZ ID:" + id + " Load initial position: " + position + " size: " + size + " params: " + paramsToString(params));

            List<Message> messages = loadRangeInternal(position, size);
            if (messages != null && messages.size() == size) {
                callback.onResult(convertMessagesToModels(messages), position, count);
            } else {
                invalidate();
            }
        }
    }

    private String paramsToString(LoadInitialParams params) {
        return "LoadInitialParams{" +
                "requestedStartPosition=" + params.requestedStartPosition +
                ", requestedLoadSize=" + params.requestedLoadSize +
                ", pageSize=" + params.pageSize +
                ", placeholdersEnabled=" + params.placeholdersEnabled +
                '}';
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadRangeInternal(int position, int loadSize) {
        List<? extends Queryable> messages = mLayerClient.executeQueryForObjects(Query.builder(
                Message.class)
                .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO,
                        mConversation))
                .sortDescriptor(new SortDescriptor(Message.Property.POSITION,
                        SortDescriptor.Order.DESCENDING))
                .offset(position)
                .limit(loadSize)
                .build());
        Log.d("ZZZZ ID:" + id + " Queried for messages at pos: " + position + " with size: " + loadSize + " and retrieved message count: " + messages.size());
        List<String> databaseIds = new ArrayList<>();
        for (Queryable message : messages) {
            databaseIds.add(((i) message).i().toString());
        }
        Log.d("ZZZZZ Message Db ids:\n" + TextUtils.join("\n", databaseIds));
        return (List<Message>) messages;
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params,
            @NonNull LoadRangeCallback<MessageModel> callback) {
        Log.d("ZZZZ ID:" + id + " Load range start position: " + params.startPosition + " load size: " + params.loadSize);

        List<Message> messages = loadRangeInternal(params.startPosition, params.loadSize);
        callback.onResult(convertMessagesToModels(messages));
    }

    private long computeCount() {
        Long count = mLayerClient.executeQueryForCount(Query.builder(Message.class)
                .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO, mConversation))
                .build());
        if (count == null) {
            return 0L;
        }
        return count;
    }

    @NonNull
    private List<MessageModel> convertMessagesToModels(List<Message> messages) {
        List<MessageModel> models = new ArrayList<>();
        for (Message message : messages) {
            String rootMimeType = MessagePartUtils.getRootMimeType(message);
            // TODO handle nulls (create legacy MessageModel)
            MessageModel model = mBinderRegistry.getMessageModelManager().getNewModel(rootMimeType);
            model.setMessageModelManager(mBinderRegistry.getMessageModelManager());
            model.setMessage(message);
            models.add(model);
        }
        return models;
    }
}
