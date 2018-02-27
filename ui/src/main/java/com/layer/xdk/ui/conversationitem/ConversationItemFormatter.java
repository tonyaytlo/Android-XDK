package com.layer.xdk.ui.conversationitem;

import android.content.Context;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.MessagePartUtils;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.messagetypes.CellFactory;
import com.layer.xdk.ui.message.model.AbstractMessageModel;
import com.layer.xdk.ui.message.model.MessageModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * A formatter that enables us to modify conversation recyclerView items in the UI
 */

public class ConversationItemFormatter {
    private static final String METADATA_KEY_CONVERSATION_TITLE = "conversationName";
    private static final int TIME_HOURS_24 = 24 * 60 * 60 * 1000;

    private Context mContext;
    private LayerClient mLayerClient;
    private IdentityFormatter mIdentityFormatter;
    private DateFormat mTimeFormat;
    private DateFormat mDateFormat;
    private List<CellFactory> mCellFactories;
    private BinderRegistry mBinderRegistry;

    public ConversationItemFormatter(Context context, LayerClient layerClient, IdentityFormatter identityFormatter,
                                     DateFormat timeFormat, DateFormat dateFormat, List<CellFactory> cellFactories) {
        mContext = context;
        mLayerClient = layerClient;
        mIdentityFormatter = identityFormatter;
        mTimeFormat = timeFormat;
        mDateFormat = dateFormat;
        mCellFactories = cellFactories;
        mBinderRegistry = new BinderRegistry(context, layerClient);
    }

    public void setMetaDataTitleOnConversation(Conversation conversation, String title) {
        if (title == null || title.trim().isEmpty()) {
            conversation.removeMetadataAtKeyPath(METADATA_KEY_CONVERSATION_TITLE);
        } else {
            conversation.putMetadataAtKeyPath(METADATA_KEY_CONVERSATION_TITLE, title.trim());
        }
    }

    public String getConversationTitle(Identity authenticatedUser, Conversation conversation) {
        return getConversationTitle(authenticatedUser, conversation, conversation.getParticipants());
    }

    public String getConversationTitle(Identity authenticatedUser, Conversation conversation, Set<Identity> participants) {
        String metadataTitle = getConversationMetadataTitle(conversation);
        if (metadataTitle != null) return metadataTitle.trim();

        StringBuilder sb = new StringBuilder();
        boolean getOnlyFirstName = participants.size() > 2;
        for (Identity participant : participants) {
            if (participant.equals(authenticatedUser)) continue;
            String displayName = getOnlyFirstName ? mIdentityFormatter.getFirstName(participant) : mIdentityFormatter.getDisplayName(participant);
            if (sb.length() > 0) sb.append(", ");
            sb.append(displayName);
        }
        return sb.toString();
    }

    public String getConversationMetadataTitle(Conversation conversation) {
        if (conversation.getMetadata() != null) {
            String metadataTitle = (String) conversation.getMetadata().get(METADATA_KEY_CONVERSATION_TITLE);
            if (metadataTitle != null && !metadataTitle.trim().isEmpty())
                return metadataTitle.trim();
        }
        return null;
    }

    public String getTimeStamp(Conversation conversation) {
        Message lastMessage = conversation.getLastMessage();

        if (lastMessage != null && lastMessage.getReceivedAt() != null) {
            return formatTime(lastMessage.getReceivedAt());
        }

        return null;
    }

    public String getLastMessagePreview(Conversation conversation) {
        Message message = conversation.getLastMessage();
        if (message == null) return "";

        String rootMimeType = MessagePartUtils.getRootMimeType(message);
        MessageModel modelToProcessParts = null;
        AbstractMessageModel model;
        if (rootMimeType == null) {
            // This is a legacy message
            // Create set of mime types then get the model based on that type
            Set<String> legacyMimeTypes = MessagePartUtils.getLegacyMessageMimeTypes(
                    message);
            model = mBinderRegistry.getMessageModelManager().getNewLegacyModel(legacyMimeTypes, message);
        } else {
            modelToProcessParts = mBinderRegistry.getMessageModelManager().getNewModel(rootMimeType, message);
            model = modelToProcessParts;
        }
        if (modelToProcessParts != null) {
            modelToProcessParts.processParts();
        }
        if (model != null && model.getPreviewText() != null) {
            return model.getPreviewText();
        }
        return mContext.getString(R.string.xdk_ui_generic_message_preview_text);
    }

    protected String formatTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long todayMidnight = cal.getTimeInMillis();
        long yesterMidnight = todayMidnight - TIME_HOURS_24;
        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;

        String timeText;
        if (date.getTime() > todayMidnight) {
            timeText = mTimeFormat.format(date.getTime());
        } else if (date.getTime() > yesterMidnight) {
            timeText = mContext.getString(R.string.xdk_ui_time_yesterday);
        } else if (date.getTime() > weekAgoMidnight) {
            cal.setTime(date);
            timeText = mContext.getResources().getStringArray(R.array.xdk_ui_time_days_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1];
        } else {
            timeText = mDateFormat.format(date);
        }
        return timeText;
    }

    public void setCellFactories(List<CellFactory> cellFactories) {
        mCellFactories = cellFactories;
    }
}

