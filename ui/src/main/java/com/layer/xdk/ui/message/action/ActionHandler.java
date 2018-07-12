package com.layer.xdk.ui.message.action;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.BuildConfig;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

public abstract class ActionHandler {
    private String mEvent;
    private LayerClient mLayerClient;

    @SuppressWarnings("WeakerAccess")
    public ActionHandler(LayerClient layerClient, String event) {
        mLayerClient = layerClient;
        mEvent = event;
    }

    public String getEvent() {
        return mEvent;
    }

    public LayerClient getLayerClient() {
        return mLayerClient;
    }

    public abstract void performAction(@NonNull Context context, @NonNull MessageModel model);

    protected void notifyUnresolvedIntent(@NonNull final Context context, final Intent intent) {
        if (Log.isLoggable(Log.WARN)) {
            Log.w("No activity can handle action's intent: " + intent);
        }
        if (BuildConfig.DEBUG) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.xdk_ui_unresolved_intent_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
