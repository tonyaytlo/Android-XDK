package com.layer.xdk.ui.message.audio;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.text.format.Formatter;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.MetadataSlots;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class AudioMetadataSlots extends MetadataSlots<AudioMessageMetadata> {

    public AudioMetadataSlots(Context appContext) {
        super(appContext);
    }

    @NonNull
    @Override
    protected Queue<String> createSlotBQueue(@NonNull AudioMessageMetadata metadata,
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
            setUsingDefaultSlotB();
            String defaultTitle = getAppContext().getString(
                    R.string.xdk_ui_audio_message_model_default_title);
            slot.add(defaultTitle);
            overallOrder.add(defaultTitle);
        }
        return slot;
    }

    @NonNull
    @Override
    protected Queue<String> createSlotCQueue(@NonNull AudioMessageMetadata metadata,
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
    @Override
    protected Queue<String> createSlotDQueue(@NonNull AudioMessageMetadata metadata,
            @NonNull List<String> overallOrder) {
        Queue<String> slot = new ArrayDeque<>(2);
        if (metadata.mDuration > 0) {
            String duration =  DateUtils.formatElapsedTime(Math.round(metadata.mDuration));
            slot.add(duration);
            overallOrder.add(duration);
        }
        if (metadata.mSize > 0L) {
            String size = Formatter.formatShortFileSize(getAppContext(), metadata.mSize);
            slot.add(size);
            overallOrder.add(size);
        }
        return slot;
    }
}
