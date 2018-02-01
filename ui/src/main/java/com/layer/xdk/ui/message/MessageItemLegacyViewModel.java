package com.layer.xdk.ui.message;

import android.content.Context;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.util.IdentityRecyclerViewEventListener;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;
import com.layer.xdk.ui.viewmodel.ItemViewModel;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class MessageItemLegacyViewModel extends ItemViewModel<Message> {

    // Config
    private boolean mEnableReadReceipts;
    private IdentityRecyclerViewEventListener mIdentityEventListener;
    private boolean mShowAvatars;
    private boolean mShowPresence;
    private ImageCacheWrapper mImageCacheWrapper;

    // View related variables
    private boolean mPreviousPartOfCluster;
    private boolean mNextPartOfCluster;
    private boolean mShouldShowDisplayName;
    private boolean mShouldShowDateTimeForMessage;
    private String mTimeGroupDay;
    private float mMessageCellAlpha;
    private String mSenderName;
    private Set<Identity> mParticipants;
    private String mReadReceipt;
    private String mGroupTime;
    private boolean mIsReadReceiptVisible;
    private boolean mIsMyCellType;
    private boolean mIsAvatarViewVisible;
    private boolean mIsPresenceVisible;
    private boolean mMessageFooterAnimationIsVisible;
    private String mTypingIndicatorMessage;
    private boolean mIsTypingIndicatorVisible;
    private boolean mShouldDisplayAvatarSpace;
    private boolean mShouldShowAvatarForCurrentUser;
    private boolean mShouldCurrentUserAvatarBeVisible;
    private boolean mShouldShowPresenceForCurrentUser;
    private boolean mShouldCurrentUserPresenceBeVisible;

    public MessageItemLegacyViewModel(Context context, LayerClient layerClient,
                                      ImageCacheWrapper imageCacheWrapper,
                                      IdentityRecyclerViewEventListener identityEventListener) {
        super(context, layerClient);
        mEnableReadReceipts = true;
        mShowAvatars = true;
        mShowPresence = true;
        mIdentityEventListener = identityEventListener;
        mImageCacheWrapper = imageCacheWrapper;
    }

    public void update(MessageCluster cluster, MessageCell messageCell, int position,
                       Integer recipientStatusPosition) {
        Message message = getItem();
        mParticipants = Collections.singleton(message.getSender());
        mIsMyCellType = messageCell.mMe;

        // Clustering and dates
        updateClusteringAndDates(message, cluster);

        // Sender-dependent elements
        updateSenderDependentElements(message, messageCell, cluster, position, recipientStatusPosition);
        notifyChange();
    }

    protected void updateClusteringAndDates(Message message, MessageCluster cluster) {
        // Determine if previous/next items are part of a cluster for padding purposes
        mPreviousPartOfCluster = cluster.mClusterWithPrevious != null
                && cluster.mClusterWithPrevious == MessageCluster.Type.LESS_THAN_HOUR;
        mNextPartOfCluster = cluster.mClusterWithNext != null
                && cluster.mClusterWithNext == MessageCluster.Type.LESS_THAN_HOUR;

        // Update and show/hide date
        if (cluster.mClusterWithPrevious == null
                || cluster.mDateBoundaryWithPrevious
                || cluster.mClusterWithPrevious == MessageCluster.Type.MORE_THAN_HOUR) {
            updateReceivedAtDateAndTime(message);
        } else {
            mShouldShowDateTimeForMessage = false;
        }

        mShouldCurrentUserAvatarBeVisible = mIsMyCellType &&  mShouldShowAvatarForCurrentUser &&
                !cluster.mNextMessageIsFromSameUser;
        mShouldCurrentUserPresenceBeVisible =
                mShouldCurrentUserAvatarBeVisible && mShouldShowPresenceForCurrentUser;
    }

    protected void updateSenderDependentElements(Message message, MessageCell messageCell,
                                                 MessageCluster cluster, int position,
                                                 Integer recipientStatusPosition) {
        Identity sender = message.getSender();

        if (messageCell.mMe) {
            updateWithRecipientStatus(message, position, recipientStatusPosition);
            mMessageCellAlpha = message.isSent() ? 1.0f : 0.5f;
        } else {
            mMessageCellAlpha = 1.0f;

            if (mEnableReadReceipts) {
                message.markAsRead();
            }

            // Sender name, only for first message in cluster
            if (!isInAOneOnOneConversation() &&
                    (cluster.mClusterWithPrevious == null
                            || cluster.mClusterWithPrevious == MessageCluster.Type.NEW_SENDER)) {
                if (sender != null) {
                    mSenderName = getIdentityFormatter().getDisplayName(sender);
                } else {
                    mSenderName = getIdentityFormatter().getUnknownNameString();
                }
                mShouldShowDisplayName = true;

                // Add the position to the positions map for Identity updates
                mIdentityEventListener.addIdentityPosition(position, Collections.singleton(sender));
            } else {
                mShouldShowDisplayName = false;
            }
        }

        // Avatars
        if (isInAOneOnOneConversation()) {
            if (mShowAvatars) {
                mIsAvatarViewVisible = !messageCell.mMe;
                mShouldDisplayAvatarSpace = true;
                mIsPresenceVisible = mIsAvatarViewVisible && mShowPresence;
            } else {
                mIsAvatarViewVisible = false;
                mShouldDisplayAvatarSpace = false;
                mIsPresenceVisible = false;
            }
        } else if (cluster.mClusterWithNext == null || cluster.mClusterWithNext != MessageCluster.Type.LESS_THAN_MINUTE) {
            // Last message in cluster
            mIsAvatarViewVisible = !messageCell.mMe;
            // Add the position to the positions map for Identity updates
            mIdentityEventListener.addIdentityPosition(position, Collections.singleton(message.getSender()));
            mShouldDisplayAvatarSpace = true;
            mIsPresenceVisible = mIsAvatarViewVisible && mShowPresence;
        } else {
            // Invisible for clustered messages to preserve proper spacing
            mIsAvatarViewVisible = false;
            mShouldDisplayAvatarSpace = true;
            mIsPresenceVisible = false;
        }
    }

    protected void updateWithRecipientStatus(Message message, int position, Integer recipientStatusPosition) {
        if (mEnableReadReceipts && recipientStatusPosition != null && position == recipientStatusPosition) {
            int readCount = 0;
            boolean delivered = false;
            Map<Identity, Message.RecipientStatus> statuses = message.getRecipientStatus();
            for (Map.Entry<Identity, Message.RecipientStatus> entry : statuses.entrySet()) {
                // Only show receipts for other members
                if (entry.getKey().equals(getLayerClient().getAuthenticatedUser())) continue;
                // Skip receipts for members no longer in the conversation
                if (entry.getValue() == null) continue;

                switch (entry.getValue()) {
                    case READ:
                        readCount++;
                        break;
                    case DELIVERED:
                        delivered = true;
                        break;
                }
            }

            if (readCount > 0) {
                mIsReadReceiptVisible = true;

                // Use 2 to include one other participant plus the current user
                if (statuses.size() > 2) {
                    mReadReceipt = getContext().getResources()
                            .getQuantityString(R.plurals.xdk_ui_message_item_read_muliple_participants, readCount, readCount);
                } else {
                    mReadReceipt = getContext().getString(R.string.xdk_ui_message_item_read);
                }
            } else if (delivered) {
                mIsReadReceiptVisible = true;
                mReadReceipt = getContext().getString(R.string.xdk_ui_message_item_delivered);
            } else {
                mIsReadReceiptVisible = false;
            }
        }
    }

    protected void updateReceivedAtDateAndTime(Message message) {
        Date receivedAt = message.getReceivedAt();
        if (receivedAt == null) receivedAt = new Date();

        mTimeGroupDay = getDateFormatter().formatTimeDay(receivedAt);
        mGroupTime = getDateFormatter().formatTime(receivedAt);

        mShouldShowDateTimeForMessage = true;
    }

    protected boolean isInAOneOnOneConversation() {
        return getItem().getConversation().getParticipants().size() == 2;
    }

    public ImageCacheWrapper getImageCacheWrapper() {
        return mImageCacheWrapper;
    }

    public void setShowAvatars(boolean showAvatars) {
        mShowAvatars = showAvatars;
    }

    public void setShouldShowAvatarForCurrentUser(boolean shouldShowAvatarForCurrentUser) {
        mShouldShowAvatarForCurrentUser = shouldShowAvatarForCurrentUser;
    }

    public void setShowPresence(boolean showPresence) {
        mShowPresence = showPresence;
    }

    public void setEnableReadReceipts(boolean enableReadReceipts) {
        mEnableReadReceipts = enableReadReceipts;
    }

    public void setShouldShowPresenceForCurrentUser(boolean shouldShowPresenceForCurrentUser) {
        mShouldShowPresenceForCurrentUser = shouldShowPresenceForCurrentUser;
    }

    // To be eliminated

    public void setAvatarViewVisibilityType(boolean avatarViewVisibilityType) {
        mIsAvatarViewVisible = avatarViewVisibilityType;
    }

    // Bindable properties

    @Bindable
    public boolean getMessageFooterAnimationVisibility() {
        return mMessageFooterAnimationIsVisible;
    }

    public void setMessageFooterAnimationVisibility(boolean isVisible) {
        mMessageFooterAnimationIsVisible = isVisible;
    }

    @Bindable
    public String getTypingIndicatorMessage() {
        return mTypingIndicatorMessage;
    }

    public void setTypingIndicatorMessage(String typingIndicatorMessage) {
        mTypingIndicatorMessage = typingIndicatorMessage;
    }

    @Bindable
    public boolean getTypingIndicatorMessageVisibility() {
        return mIsTypingIndicatorVisible;
    }

    public void setTypingIndicatorMessageVisibility(boolean isTypingIndicatorVisible) {
        mIsTypingIndicatorVisible = isTypingIndicatorVisible;
    }

    @Bindable
    public boolean isPreviousPartOfCluster() {
        return mPreviousPartOfCluster;
    }

    @Bindable
    public boolean isNextPartOfCluster() {
        return mNextPartOfCluster;
    }

    @Bindable
    public boolean getAvatarVisibility() {
        return mIsAvatarViewVisible;
    }

    @Bindable
    public boolean getShouldShowAvatarForCurrentUser() {
        return mShouldShowAvatarForCurrentUser;
    }

    @Bindable
    public boolean isPresenceVisible() {
        return mIsPresenceVisible;
    }

    @Bindable
    public Set<Identity> getParticipants() {
        return mParticipants;
    }

    public void setParticipants(Set<Identity> identities) {
        mParticipants = identities;
    }

    @Bindable
    public String getTimeGroupDay() {
        return mTimeGroupDay;
    }

    @Bindable
    public boolean getShouldShowDateTimeForMessage() {
        return mShouldShowDateTimeForMessage;
    }

    @Bindable
    public String getReadReceipt() {
        return mReadReceipt;
    }

    @Bindable
    public float getMessageCellAlpha() {
        return mMessageCellAlpha;
    }

    @Bindable
    public String getSenderName() {
        return mSenderName;
    }

    @Bindable
    public boolean getShouldShowDisplayName() {
        return mShouldShowDisplayName;
    }

    @Bindable
    public String getGroupTime() {
        return mGroupTime;
    }

    @Bindable
    public boolean isReadReceiptVisible() {
        return mIsReadReceiptVisible;
    }

    @Bindable
    public boolean isMyCellType() {
        return mIsMyCellType;
    }

    @Bindable
    public boolean getShouldDisplayAvatarSpace() {
        return mShouldDisplayAvatarSpace;
    }

    @Bindable
    public boolean getShouldCurrentUserAvatarBeVisible() {
        return mShouldCurrentUserAvatarBeVisible;
    }

    @Bindable
    public boolean getShouldCurrentUserPresenceBeVisible() {
        return mShouldCurrentUserPresenceBeVisible;
    }
}
