package com.layer.xdk.ui.message.audio;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Formatter;

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

public class AudioMessageModel extends MessageModel {
    public static final String ROOT_MIME_TYPE = "application/vnd.layer.audio+json";
    private static final String ROLE_SOURCE = "source";
    private static final String ROLE_TRANSCRIPT = "transcript";
    private static final String ROLE_PREVIEW = "preview";

    private AudioMessageMetadata mMetadata;
    // TODO not sure if we need this flag
    private boolean mUsesSourceData;
    private ImageRequestParameters mPreviewRequestParameters;

    private String mTitle;
    private String mDescription;
    private String mFooter;
    private List<String> mDisplaySlots;


    public AudioMessageModel(@NonNull Context context,
            @NonNull LayerClient layerClient,
            @NonNull Message message) {
        super(context, layerClient, message);
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        parseRootMessagePart(messagePart);
    }

    @Override
    protected boolean parseChildPart(@NonNull MessagePart childMessagePart) {
        if (MessagePartUtils.isRole(childMessagePart, ROLE_SOURCE)) {
            // TODO Consume the source part
            mUsesSourceData = true;
            Log.w("TODO Received message part with source audio, need to process");

            return true;
        }
        return false;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_audio_message_layout;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_standard_message_container;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        // TODO should we download this?
        return false;
    }

    @Override
    public boolean getHasContent() {
        return mMetadata != null || mUsesSourceData;
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

    private void parseRootMessagePart(MessagePart messagePart) {
        InputStreamReader inputStreamReader = new InputStreamReader(messagePart.getDataStream());
        JsonReader reader = new JsonReader(inputStreamReader);
        mMetadata = getGson().fromJson(reader, AudioMessageMetadata.class);
        computeSlots(mMetadata);

        ImageRequestParameters.Builder previewBuilder = new ImageRequestParameters.Builder()
                .placeHolder(R.drawable.xdk_ui_file_audio);

        if (TextUtils.isEmpty(mMetadata.mPreviewUrl)) {
            previewBuilder.resourceId(R.drawable.xdk_ui_file_audio);
        } else {
            previewBuilder.url(mMetadata.mPreviewUrl);
        }

        mPreviewRequestParameters = previewBuilder.build();
    }

    private void computeSlots(@NonNull AudioMessageMetadata metadata) {
        mDisplaySlots = new ArrayList<>();
        Queue<String> slotB = createSlotBQueue(metadata, mDisplaySlots);
        Queue<String> slotC = createSlotCQueue(metadata, mDisplaySlots);
        Queue<String> slotD = createSlotDQueue(metadata, mDisplaySlots);

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
