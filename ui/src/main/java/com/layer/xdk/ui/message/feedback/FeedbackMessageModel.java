package com.layer.xdk.ui.message.feedback;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.response.ResponseSummaryMetadataV2;
import com.layer.xdk.ui.message.response.crdt.OrOperationResult;
import com.layer.xdk.ui.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class FeedbackMessageModel extends MessageModel {
    public static final String ROOT_MIME_TYPE = "application/vnd.layer.feedback+json";
    private static final int INVALID_RATING = -1;
    private static final String RESPONSE_KEY_RATING = "rating";
    private static final String RESPONSE_KEY_COMMENT = "comment";
    private static final String DEFAULT_ACTION_EVENT = "layer-show-large-message";

    private FeedbackOrSetHelper mOrSetHelper;
    private FeedbackMessageMetadata mMetadata;
    private Date mResponseDate;

    private Integer mRequestedRating;

    public FeedbackMessageModel(@NonNull Context context,
            @NonNull LayerClient layerClient,
            @NonNull Message message) {
        super(context, layerClient, message);
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        InputStreamReader inputStreamReader = new InputStreamReader(messagePart.getDataStream());
        JsonReader reader = new JsonReader(inputStreamReader);
        mMetadata = getGson().fromJson(reader, FeedbackMessageMetadata.class);
        try {
            inputStreamReader.close();
        } catch (IOException e) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Failed to close input stream while parsing feedback message", e);
            }
        }

        mMetadata.setEnabledForMe(getAuthenticatedUserId());

        if (getRootMessagePart() == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Root part needs to be set before parsing this feedback model");
            }
            throw new IllegalStateException(
                    "Root part needs to be set before parsing this feedback model");
        }

        mOrSetHelper = new FeedbackOrSetHelper(getGson(), getRootMessagePart());
    }

    @Override
    protected void parseResponseSummary(MessagePart messagePart) {
        super.parseResponseSummary(messagePart);
        mResponseDate = messagePart.getUpdatedAt();
        // There is already a rating so clear the requested rating
        mRequestedRating = null;
    }

    @Override
    protected void processResponseSummaryMetadata(@NonNull ResponseSummaryMetadataV2 metadata) {
        mOrSetHelper.processRemoteResponseSummary(metadata);
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_feedback_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_titled_message_container;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    @Override
    public boolean getHasContent() {
        return mMetadata != null;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        return getTitle();
    }

    @Nullable
    @Override
    public String getTitle() {
        String title = mMetadata == null ? null : mMetadata.mTitle;
        if (title == null) {
            title = getAppContext().getString(R.string.xdk_ui_feedback_message_default_title);
        }
        return title;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Nullable
    @Override
    public String getFooter() {
        return null;
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
     * @return true if the user can still submit feedback, false otherwise
     */
    public boolean isEditable() {
        return mMetadata != null && mMetadata.mEnabledForMe && getRating() == INVALID_RATING;
    }

    /**
     * @return the current rating or -1 if no rating has been made yet
     */
    public int getRating() {
        if (mMetadata != null) {
            Set<String> selections = mOrSetHelper.getSelections(mMetadata.mEnabledFor,
                    RESPONSE_KEY_RATING);
            if (!selections.isEmpty()) {
                return Integer.parseInt(selections.iterator().next());
            }
        }
        return INVALID_RATING;
    }

    /**
     * @return the current comment or null if no comment was or has been made
     */
    @Nullable
    public String getComment() {
        if (mMetadata != null) {
            Set<String> selections = mOrSetHelper.getSelections(mMetadata.mEnabledFor,
                    RESPONSE_KEY_COMMENT);
            if (!selections.isEmpty()) {
                return selections.iterator().next();
            }
        }
        return null;
    }

    /**
     * @return the root message part's ID or null if this model has not been parsed yet
     */
    @Nullable
    public Uri getRootPartId() {
        if (getRootMessagePart() != null) {
            return getRootMessagePart().getId();
        }
        return null;
    }

    /**
     * Sets a rating that is used as a transient holder before feedback is submitted.
     *
     * @param requestedRating rating requested by a user action
     */
    public void setRequestedRating(Integer requestedRating) {
        mRequestedRating = requestedRating;
    }

    /**
     * @return the rating used as a transient holder before feedback is submitted
     */
    public Integer getRequestedRating() {
        return mRequestedRating;
    }

    /**
     * @return the summary text for display
     */
    @Nullable
    public String getSummary() {
        if (mMetadata != null) {

            if (getRating() == INVALID_RATING) {
                if (mMetadata.mEnabledForMe) {
                    return mMetadata.mPrompt != null ? mMetadata.mPrompt : getAppContext()
                            .getString(R.string.xdk_ui_feedback_message_default_prompt);
                } else {
                    return mMetadata.mPromptWait != null ? mMetadata.mPromptWait : getAppContext()
                            .getString(R.string.xdk_ui_feedback_message_default_prompt_wait);
                }
            } else if (getRating() != INVALID_RATING && mResponseDate != null) {
                return String.format("%s %s", getDateFormatter().formatTimeDay(mResponseDate),
                        getDateFormatter().formatTime(mResponseDate));
            }
        }
        return null;
    }

    /**
     * @return text to populate the comment's hint field
     */
    public String getCommentHint() {
        String hint = mMetadata == null ? null : mMetadata.mCommentHint;
        if (hint == null) {
            hint = getAppContext().getString(R.string.xdk_ui_feedback_message_default_comment_hint);
        }
        return hint;
    }

    /**
     * @return the metadata of this model, null if the message has not been parsed yet
     */
    @Nullable
    public FeedbackMessageMetadata getMetadata() {
        return mMetadata;
    }

    /**
     * Send the feedback with the rating, optional comment and a status message.
     *
     * @param rating rating of the feedback
     * @param comment optional comment string
     */
    void sendFeedback(int rating, @Nullable String comment) {
        Uri identityId = getAuthenticatedUserId();
        if (identityId == null) {
            if (Log.isLoggable(Log.WARN)) {
                Log.w("Unable to process feedback with no authenticated user");
            }
            return;
        }
        if (getRootPartId() == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Unable to process feedback when message hasn't been parsed");
            }
            throw new IllegalStateException(
                    "Unable to process feedback when message hasn't been parsed");
        }

        List<OrOperationResult> results = new ArrayList<>(2);
        results.addAll(mOrSetHelper.processLocalSelection(identityId, RESPONSE_KEY_RATING, true, String.valueOf(rating)));
        if (!TextUtils.isEmpty(comment)) {
            results.addAll(mOrSetHelper.processLocalSelection(identityId, RESPONSE_KEY_COMMENT, true, comment));
        }

        SendFeedbackDelegate delegate = new SendFeedbackDelegate(getAppContext(), getRootPartId(), getMessageSenderRepository(),
                getMessage());
        delegate.sendResponse(results);
    }
}
