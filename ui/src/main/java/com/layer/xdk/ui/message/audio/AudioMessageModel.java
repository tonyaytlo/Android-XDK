package com.layer.xdk.ui.message.audio;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.image.cache.ImageRequestParameters;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Message model to encapsulate audio.
 */
public class AudioMessageModel extends MessageModel {
    public static final String ROOT_MIME_TYPE = "application/vnd.layer.audio+json";
    private static final String ROLE_SOURCE = "source";
    private static final String ROLE_PREVIEW = "preview";
    private static final String DEFAULT_ACTION_EVENT = "layer-show-large-message";

    private AudioMessageMetadata mMetadata;
    private ImageRequestParameters mPreviewRequestParameters;

    private Uri mSourceUri;
    private boolean mDownloadingSourcePart;
    private AudioMetadataSlots mMetadataSlots;

    private Uri mAudioPartId;

    public AudioMessageModel(@NonNull Context context,
            @NonNull LayerClient layerClient,
            @NonNull Message message) {
        super(context, layerClient, message);
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        InputStreamReader inputStreamReader = new InputStreamReader(messagePart.getDataStream());
        JsonReader reader = new JsonReader(inputStreamReader);
        mMetadata = getGson().fromJson(reader, AudioMessageMetadata.class);
        if (mMetadata.mSourceUrl != null) {
            mSourceUri = Uri.parse(mMetadata.mSourceUrl);
            mAudioPartId = messagePart.getId();
        }
        mMetadataSlots = new AudioMetadataSlots(getAppContext());
        mMetadataSlots.compute(mMetadata);
        createPreviewRequest(null, mMetadata.mPreviewUrl);
        try {
            inputStreamReader.close();
        } catch (IOException e) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Failed to close input stream while parsing audio message", e);
            }
        }
    }

    @Override
    protected boolean parseChildPart(@NonNull MessagePart childMessagePart) {
        String role = MessagePartUtils.getRole(childMessagePart);
        if (role != null) {
            switch (role) {
                case ROLE_SOURCE:
                    mAudioPartId = childMessagePart.getId();
                    if (childMessagePart.getTransferStatus() == MessagePart.TransferStatus.COMPLETE) {
                        mSourceUri = childMessagePart.getFileUri(getAppContext());
                        // If there is no file then it must be inline. Create a data URI
                        if (mSourceUri == null) {
                            mSourceUri = createDataUri(childMessagePart);
                        }
                        if (mSourceUri == null) {
                            if (Log.isLoggable(Log.ERROR)) {
                                Log.e("Source part has neither inline nor external content");
                            }
                            throw new IllegalStateException(
                                    "Source part has neither inline nor external content");
                        }
                    }
                    mDownloadingSourcePart = false;
                    return true;
                case ROLE_PREVIEW:
                    createPreviewRequest(childMessagePart.getId(), null);
                    return true;
            }
        }
        return false;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_audio_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_standard_message_container;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        if (MessagePartUtils.isRole(messagePart, ROLE_SOURCE) && !messagePart.isContentReady()) {
            // Set the part ID here since the content is not ready
            mAudioPartId = messagePart.getId();
            mDownloadingSourcePart = true;
        }
        return true;
    }

    @Override
    public boolean getHasContent() {
        return mMetadata != null;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        String title = mMetadataSlots == null ? null : mMetadataSlots.getSlotB();
        return title != null ? title : getAppContext().getString(R.string.xdk_ui_audio_message_preview_text);
    }

    @Nullable
    @Override
    public String getTitle() {
        if (mDownloadingSourcePart) {
            return getAppContext().getString(R.string.xdk_ui_audio_message_model_downloading_title);
        }
        String title = mMetadataSlots == null ? null : mMetadataSlots.getSlotB();
        if (title != null) {
            return title;
        }

        return getAppContext().getString(R.string.xdk_ui_audio_message_model_default_title);
    }

    @Nullable
    @Override
    public String getDescription() {
        return mMetadataSlots == null ? null : mMetadataSlots.getSlotC();
    }

    @Nullable
    @Override
    public String getFooter() {
        return mMetadataSlots == null ? null : mMetadataSlots.getSlotD();
    }

    @Override
    public int getBackgroundColor() {
        return R.color.xdk_ui_color_primary_gray;
    }

    @Override
    public String getActionEvent() {
        if (super.getActionEvent() != null) {
            return super.getActionEvent();
        }

        if (mMetadata.mAction != null) {
            return mMetadata.mAction.getEvent();
        } else {
            return DEFAULT_ACTION_EVENT;
        }
    }

    @NonNull
    @Override
    public JsonObject getActionData() {
        if (super.getActionData().size() > 0) {
            return super.getActionData();
        }

        if (mMetadata != null && mMetadata.mAction != null) {
            return mMetadata.mAction.getData();
        }

        return new JsonObject();
    }

    /**
     * @return request parameters for the associated preview image
     */
    public ImageRequestParameters getPreviewRequestParameters() {
        return mPreviewRequestParameters;
    }

    /**
     * @return the audio source URI if is remote or if it has been downloaded locally, false
     * otherwise
     */
    @Nullable
    public Uri getSourceUri() {
        return mSourceUri;
    }

    /**
     * @return true if currently downloading the external content audio file, false otherwise
     */
    public boolean isDownloadingSourcePart() {
        return mDownloadingSourcePart;
    }

    /**
     * @return The ID of the {@link MessagePart} that contains the audio URL or data
     */
    public Uri getAudioPartId() {
        return mAudioPartId;
    }

    /**
     * @return metadata fields ordered for display
     */
    @Nullable
    public ArrayList<String> getOrderedMetadata() {
        return mMetadataSlots == null ? null : mMetadataSlots.getOrderedMetadata();
    }

    /**
     * @return the metadata of this model, null if the message has not been parsed yet
     */
    @Nullable
    public AudioMessageMetadata getMetadata() {
        return mMetadata;
    }

    /**
     * Creates a data Uri so inline content can be played in a media player
     *
     * @param sourceMessagePart message part that contains the audio data
     * @return a Uri that has the data base 64 encoded and the mime type for the part
     */
    @Nullable
    private Uri createDataUri(@NonNull MessagePart sourceMessagePart) {
        byte[] data = sourceMessagePart.getData();
        if (data != null) {
            String uri = String.format("data:%s;base64,%s",
                    MessagePartUtils.getMimeType(sourceMessagePart),
                    Base64.encodeToString(data, Base64.DEFAULT));
            return Uri.parse(uri);
        }
        return null;
    }

    private void createPreviewRequest(@Nullable Uri partId, @Nullable String url) {
        ImageRequestParameters.Builder builder = new ImageRequestParameters.Builder();
        if (partId != null) {
            builder.uri(partId);
        } else if (url != null) {
            builder.url(mMetadata.mPreviewUrl);
        }

        if (mMetadata.getPreviewWidth() > 0 && mMetadata.getPreviewHeight() > 0) {
            builder.resize(mMetadata.getPreviewWidth(), mMetadata.getPreviewHeight());
        }
        mPreviewRequestParameters = builder.build();
    }
}
