package com.layer.xdk.ui.message.action;

import android.content.Context;
import android.support.annotation.NonNull;

import com.layer.xdk.ui.message.choice.ChoiceMetadata;
import com.layer.xdk.ui.message.model.MessageModel;

import java.util.HashMap;
import java.util.Map;

public class ActionHandlerRegistry {
    private static Map<String, ActionHandler> sActionHandlers = new HashMap<>();
    private static Map<String, ChoiceHandler> sChoiceHandlers = new HashMap<>();

    public static void registerHandler(ActionHandler handler) {
        sActionHandlers.put(handler.getEvent(), handler);
    }

    public static void dispatchEvent(Context context, @NonNull String event, @NonNull MessageModel model)
            throws UnsupportedOperationException {
        if (sActionHandlers.containsKey(event)) {
            sActionHandlers.get(event).performAction(context, model);
        } else {
            throw new UnsupportedOperationException("No registered action handler for event: " + event);
        }
    }

    @SuppressWarnings("unused")
    public static void registerChoiceHandler(ChoiceHandler handler) {
        sChoiceHandlers.put(handler.getChoiceId(), handler);
    }

    public static void dispatchChoiceSelection(@NonNull Context context, ChoiceMetadata choice,
            MessageModel model, MessageModel rootModel) {
        if (sChoiceHandlers.containsKey(choice.mId)) {
            sChoiceHandlers.get(choice.mId).onChoiceSelect(context, choice, model, rootModel);
        }
    }
}
