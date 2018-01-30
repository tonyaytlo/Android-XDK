package com.layer.ui.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagePartUtils {
    public static final String ROLE_SOURCE = "source";

    private static final Pattern PARAMETER_ROLE = Pattern.compile("\\s*role\\s*=\\s*\\w+");
    private static final Pattern PARAMETER_IS_ROOT = Pattern.compile(".*;\\s*role\\s*=\\s*root\\s*;?");

    private static final String ROLE_ROOT = "root";
    private static final String ROLE_RESPONSE_SUMMARY = "response_summary";
    private static final String PARAMETER_KEY_PARENT_NODE_ID = "parent-node-id";
    private static final String PARAMETER_ITEM_ORDER = "item-order";

    @Nullable
    public static String getMimeType(@NonNull MessagePart messagePart) {
        String mimeType = messagePart.getMimeType();
        if (mimeType == null || mimeType.isEmpty()) return null;

        if (!mimeType.contains(";")) return mimeType;

        return mimeType.split(";")[0].trim();
    }

    @Nullable
    public static Map<String, String> getMimeTypeArguments(MessagePart messagePart) {
        String mimeType = messagePart.getMimeType();
        if (mimeType == null || mimeType.isEmpty() || !mimeType.contains(";")) return null;

        String[] split = mimeType.split(";");
        if (split.length < 2) return null;

        Map<String, String> arguments = new HashMap<>();
        for (int i = 1; i < split.length; i++) {
            String[] subsplit = split[i].split("=");
            arguments.put(subsplit[0].trim(), subsplit[1].trim());
        }

        return arguments;
    }

    @Nullable
    public static String getNodeId(@NonNull MessagePart messagePart) {
        return messagePart.getId().getLastPathSegment();
    }

    @Nullable
    public static String getParentNodeId(@NonNull MessagePart messagePart) {
        Map<String, String> arguments = getMimeTypeArguments(messagePart);
        return arguments != null ? arguments.get(PARAMETER_KEY_PARENT_NODE_ID) : null;
    }

    public static boolean hasMessagePartWithRole(@NonNull Message message, @NonNull String role) {
        return getMessagePartWithRole(message, role) != null;
    }

    public static boolean hasMessagePartWithRole(@NonNull Message message, @NonNull String... roles) {
        for (String role : roles) {
            if (hasMessagePartWithRole(message, role)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static MessagePart getMessagePartWithRole(@NonNull Message message, @NonNull String role) {
        for (MessagePart messagePart : message.getMessageParts()) {
            if (isRole(messagePart, role)) {
                return messagePart;
            }
        }

        return null;
    }

    @Nullable
    public static MessagePart getMessagePartWithRoleRoot(@NonNull Message message) {
        return getMessagePartWithRole(message, ROLE_ROOT);
    }

    @Nullable
    public static String getRole(@NonNull MessagePart messagePart) {
        String mimeType = messagePart.getMimeType();
        if (mimeType == null || mimeType.isEmpty()) return null;

        Matcher matcher = PARAMETER_ROLE.matcher(mimeType);
        return matcher.find() ? matcher.group(0).split("=")[1].trim() : null;
    }

    public static boolean isRoleRoot(@NonNull MessagePart messagePart) {
        String mimeType = messagePart.getMimeType();
        if (mimeType == null || mimeType.isEmpty()) return false;

        return PARAMETER_IS_ROOT.matcher(mimeType).find();
    }

    public static boolean isResponseSummaryPart(@NonNull MessagePart childMessagePart) {
        return isRole(childMessagePart, ROLE_RESPONSE_SUMMARY);
    }

    public static boolean isParentMessagePart(@NonNull MessagePart rootMessagePart, @NonNull MessagePart childMessagePart) {
        String parentNodeId = getParentNodeId(childMessagePart);
        if (parentNodeId == null) return false;

        return parentNodeId.equals(getNodeId(rootMessagePart));
    }

    @NonNull
    public static List<MessagePart> getChildParts(@NonNull Message message, @NonNull MessagePart parentPart) {
        List<MessagePart> children = new ArrayList<>();
        String parentPartId = parentPart.getId().getLastPathSegment();

        for (MessagePart messagePart : message.getMessageParts()) {
            Map<String, String> mimeTypeArguments = getMimeTypeArguments(messagePart);
            if (mimeTypeArguments == null) continue;

            if (mimeTypeArguments.containsKey(PARAMETER_KEY_PARENT_NODE_ID)) {
                String id = mimeTypeArguments.get(PARAMETER_KEY_PARENT_NODE_ID);
                if (parentPartId.equals(id)) {
                    children.add(messagePart);
                }
            }
        }

        if (!children.isEmpty()) {
            Collections.sort(children, new Comparator<MessagePart>() {
                @Override
                public int compare(MessagePart messagePart, MessagePart otherPart) {
                    Map<String, String> messagePartArguments = getMimeTypeArguments(messagePart);
                    Map<String, String> otherPartArguments = getMimeTypeArguments(otherPart);

                    String messagePartOrder = messagePartArguments != null ? messagePartArguments.get(PARAMETER_ITEM_ORDER) : null;
                    String otherPartOrder = otherPartArguments != null ? otherPartArguments.get(PARAMETER_ITEM_ORDER) : null;

                    int result = 0;

                    if (messagePartOrder != null && otherPartOrder != null) {
                        Integer messageOrder = Integer.parseInt(messagePartOrder);
                        Integer otherOrder = Integer.parseInt(otherPartOrder);
                        result = messageOrder.compareTo(otherOrder);

                    } else if (messagePartOrder != null) {
                        if (Log.isLoggable(Log.WARN)) {
                            Log.w("Found message part without item-order: " + otherPart.getId());
                        }

                        result = -1;
                    } else if (otherPartOrder != null) {
                        if (Log.isLoggable(Log.WARN)) {
                            Log.w("Found message part without item-order: " + messagePart.getId());
                        }

                        result = 1;
                    }

                    return result;
                }
            });
        }
        return children;
    }

    public static boolean isRole(@NonNull MessagePart messagePart, @NonNull String role) {
        String r = getRole(messagePart);
        return r != null && r.equals(role);
    }

    public static String getRootMimeType(Message message) {
        for (MessagePart part : message.getMessageParts()) {
            if (isRoleRoot(part)) {
                return getMimeType(part);
            }
        }

        return null;
    }

    public static String getAsRoleRoot(String mimeType) {
        StringBuilder builder = new StringBuilder(mimeType);
        return builder.append(";role=").append("root").toString();
    }

    public static String getAsRoleWithParentId(String mimeType, @NonNull String role,
                                               @Nullable String parentNodeId) {
        StringBuilder builder = new StringBuilder(mimeType);
        builder.append(";role=").append(role);

        if (parentNodeId != null) {
            builder.append("; parent-node-id=").append(parentNodeId).toString();
        }

        return builder.toString();
    }
}
