package com.layer.xdk.ui.message.action;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.audio.AudioMessageModel;
import com.layer.xdk.ui.message.image.cache.ImageRequestParameters;
import com.layer.xdk.ui.message.large.LargeMediaMessageFragment;
import com.layer.xdk.ui.message.large.LargeMessageActivity;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.video.VideoMessageModel;
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
            intent = createAudioIntent(context, (AudioMessageModel) model);
        } else if (model instanceof VideoMessageModel) {
            intent = createVideoIntent(context, (VideoMessageModel) model);
        } else {
            if (Log.isLoggable(Log.INFO)) {
                Log.i(ShowLargeMessageActionHandler.class.getSimpleName() +
                        " does not handle a large view of this model: " + model);
            }
            return;
        }

        context.startActivity(intent);
    }

    private Intent createVideoIntent(@NonNull Context context, @NonNull VideoMessageModel model) {
        Intent intent = new Intent(context, LargeMessageActivity.class);

        intent.putExtra(LargeMediaMessageFragment.ARG_MESSAGE_PART_ID, model.getVideoPartId().toString());
        if (model.getSourceUri() != null) {
            intent.putExtra(LargeMediaMessageFragment.ARG_SOURCE_URI,
                    model.getSourceUri().toString());
        }
        if (model.hasNonDefaultOrderedMetadata()) {
            intent.putExtra(LargeMediaMessageFragment.ARG_ORDERED_METADATA, model.getOrderedMetadata());
        }
        intent.putExtra(LargeMediaMessageFragment.ARG_IS_VIDEO, true);
        if (model.getMetadata() != null) {
            intent.putExtra(LargeMediaMessageFragment.ARG_MEDIA_WIDTH, model.getMetadata().getWidth());
            intent.putExtra(LargeMediaMessageFragment.ARG_MEDIA_HEIGHT, model.getMetadata().getHeight());
            intent.putExtra(LargeMediaMessageFragment.ARG_MEDIA_ASPECT_RATIO, model.getMetadata().getAspectRatio());
        }

        intent.putExtra(LargeMessageActivity.ARG_TITLE, R.string.xdk_ui_video_message_model_default_title);
        return intent;
    }

    @NonNull
    private Intent createAudioIntent(@NonNull Context context, @NonNull AudioMessageModel model) {
        Intent intent = new Intent(context, LargeMessageActivity.class);


        intent.putExtra(LargeMediaMessageFragment.ARG_MESSAGE_PART_ID, model.getAudioPartId().toString());
        if (model.getSourceUri() != null) {
            intent.putExtra(LargeMediaMessageFragment.ARG_SOURCE_URI,
                    model.getSourceUri().toString());
        }
        intent.putExtra(LargeMediaMessageFragment.ARG_ORDERED_METADATA, model.getOrderedMetadata());
        ImageRequestParameters imageParams = model.getPreviewRequestParameters();
        if (imageParams.getUri() != null) {
            intent.putExtra(LargeMediaMessageFragment.ARG_PREVIEW_URL, imageParams.getUri().toString());
        }
        if (imageParams.getUrl() != null) {
            intent.putExtra(LargeMediaMessageFragment.ARG_PREVIEW_URL, imageParams.getUrl());
        }
        if (imageParams.getTargetWidth() > 0) {
            intent.putExtra(LargeMediaMessageFragment.ARG_MEDIA_WIDTH, imageParams.getTargetWidth());
        }
        if (imageParams.getTargetHeight() > 0) {
            intent.putExtra(LargeMediaMessageFragment.ARG_MEDIA_HEIGHT, imageParams.getTargetHeight());
        }

        intent.putExtra(LargeMessageActivity.ARG_TITLE, R.string.xdk_ui_audio_message_model_default_title);
        return intent;
    }
}
