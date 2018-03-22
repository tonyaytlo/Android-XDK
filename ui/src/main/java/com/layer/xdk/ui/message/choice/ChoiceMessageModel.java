package com.layer.xdk.ui.message.choice;

import android.content.Context;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.response.ResponseSummary;
import com.layer.xdk.ui.util.json.AndroidFieldNamingStrategy;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChoiceMessageModel extends MessageModel {
    public static final String MIME_TYPE = "application/vnd.layer.choice+json";

    private ChoiceMessageMetadata mMetadata;
    private Gson mGson;
    private ChoiceStateSummary mChoiceStateSummary;
    private ChoiceClickDelegate mChoiceClickDelegate;

    public ChoiceMessageModel(Context context, LayerClient layerClient, Message message) {
        super(context, layerClient, message);
        mGson = new GsonBuilder().setFieldNamingStrategy(new AndroidFieldNamingStrategy()).create();
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_choice_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_titled_message_container;
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        JsonReader reader = new JsonReader(new InputStreamReader(messagePart.getDataStream()));
        mMetadata = mGson.fromJson(reader, ChoiceMessageMetadata.class);
        mMetadata.setEnabledForMe(getAuthenticatedUserId());
        mChoiceStateSummary = new ChoiceStateSummary(getMessage(), mGson,
                Collections.<ChoiceConfigMetadata>singleton(mMetadata));
        notifyPropertyChanged(BR.choiceMessageMetadata);
    }

    @Override
    protected void processResponseSummaryPart(@NonNull MessagePart responseSummaryPart) {
        JsonReader reader = new JsonReader(new InputStreamReader(responseSummaryPart.getDataStream()));
        ResponseSummary responseSummary = mGson.fromJson(reader, ResponseSummary.class);
        mChoiceStateSummary.processRemoteResponseSummary(responseSummary);
        notifyPropertyChanged(BR.selectedChoices);
    }


    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    @Nullable
    @Override
    public String getTitle() {
        String title = getAppContext().getString(R.string.xdk_ui_choice_message_model_default_title);
        if (mMetadata != null && mMetadata.mTitle != null) {
            title = mMetadata.mTitle;
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

        return null;
    }

    @Override
    public boolean getHasContent() {
        return mMetadata != null;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        if (getHasContent() && mMetadata.mChoices.size() > 0) {
            return getTitle();
        }
        return null;
    }

    @Nullable
    @Bindable
    public ChoiceMessageMetadata getChoiceMessageMetadata() {
        return mMetadata;
    }

    @Bindable
    public String getLabel() {
        return mMetadata != null ? mMetadata.mLabel : null;
    }

    @Bindable
    public Set<String> getSelectedChoices() {
        return new HashSet<>(mChoiceStateSummary.getSelectedChoices(mMetadata.getResponseName()));
    }

    @Bindable
    public boolean getIsEnabledForMe() {
        return mMetadata.mEnabledForMe;
    }

    void onChoiceClicked(@NonNull ChoiceMetadata choice, boolean selected, @NonNull Set<String> selectedChoices) {
        String userId = getLayerClient().getAuthenticatedUser().getUserId();
        mChoiceStateSummary.handleUserSelectionChange(mMetadata.getResponseName(), selectedChoices,
                userId);
        getChoiceClickDelegate().sendResponse(mMetadata, choice, selected, selectedChoices);
    }

    private ChoiceClickDelegate getChoiceClickDelegate() {
        if (mChoiceClickDelegate == null) {
            String userName = getIdentityFormatter().getDisplayName(
                    getLayerClient().getAuthenticatedUser());
            mChoiceClickDelegate = new ChoiceClickDelegate(userName, getAppContext(),
                    getRootMessagePart().getId(), getMessageSenderRepository(), getMessage());
        }
        return mChoiceClickDelegate;
    }
}
