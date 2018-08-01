package com.layer.xdk.ui.message.feedback;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.XdkUiDependencyManager;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.model.MessageModelManager;
import com.layer.xdk.ui.repository.MessagePartFetcher;
import com.layer.xdk.ui.util.Log;

import javax.inject.Inject;

/**
 * ViewModel that handles a display of a large feedback message.
 */
public class LargeFeedbackViewModel extends ViewModel {

    @Inject
    LayerClient mLayerClient;

    @Inject
    MessageModelManager mMessageModelManager;

    @Inject
    MessagePartFetcher mMessagePartFetcher;

    private Integer mRequestedRating;

    public final ObservableBoolean mEditable = new ObservableBoolean();
    public final ObservableField<String> mComment = new ObservableField<>();


    public ObservableField<FeedbackMessageModel> mMessageModel = new ObservableField<>();

    private final Observer<MessagePart> mMessagePartObserver = new Observer<MessagePart>() {
        @Override
        public void onChanged(@Nullable MessagePart messagePart) {
            if (messagePart != null) {
                MessageModel model = mMessageModelManager.getNewModel(
                        FeedbackMessageModel.ROOT_MIME_TYPE,
                        messagePart.getMessage());
                if (model instanceof FeedbackMessageModel) {
                    mMessageModel.set((FeedbackMessageModel) model);
                } else {
                    if (Log.isLoggable(Log.ERROR)) {
                        Log.e("Requested message part is not a feedback message. Model: " + model);
                    }
                    throw new IllegalArgumentException(
                            "Requested message part is not a feedback message. Model: " + model);
                }

                FeedbackMessageModel feedbackModel = mMessageModel.get();
                if (!messagePart.isContentReady()) {
                    messagePart.download(null);
                }
                feedbackModel.processParts(messagePart);
                mComment.set(feedbackModel.getComment());
                mEditable.set(feedbackModel.isEditable());
                if (feedbackModel.isEditable()) {
                    feedbackModel.setRequestedRating(mRequestedRating);
                }
            }
        }
    };

    public LargeFeedbackViewModel() {
        XdkUiDependencyManager.INSTANCE.getXdkUiComponent().inject(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        mMessagePartFetcher.getPart().removeObserver(mMessagePartObserver);
        mMessagePartFetcher.cleanUp();
    }

    /**
     * Set the extras passed in the activity/fragment creation to initialize member variables.
     *
     * @param extras bundle that was passed in the intent/arguments for starting the activity or
     *               fragment
     */
    public void setExtras(@NonNull Bundle extras) {
        String messagePartId = extras.getString(LargeFeedbackMessageFragment.ARG_MESSAGE_PART_ID);
        if (extras.containsKey(LargeFeedbackMessageFragment.ARG_REQUESTED_RATING)) {
            mRequestedRating = extras.getInt(LargeFeedbackMessageFragment.ARG_REQUESTED_RATING);
        }

        mMessagePartFetcher.setObserveMessageChanges(true);
        mMessagePartFetcher.getPart().observeForever(mMessagePartObserver);
        mMessagePartFetcher.fetchMessagePart(Uri.parse(messagePartId));
    }

    /**
     * Send the feedback with a rating and comment.
     *
     * @param rating feedback rating
     * @param comment optional feedback comment
     */
    public void send(int rating, @Nullable String comment) {
        mMessageModel.get().sendFeedback(rating, comment);
    }
}
