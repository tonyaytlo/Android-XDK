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
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesDataSource extends PositionalDataSource<MessageModel> {

    static int ID_SOURCE = 0;
    private final int id = ID_SOURCE++;

    private final GroupingCalculator mGroupingCalculator;
    private final LayerClient mLayerClient;
    private final Conversation mConversation;
    private final LayerChangeEventListener.BackgroundThread.Weak listener;
    private final BinderRegistry mBinderRegistry;

    public MessagesDataSource(LayerClient layerClient, final Conversation conversation,
            BinderRegistry binderRegistry, GroupingCalculator groupingCalculator) {
        mLayerClient = layerClient;
        mConversation = conversation;
        mBinderRegistry = binderRegistry;
        mGroupingCalculator = groupingCalculator;

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

            LoadRangeResults results = loadRangeInternal(position, size);
            if (results.mRealSize == size) {
                callback.onResult(convertMessagesToModels(results), position, count);
            } else {
                invalidate();
            }
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params,
            @NonNull LoadRangeCallback<MessageModel> callback) {
        Log.d("ZZZZ ID:" + id + " Load range start position: " + params.startPosition + " load size: " + params.loadSize);

        LoadRangeResults results = loadRangeInternal(params.startPosition, params.loadSize);
        callback.onResult(convertMessagesToModels(results));
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
    @SuppressWarnings("unchecked")
    private LoadRangeResults loadRangeInternal(int position, int requestedLoadSize) {
        LoadRangeResults results = new LoadRangeResults();
        // Load an additional after for cluster calculation
        int loadSizeForGrouping = requestedLoadSize + 1;
        // Load an additional before for cluster calculation

        if (position != 0) {
            results.mExtraAtBeginning = true;
            position--;
            loadSizeForGrouping++;
        }
        List<? extends Queryable> messages = mLayerClient.executeQueryForObjects(Query.builder(
                Message.class)
                .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO,
                        mConversation))
                .sortDescriptor(new SortDescriptor(Message.Property.POSITION,
                        SortDescriptor.Order.DESCENDING))
                .offset(position)
                .limit(loadSizeForGrouping) // Additional for clustering calculation
                .build());

        results.mMessages = (List<Message>) messages;

        // Determine if there is an extra at the end or not
        int resultSize = messages.size();
        if (results.mExtraAtBeginning) {
            resultSize--;
        }
        if (resultSize == requestedLoadSize + 1) {
            resultSize--;
            results.mExtraAtEnd = true;
        }
        results.mRealSize = resultSize;

//        Log.d("ZZZZ ID:" + id + " Queried for messages at pos: " + position + " with size: " + loadSize + " and retrieved message count: " + messages.size());
        List<String> databaseIds = new ArrayList<>();
        for (Queryable message : messages) {
            databaseIds.add(((i) message).i().toString());
        }
        Log.d("ZZZZZ Message Db ids:\n" + TextUtils.join("\n", databaseIds));
        return results;
    }

    @NonNull
    private List<MessageModel> convertMessagesToModels(LoadRangeResults loadResults) {
        List<MessageModel> models = new ArrayList<>();
        for (Message message : loadResults.mMessages) {
            MessageModel model = mBinderRegistry.getMessageModelManager().getNewModel(message);
            model.processParts();
//            Log.d("ZZZZ Created MimeTypeTree: " + model.getMimeTypeTree());

            models.add(model);
        }
        mGroupingCalculator.calculateGrouping(models);
        // Trim extras that were loaded for the grouping calc
        if (loadResults.mExtraAtBeginning) {
            models.remove(0);
        }
        if (loadResults.mExtraAtEnd) {
            models.remove(models.size() - 1);
        }

        return models;
    }

    private String paramsToString(LoadInitialParams params) {
        return "LoadInitialParams{" +
                "requestedStartPosition=" + params.requestedStartPosition +
                ", requestedLoadSize=" + params.requestedLoadSize +
                ", pageSize=" + params.pageSize +
                ", placeholdersEnabled=" + params.placeholdersEnabled +
                '}';
    }

    private static class LoadRangeResults {
        List<Message> mMessages;
        int mRealSize;
        boolean mExtraAtBeginning;
        boolean mExtraAtEnd;
    }
}
