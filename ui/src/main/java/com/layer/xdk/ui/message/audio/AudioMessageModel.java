package com.layer.xdk.ui.message.audio;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.Base64;

import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.image.cache.ImageRequestParameters;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Message model to encapsulate audio.
 */
public class AudioMessageModel extends MessageModel {
    public static final String ROOT_MIME_TYPE = "application/vnd.layer.audio+json";
    private static final String ROLE_SOURCE = "source";
    private static final String ROLE_TRANSCRIPT = "transcript";
    private static final String ROLE_PREVIEW = "preview";

    private AudioMessageMetadata mMetadata;
    private ImageRequestParameters mPreviewRequestParameters;

    private String mTitle;
    private String mDescription;
    private String mFooter;
    private Uri mSourceUri;
    private boolean mDownloadingSourcePart;

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
        }
        computeSlots(mMetadata);
        createPreviewRequest(null, mMetadata.mPreviewUrl);
    }

    @Override
    protected boolean parseChildPart(@NonNull MessagePart childMessagePart) {
        String role = MessagePartUtils.getRole(childMessagePart);
        if (role != null) {
            switch (role) {
                case ROLE_SOURCE:
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
        String title = mMetadata == null ? null : mMetadata.mTitle;
        return title != null ? title : getAppContext().getString(R.string.xdk_ui_audio_message_preview_text);
    }

    @Nullable
    @Override
    public String getTitle() {
        if (mDownloadingSourcePart) {
            return getAppContext().getString(R.string.xdk_ui_audio_message_model_downloading_title);
        }
        if (mTitle != null) {
            return mTitle;
        }

        return getAppContext().getString(R.string.xdk_ui_audio_message_model_default_title);
    }

    @Nullable
    @Override
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    @Override
    public String getFooter() {
        return mFooter;
    }

    @Override
    public int getBackgroundColor() {
        return R.color.xdk_ui_color_primary_gray;
    }

    public ImageRequestParameters getPreviewRequestParameters() {
        return mPreviewRequestParameters;
    }

    @Nullable
    public Uri getSourceUri() {
        return mSourceUri;
    }

    public boolean isDownloadingSourcePart() {
        return mDownloadingSourcePart;
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
        } else {
            builder.resourceId(R.drawable.xdk_ui_file_audio);
        }

        builder.placeHolder(R.drawable.xdk_ui_file_audio);

        if (mMetadata.getPreviewWidth() > 0 && mMetadata.getPreviewHeight() > 0) {
            builder.resize(mMetadata.getPreviewWidth(), mMetadata.getPreviewHeight());
        }
        mPreviewRequestParameters = builder.build();
    }

    /**
     * Determine what data should go in the title, description and footer locations based on
     * available metadata.
     *
     * @param metadata metadata for this model
     */
    private void computeSlots(@NonNull AudioMessageMetadata metadata) {
        List<String> displaySlots = new ArrayList<>();
        Queue<String> slotB = createSlotBQueue(metadata, displaySlots);
        Queue<String> slotC = createSlotCQueue(metadata, displaySlots);
        Queue<String> slotD = createSlotDQueue(metadata, displaySlots);

        mTitle = slotB.isEmpty() ? null : slotB.remove();

        mDescription = slotC.isEmpty() ? null : slotC.remove();
        if (mDescription == null) {
            // Promotion attempt
            mDescription = slotD.isEmpty() ? null : slotD.remove();
        }
        if (mDescription == null) {
            // Demotion attempt
            mDescription = slotB.isEmpty() ? null : slotB.remove();
        }

        mFooter = slotD.isEmpty() ? null : slotD.remove();
        if (mFooter == null) {
            // Demotion attempt
            mFooter = slotC.isEmpty() ? null : slotC.remove();
        }
        if (mFooter == null) {
            // Demotion attempt
            mFooter = slotB.isEmpty() ? null : slotB.remove();
        }
    }

    @NonNull
    private Queue<String> createSlotBQueue(@NonNull AudioMessageMetadata metadata,
            @NonNull List<String> overallOrder) {
        Queue<String> slot = new ArrayDeque<>(3);
        if (metadata.mTitle != null) {
            slot.add(metadata.mTitle);
            overallOrder.add(metadata.mTitle);
        }
        if (metadata.mSourceUrl != null) {
            String lastPath = Uri.parse(metadata.mSourceUrl).getLastPathSegment();
            if (lastPath != null) {
                slot.add(lastPath);
                overallOrder.add(lastPath);
            }
        }
        if (slot.isEmpty()) {
            String defaultTitle = getAppContext().getString(
                    R.string.xdk_ui_audio_message_model_default_title);
            slot.add(defaultTitle);
            overallOrder.add(defaultTitle);
        }
        return slot;
    }

    @NonNull
    private Queue<String> createSlotCQueue(@NonNull AudioMessageMetadata metadata,
            @NonNull List<String> overallOrder) {
        Queue<String> slot = new ArrayDeque<>(3);
        if (metadata.mArtist != null) {
            slot.add(metadata.mArtist);
            overallOrder.add(metadata.mArtist);
        }
        if (metadata.mAlbum != null) {
            slot.add(metadata.mAlbum);
            overallOrder.add(metadata.mAlbum);
        }
        if (metadata.mGenre != null) {
            slot.add(metadata.mGenre);
            overallOrder.add(metadata.mGenre);
        }
        return slot;
    }

    @NonNull
    private Queue<String> createSlotDQueue(@NonNull AudioMessageMetadata metadata,
            @NonNull List<String> overallOrder) {
        Queue<String> slot = new ArrayDeque<>(2);
        if (metadata.mDuration > 0) {
            String duration =  DateUtils.formatElapsedTime(Math.round(mMetadata.mDuration));
            slot.add(duration);
            overallOrder.add(duration);
        }
        if (metadata.mSize > 0L) {
            String size = Formatter.formatShortFileSize(getAppContext(), mMetadata.mSize);
            slot.add(size);
            overallOrder.add(size);
        }
        return slot;
    }
}
