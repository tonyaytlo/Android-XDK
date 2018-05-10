package com.layer.xdk.test.common.stub;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class MessagePartStub extends MessagePart {

    public Uri mId = Uri.parse("layer:///messages/" + UUID.randomUUID() + "/parts/" + UUID.randomUUID());
    public byte[] mData;
    public String mMimeType;
    public boolean mContentReady = true;

    @Override
    public Uri getId() {
        return mId;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public String getMimeType() {
        return mMimeType;
    }

    @Override
    public byte[] getData() {
        return mData;
    }

    @Override
    public InputStream getDataStream() {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public TransferStatus getTransferStatus() {
        return null;
    }

    @Override
    public void download(LayerProgressListener layerProgressListener) {

    }

    @Override
    public boolean isContentReady() {
        return mContentReady;
    }

    @Override
    public void deleteLocalContent() {

    }

    @Nullable
    @Override
    public Date getUpdatedAt() {
        return null;
    }
}
