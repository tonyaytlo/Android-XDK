package com.layer.xdk.ui.message.button;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.action.ActionHandlerRegistry;
import com.layer.xdk.ui.message.choice.ChoiceMetadata;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.response.ChoiceResponseModel;
import com.layer.xdk.ui.message.response.ResponseSummary;
import com.layer.xdk.ui.repository.MessageSenderRepository;
import com.layer.xdk.ui.util.json.AndroidFieldNamingStrategy;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ButtonMessageModel extends MessageModel {
    public static final String ROOT_MIME_TYPE = "application/vnd.layer.buttons+json";
    private static final String ROLE_CONTENT = "content";
    private Gson mGson;

    private ButtonMessageMetadata mMetadata;

    private ResponseSummary mResponseSummary;

    private Map<String, Set<String>> mSelectedChoiceIds = new HashMap<>();

    public ButtonMessageModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        mGson = new GsonBuilder().setFieldNamingStrategy(new AndroidFieldNamingStrategy()).create();
    }

    @Override
    public Class<ButtonMessageView> getRendererType() {
        return ButtonMessageView.class;
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        if (getRootMessagePart().equals(messagePart)) {
            JsonReader reader = new JsonReader(new InputStreamReader(messagePart.getDataStream()));
            mMetadata = mGson.fromJson(reader, ButtonMessageMetadata.class);

            // Populate choice data objects
            for (ButtonModel buttonModel : mMetadata.getButtonModels()) {
                if (ButtonModel.TYPE_CHOICE.equals(buttonModel.getType())) {
                    JsonObject data = buttonModel.getData();
                    if (data != null) {
                        ButtonModel.ChoiceData choiceData = mGson.fromJson(data,
                                ButtonModel.ChoiceData.class);
                        buttonModel.setChoiceData(choiceData);
                        choiceData.setEnabledForMe(getIsEnabledForMe(choiceData));

                        String responseName = choiceData.getResponseName();

                        Set<String> selectedChoices = getSelectedChoices(responseName);
                        selectedChoices.clear();
                        if (choiceData.getPreselectedChoice() != null) {
                            selectedChoices.add(choiceData.getPreselectedChoice());
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void processResponseSummaryPart(@NonNull MessagePart responseSummaryPart) {
        JsonReader reader = new JsonReader(new InputStreamReader(responseSummaryPart.getDataStream()));
        mResponseSummary = mGson.fromJson(reader, ResponseSummary.class);

        if (mResponseSummary != null && mResponseSummary.hasData()) {
            for (Map.Entry<String, JsonObject> participantResponses : mResponseSummary.getParticipantData().entrySet()) {
                for (String responseName : mSelectedChoiceIds.keySet()) {
                    if (participantResponses.getValue().has(responseName)) {
                        Set<String> selectedChoices = getSelectedChoices(responseName);
                        selectedChoices.clear();
                        String[] ids = participantResponses.getValue().get(
                                responseName).getAsString().split(",");
                        selectedChoices.addAll(Arrays.asList(ids));
                        // If nothing is selected then we can get an empty string. Remove this
                        selectedChoices.remove("");
                    }
                }
            }
        }
    }

    private boolean getIsEnabledForMe(ButtonModel.ChoiceData choiceData) {
        if (getLayerClient().getAuthenticatedUser() == null || mMetadata == null) {
            return false;
        }
        String myUserID = getLayerClient().getAuthenticatedUser().getId().toString();
        return choiceData.getEnabledFor() == null || choiceData.getEnabledFor().contains(myUserID);

    }

    @NonNull
    Set<String> getSelectedChoices(@NonNull String responseName) {
        Set<String> selectedChoices = mSelectedChoiceIds.get(responseName);
        if (selectedChoices == null) {
            selectedChoices = new HashSet<>();
            mSelectedChoiceIds.put(responseName, selectedChoices);
        }
        return selectedChoices;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
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

    @Nullable
    @Override
    public String getPreviewText() {
        if (getHasContent()) {
            String title = null;
            List<MessageModel> childMessageModels = getChildMessageModelsWithRole("content");
            if (childMessageModels.size() > 0) {
                title = childMessageModels.get(0).getPreviewText();
            }

            if (title != null) {
                return title;
            } else {
                return getContext().getResources().getQuantityString(R.plurals.ui_button_message_preview_text, 0, mMetadata.getButtonModels().size());
            }
        }

        return "";
    }

    @Override
    public String getActionEvent() {
        if (super.getActionEvent() != null) {
            return super.getActionEvent();
        }

        MessageModel contentModel = getContentModel();
        if (contentModel != null) {
            return contentModel.getActionEvent();
        }

        return null;
    }

    @Override
    public JsonObject getActionData() {
        if (super.getActionData().size() > 0) {
            return super.getActionData();
        }

        MessageModel contentModel = getContentModel();
        if (contentModel != null) {
            return contentModel.getActionData();
        }

        return new JsonObject();
    }

    @Override
    public int getBackgroundColor() {
        return R.color.transparent;
    }

    @Override
    public boolean getHasContent() {
        return getRootMessagePart().isContentReady();
    }

    @Nullable
    public MessageModel getContentModel() {
        if (getChildMessageModels().size() > 0) {
            return getChildMessageModels().get(0);
        }

        return null;
    }

    @Nullable
    public List<ButtonModel> getButtonModels() {
        return mMetadata != null ? mMetadata.getButtonModels() : null;
    }

    public void onChoiceClicked(ButtonModel.ChoiceData choiceData, ChoiceMetadata choice,
                                boolean selected, Set<String> selectedChoices) {
        sendResponse(choiceData, choice, selected, selectedChoices);

        // Get root model
        MessageModel root = this;
        while (true) {
            MessageModel parent = root.getParentMessageModel();
            if (parent == null) {
                break;
            }
            root = parent;
        }
        ActionHandlerRegistry.dispatchChoiceSelection(getContext(), choice, this, root);

    }

    @SuppressWarnings("WeakerAccess")
    void sendResponse(ButtonModel.ChoiceData choiceData, @NonNull ChoiceMetadata choice,
                      boolean selected, @NonNull Set<String> selectedChoices) {
        String userName = getIdentityFormatter().getDisplayName(
                getLayerClient().getAuthenticatedUser());
        String statusText;
        if (TextUtils.isEmpty(choiceData.getName())) {
            statusText = getContext().getString(
                    selected ? R.string.response_message_status_text_selected
                            : R.string.response_message_status_text_deselected,
                    userName,
                    choice.getText());
        } else {
            statusText = getContext().getString(
                    selected ? R.string.response_message_status_text_with_name_selected
                            : R.string.response_message_status_text_with_name_deselected,
                    userName,
                    choice.getText(),
                    choiceData.getName());
        }

        UUID rootPartId = UUID.fromString(getRootMessagePart().getId().getLastPathSegment());

        ChoiceResponseModel choiceResponseModel = new ChoiceResponseModel(getMessage().getId(),
                rootPartId, statusText);
        choiceResponseModel.addChoices(choiceData.getResponseName(), selectedChoices);

        MessageSenderRepository messageSenderRepository = getMessageSenderRepository();
        messageSenderRepository.sendChoiceResponse(getMessage().getConversation(),
                choiceResponseModel);
    }
}
