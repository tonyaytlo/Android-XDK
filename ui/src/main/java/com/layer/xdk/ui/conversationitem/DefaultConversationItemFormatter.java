package com.layer.xdk.ui.conversationitem;

import android.content.Context;
import android.support.annotation.NonNull;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * A default implementation of {@link ConversationItemFormatter}
 */
public class DefaultConversationItemFormatter implements ConversationItemFormatter {
    private static final String METADATA_KEY_CONVERSATION_TITLE = "conversationName";
    private static final int TIME_HOURS_24 = 24 * 60 * 60 * 1000;

    private Context mContext;
    private LayerClient mLayerClient;
    private IdentityFormatter mIdentityFormatter;
    private DateFormat mTimeFormat;
    private DateFormat mDateFormat;
    private BinderRegistry mBinderRegistry;

    public DefaultConversationItemFormatter(Context context, LayerClient layerClient, IdentityFormatter identityFormatter,
                                            DateFormat timeFormat, DateFormat dateFormat) {
        mContext = context;
        mLayerClient = layerClient;
        mIdentityFormatter = identityFormatter;
        mTimeFormat = timeFormat;
        mDateFormat = dateFormat;
        mBinderRegistry = new BinderRegistry(context, layerClient);
    }

    @Override
    public void setConversationMetadataTitle(@NonNull Conversation conversation, String title) {
        if (title == null || title.trim().isEmpty()) {
            conversation.removeMetadataAtKeyPath(METADATA_KEY_CONVERSATION_TITLE);
        } else {
            conversation.putMetadataAtKeyPath(METADATA_KEY_CONVERSATION_TITLE, title.trim());
        }
    }

    @Override
    public String getConversationTitle(@NonNull Conversation conversation) {
        String metadataTitle = getConversationMetadataTitle(conversation);
        if (metadataTitle != null) return metadataTitle.trim();

        Identity authenticatedUser = mLayerClient.getAuthenticatedUser();
        Set<Identity> participants = conversation.getParticipants();
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

    @Override
    public String getConversationMetadataTitle(@NonNull Conversation conversation) {
        if (conversation.getMetadata() != null) {
            String metadataTitle = (String) conversation.getMetadata().get(METADATA_KEY_CONVERSATION_TITLE);
            if (metadataTitle != null && !metadataTitle.trim().isEmpty())
                return metadataTitle.trim();
        }
        return null;
    }

    @Override
    @NonNull
    public String getTimeStamp(@NonNull Conversation conversation) {
        Message lastMessage = conversation.getLastMessage();

        if (lastMessage != null && lastMessage.getReceivedAt() != null) {
            return formatTime(lastMessage.getReceivedAt());
        }

        return null;
    }

    @NonNull
    @Override
    public String getLastMessagePreview(@NonNull Conversation conversation) {
        Message message = conversation.getLastMessage();
        if (message == null) return "";

        MessageModel messageModel = mBinderRegistry.getMessageModelManager().getNewModel(message);
        messageModel.processPartsFromTreeRoot();
        if (messageModel.getPreviewText() != null) {
            return messageModel.getPreviewText();
        }
        return mContext.getString(R.string.xdk_ui_generic_message_preview_text);
    }

    private String formatTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long todayMidnight = cal.getTimeInMillis();
        long yesterdayMidnight = todayMidnight - TIME_HOURS_24;
        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;

        String timeText;
        if (date.getTime() > todayMidnight) {
            timeText = mTimeFormat.format(date.getTime());
        } else if (date.getTime() > yesterdayMidnight) {
            timeText = mContext.getString(R.string.xdk_ui_time_yesterday);
        } else if (date.getTime() > weekAgoMidnight) {
            cal.setTime(date);
            timeText = mContext.getResources().getStringArray(R.array.xdk_ui_time_days_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1];
        } else {
            timeText = mDateFormat.format(date);
        }
        return timeText;
    }
}

