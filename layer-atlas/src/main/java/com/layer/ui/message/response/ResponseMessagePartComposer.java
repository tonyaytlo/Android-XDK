package com.layer.ui.message.response;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.message.MessageItemStatusViewModel;
import com.layer.ui.message.MessagePartUtils;
import com.layer.ui.util.json.AndroidFieldNamingStrategy;

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

        String rootMimeTpe = MessagePartUtils.getAsRoleRoot(
                MessageItemStatusViewModel.RESPONSE_ROOT_MIME_TYPE);

        ResponseMetadata responseMetadata = createResponseMetadata(responseModel);
        return layerClient.newMessagePart(rootMimeTpe, gson.toJson(responseMetadata).getBytes());
    }

    private ResponseMetadata createResponseMetadata(ResponseModel responseModel) {
        ResponseMetadata responseMetadata = new ResponseMetadata();
        responseMetadata.setMessageIdToRespondTo(responseModel.getMessageIdToRespondTo().toString());
        responseMetadata.setPartIdToRespondTo(responseModel.getPartIdToRespondTo().toString());

        responseMetadata.setParticipantData(responseModel.getParticipantData());
        return responseMetadata;
    }
}
