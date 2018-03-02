package com.layer.xdk.ui.message.model;


import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.DateFormatterImpl;
import com.layer.xdk.ui.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractMessageModel extends BaseObservable {

    // TODO AND-1242 I don't think we need this anymore
    private final AtomicInteger mDownloadingPartCounter;

    // TODO AND-1287 Inject these and make them non static. Making static for now to reduce allocs
    // TODO AND-1242 change prefix to s
    private static IdentityFormatter mIdentityFormatter;
    private static DateFormatter mDateFormatter;

    private final Context mContext;
    private final LayerClient mLayerClient;

    private final Message mMessage;

    // It's safe to cache this since no model will live after a de-auth
    private final Uri mAuthenticatedUserId;
    private final Uri mSenderId;

    private int mParticipantCount;

    public AbstractMessageModel(Context context, LayerClient layerClient, @NonNull Message message) {
        mContext = context.getApplicationContext();
        if (mIdentityFormatter == null) {
            mIdentityFormatter = new IdentityFormatterImpl(mContext);
        }
        if (mDateFormatter == null) {
            mDateFormatter = new DateFormatterImpl(mContext);
        }
        mDownloadingPartCounter = new AtomicInteger();
        mLayerClient = layerClient;

        mMessage = message;
        Identity authenticatedUser = layerClient.getAuthenticatedUser();
        mAuthenticatedUserId = authenticatedUser == null ? null : authenticatedUser.getId();
        Identity sender = message.getSender();
        mSenderId = sender == null ? null : sender.getId();

        mParticipantCount = mMessage.getConversation().getParticipants().size();
    }

    /**
     * Provide a tree of mime types that correspond to all the message parts. Usually
     * this should not be overridden. If it is then build the tree as follows
     *  1. The root level parts should be comma separated
     *  2. If a part has children, those mime types should be comma separated and enclosed in
     *  square brackets (i.e. []).
     *
     * @return A string representing the mime type tree of all message parts
     */
    public abstract String getMimeTypeTree();

    /**
     * Provides the layout resource ID of the view to inflate into the container.
     *
     * @return layout resource ID to inflate into the container. If no layout is associated, 0
     * should be returned.
     */
    @LayoutRes
    public abstract int getViewLayoutId();

    /**
     * Provides the layout resource ID of the container for this model that will be inflated into
     * a ViewHolder. If no layout is associated, 0 should be returned.
     *
     * @return layout resource ID to inflate into the ViewHolder. If no layout is associated, 0
     * should be returned.
     */
    @LayoutRes
    public abstract int getContainerViewLayoutId();

    protected void download(@NonNull MessagePart messagePart) {
        // TODO AND-1242 - I don't think we need to set a listener as the part should be updated, thus updating the data source. Verify this.
        messagePart.download(null);
    }

    protected abstract boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart);


    @Bindable
    @Nullable
    public abstract String getPreviewText();

    @Nullable
    @Bindable
    public abstract String getTitle();

    @Nullable
    @Bindable
    public abstract String getDescription();

    @Nullable
    @Bindable
    public abstract String getFooter();

    @Bindable
    @ColorRes
    public int getBackgroundColor() {
        return R.color.transparent;
    }

    // TODO AND-1242 rename to get application context
    protected Context getContext() {
        return mContext;
    }

    protected LayerClient getLayerClient() {
        return mLayerClient;
    }

    protected IdentityFormatter getIdentityFormatter() {
        return mIdentityFormatter;
    }

    public void setIdentityFormatter(IdentityFormatter identityFormatter) {
        mIdentityFormatter = identityFormatter;
    }

    protected DateFormatter getDateFormatter() {
        return mDateFormatter;
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        mDateFormatter = dateFormatter;
    }

    @NonNull
    public final Message getMessage() {
        return mMessage;
    }

    @Bindable
    public final boolean isMessageFromMe() {
        if (mAuthenticatedUserId != null) {
            return mAuthenticatedUserId.equals(mSenderId);
        }
        if (Log.isLoggable(Log.ERROR)) {
            Log.e("Failed to check if message is from me. Authenticated user is null Message: "
                    + getMessage());
        }
        throw new IllegalStateException("Failed to check if message is from me. Authenticated "
                + "user is null Message: " + getMessage());
    }

    @Nullable
    public final Uri getAuthenticatedUserId() {
        return mAuthenticatedUserId;
    }

    @Nullable
    public final Uri getSenderId() {
        return mSenderId;
    }

    public final int getParticipantCount() {
        return mParticipantCount;
    }
}
