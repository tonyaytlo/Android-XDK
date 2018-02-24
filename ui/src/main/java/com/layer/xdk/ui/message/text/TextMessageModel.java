package com.layer.xdk.ui.message.text;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiTextMessageViewBinding;
import com.layer.xdk.ui.message.button.ButtonMessageView;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.view.MessageView;

public class TextMessageModel extends MessageModel {

    public final static String ROOT_MIME_TYPE = "application/vnd.layer.text+json";
    private final JsonParser mJsonParser;

    private String mText;
    private String mTitle;
    private String mSubtitle;
    private String mAuthor;
    private String mActionEvent;
    private JsonObject mCustomData;

    public TextMessageModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        mJsonParser = new JsonParser();
    }

    @Override
    @Deprecated
    public Class<? extends MessageView> getRendererType() {
        return ButtonMessageView.class;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_text_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_standard_message_container;
    }

    @Override
    public Object createNewViewController(ViewDataBinding binding) {
        // TODO AND-1242 refactor out cast
        return new TextMessageViewController((XdkUiTextMessageViewBinding) binding);
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(MessagePart messagePart) {
        return true;
    }

    @Override
    protected void parse(MessagePart messagePart) {
        String data = new String(messagePart.getData());
        JsonObject jsonObject = mJsonParser.parse(data).getAsJsonObject();
        mText = jsonObject.has("text") ? jsonObject.get("text").getAsString() : null;
        mSubtitle = jsonObject.has("subtitle") ? jsonObject.get("subtitle").getAsString().trim() : null;
        mTitle = jsonObject.has("title") ? jsonObject.get("title").getAsString().trim() : null;
        mAuthor = jsonObject.has("author") ? jsonObject.get("author").getAsString().trim() : null;
        if (jsonObject.has("action")) {
            JsonObject action = jsonObject.getAsJsonObject("action");
            mActionEvent = action.get("event").getAsString();
            mCustomData = action.get("data").getAsJsonObject();
        } else {
            mActionEvent = null;
            mCustomData = null;
        }
    }

    @Bindable
    public String getText() {
        return mText;
    }

    @Override
    @Bindable
    public String getTitle() {
        return mTitle;
    }

    @Override
    @Bindable
    public String getDescription() {
        return mSubtitle;
    }

    @Override
    public String getActionEvent() {
        if (super.getActionEvent() != null) {
            return super.getActionEvent();
        }

        return mActionEvent;
    }

    @Override
    public JsonObject getActionData() {
        if (super.getActionData().size() > 0) {
            return super.getActionData();
        }

        if (mCustomData != null) {
            return mCustomData;
        }

        return new JsonObject();
    }

    @Override
    public String getFooter() {
        return mAuthor;
    }

    @Override
    public boolean getHasContent() {
        return !TextUtils.isEmpty(mText);
    }

    @Nullable
    @Override
    public String getPreviewText() {
        if (getHasContent()) {
            return mTitle != null ? mTitle : mText;
        } else {
            return getContext().getString(R.string.xdk_ui_text_message_preview_text);
        }
    }

    @Override
    public int getBackgroundColor() {
        return isMessageFromMe() ? R.color.xdk_ui_text_message_view_background_me : R.color.xdk_ui_text_message_view_background_them;
    }

    @Bindable
    public String getAuthorName() {
        return getIdentityFormatter().getDisplayName(getMessage().getSender());
    }

    @Bindable
    public boolean isDownloadingParts() {
        return getNumberOfPartsCurrentlyDownloading() > 0;
    }
}
