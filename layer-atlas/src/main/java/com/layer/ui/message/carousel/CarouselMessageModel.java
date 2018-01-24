package com.layer.ui.message.carousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.R;
import com.layer.ui.message.model.MessageModel;
import com.layer.ui.message.view.MessageView;
import com.layer.ui.util.json.AndroidFieldNamingStrategy;

import java.io.InputStreamReader;
import java.util.List;

public class CarouselMessageModel extends MessageModel {
    public static final String MIME_TYPE = "application/vnd.layer.carousel+json";
    private Gson mGson;
    private CarouselModelMetadata mMetadata;

    public CarouselMessageModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        mGson = new GsonBuilder().setFieldNamingStrategy(new AndroidFieldNamingStrategy()).create();
    }

    @Override
    public Class<? extends MessageView> getRendererType() {
        return CarouselMessageView.class;
    }

    @Override
    protected void processChildParts() {
        super.processChildParts();
        if (mMetadata != null && mMetadata.getAction() != null) {
            List<MessageModel> childModels = getChildMessageModels();
            if (childModels != null) {
                for (MessageModel model : childModels) {
                    model.setAction(mMetadata.getAction());
                }
            }
        }
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        if (messagePart.equals(getRootMessagePart())) {
            JsonReader reader = new JsonReader(new InputStreamReader(messagePart.getDataStream()));
            mMetadata = mGson.fromJson(reader, CarouselModelMetadata.class);
        }
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    public List<MessageModel> getCarouselItemModels() {
        return getChildMessageModels();
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

    @Override
    public String getActionEvent() {
        if (super.getActionEvent() != null) {
            return super.getActionEvent();
        }
        return mMetadata.getAction().getEvent();
    }

    @Override
    public JsonObject getActionData() {
        if (super.getActionData().size() > 0) {
            return super.getActionData();
        }

        return mMetadata.getAction().getData();
    }

    @Override
    public int getBackgroundColor() {
        return R.color.transparent;
    }

    @Override
    public boolean getHasContent() {
        return getChildMessageModels() != null && !getChildMessageModels().isEmpty();
    }
}
