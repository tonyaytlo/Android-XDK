package com.layer.xdk.ui.message.response;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.util.AndroidFieldNamingStrategy;

/**
 * Converts {@link ResponseModel}s to {@link MessagePart}s.
 */
@SuppressWarnings("WeakerAccess")
public class ResponseMessagePartComposer {

    /**
     * Create a {@link MessagePart} from a {@link ResponseModel}
     *
     * @param layerClient used to create the MessagePart
     * @param responseModel model to use when populating the part
     * @return a MessagePart built from the ResponseModel data
     */
    public MessagePart buildResponseMessagePart(LayerClient layerClient, ResponseModel responseModel) {
        Gson gson = new GsonBuilder().setFieldNamingStrategy(new AndroidFieldNamingStrategy()).create();

        String rootMimeTpe = MessagePartUtils.getAsRoleRoot(ResponseMessageModel.MIME_TYPE_V2);

        ResponseMetadata responseMetadata = createResponseMetadata(responseModel);
        return layerClient.newMessagePart(rootMimeTpe, gson.toJson(responseMetadata).getBytes());
    }

    private ResponseMetadata createResponseMetadata(ResponseModel responseModel) {
        ResponseMetadata responseMetadata = new ResponseMetadata();
        responseMetadata.mMessageIdToRespondTo = responseModel.getMessageIdToRespondTo().toString();
        responseMetadata.mPartIdToRespondTo = responseModel.getPartIdToRespondTo().toString();

        responseMetadata.mChanges = responseModel.getChanges();
        return responseMetadata;
    }
}
