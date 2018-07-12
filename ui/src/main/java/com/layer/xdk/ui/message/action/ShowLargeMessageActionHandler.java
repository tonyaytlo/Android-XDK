package com.layer.xdk.ui.message.action;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.audio.AudioMessageModel;
import com.layer.xdk.ui.message.image.cache.ImageRequestParameters;
import com.layer.xdk.ui.message.large.LargeAudioMessageFragment;
import com.layer.xdk.ui.message.large.LargeMessageActivity;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

/**
 * Action handler responsible for showing large message versions for given models
 */
public class ShowLargeMessageActionHandler extends ActionHandler {

    private static final String ACTION_EVENT_SHOW_LARGE_MESSAGE = "layer-show-large-message";

    public ShowLargeMessageActionHandler() {
        super(null, ACTION_EVENT_SHOW_LARGE_MESSAGE);
    }

    @Override
    public void performAction(@NonNull Context context, @NonNull MessageModel model) {
        Intent intent;
        if (model instanceof AudioMessageModel) {
            intent = createAudioIntent(context, model);
        } else {
            if (Log.isLoggable(Log.INFO)) {
                Log.i(ShowLargeMessageActionHandler.class.getSimpleName() +
                        " does not handle a large view of this model: " + model);
            }
            return;
        }

        context.startActivity(intent);
    }

    @NonNull
    private Intent createAudioIntent(@NonNull Context context, @NonNull MessageModel model) {
        Intent intent = new Intent(context, LargeMessageActivity.class);

        AudioMessageModel audioModel = (AudioMessageModel) model;

        intent.putExtra(LargeAudioMessageFragment.ARG_MESSAGE_PART_ID, audioModel.getAudioPartId().toString());
        if (audioModel.getSourceUri() != null) {
            intent.putExtra(LargeAudioMessageFragment.ARG_SOURCE_URI,
                    audioModel.getSourceUri().toString());
        }
        intent.putExtra(LargeAudioMessageFragment.ARG_ORDERED_METADATA, audioModel.getOrderedMetadata());
        ImageRequestParameters imageParams = audioModel.getPreviewRequestParameters();
        if (imageParams.getUri() != null) {
            intent.putExtra(LargeAudioMessageFragment.ARG_PREVIEW_URL, imageParams.getUri().toString());
        }
        if (imageParams.getUrl() != null) {
            intent.putExtra(LargeAudioMessageFragment.ARG_PREVIEW_URL, imageParams.getUrl());
        }
        if (imageParams.getTargetWidth() > 0) {
            intent.putExtra(LargeAudioMessageFragment.ARG_IMAGE_WIDTH, imageParams.getTargetWidth());
        }
        if (imageParams.getTargetHeight() > 0) {
            intent.putExtra(LargeAudioMessageFragment.ARG_IMAGE_HEIGHT, imageParams.getTargetHeight());
        }

        intent.putExtra(LargeMessageActivity.ARG_TITLE, R.string.xdk_ui_audio_message_model_default_title);
        return intent;
    }
}
