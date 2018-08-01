package com.layer.xdk.ui.repository;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.os.AsyncTask;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.Queryable;
import com.layer.xdk.ui.util.Log;

import java.util.List;

/**
 * Fetch the message part from the LayerClient and post it to the live data object.
 */
public class FetchMessagePartTask extends AsyncTask<Void, Void, Void> {

    private final MutableLiveData<MessagePart> mMessagePart;
    private final LayerClient mLayerClient;
    private final Uri mMessagePartId;

    /**
     * @param layerClient layer client instance to query with
     * @param messagePartId the ID of the message to query
     * @param messagePart live data to post results to
     */
    public FetchMessagePartTask(LayerClient layerClient, Uri messagePartId,
            MutableLiveData<MessagePart> messagePart) {
        mLayerClient = layerClient;
        mMessagePartId = messagePartId;
        mMessagePart = messagePart;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<? extends Queryable> parts = mLayerClient.executeQueryForObjects(
                Query.builder(MessagePart.class)
                        .predicate(new Predicate(MessagePart.Property.ID,
                                Predicate.Operator.EQUAL_TO,
                                mMessagePartId))
                        .build());
        if (parts.size() == 1) {
            mMessagePart.postValue(((MessagePart) parts.get(0)));
        } else if (Log.isLoggable(Log.ERROR)) {
            Log.e("Failed to find part ID: " + mMessagePartId);
            mMessagePart.postValue(null);
        }
        return null;
    }
}
