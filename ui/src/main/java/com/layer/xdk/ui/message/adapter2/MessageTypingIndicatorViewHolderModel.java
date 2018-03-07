package com.layer.xdk.ui.message.adapter2;


import android.content.Context;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;
import com.layer.xdk.ui.viewmodel.MessageViewHolderModel;

import java.util.Collections;
import java.util.Set;

public class MessageTypingIndicatorViewHolderModel extends MessageViewHolderModel {

    private Set<Identity> mParticipants = Collections.emptySet();
    private boolean mAvatarViewVisible;
    private boolean mTypingIndicatorMessageVisible;
    private String mTypingIndicatorMessage;
    private boolean mAnimationVisible;
    private ImageCacheWrapper mImageCacheWrapper;

    public MessageTypingIndicatorViewHolderModel(Context context, LayerClient layerClient,
            ImageCacheWrapper imageCacheWrapper, IdentityFormatter identityFormatter,
            DateFormatter dateFormatter) {
        super(context, layerClient, identityFormatter, dateFormatter);
        mImageCacheWrapper = imageCacheWrapper;
    }

    public Set<Identity> getParticipants() {
        return mParticipants;
    }

    public void setParticipants(Set<Identity> participants) {
        mParticipants = participants;
    }

    public boolean isAvatarViewVisible() {
        return mAvatarViewVisible;
    }

    public void setAvatarViewVisible(boolean avatarViewVisible) {
        mAvatarViewVisible = avatarViewVisible;
    }

    public boolean isTypingIndicatorMessageVisible() {
        return mTypingIndicatorMessageVisible;
    }

    public void setTypingIndicatorMessageVisible(boolean typingIndicatorMessageVisible) {
        mTypingIndicatorMessageVisible = typingIndicatorMessageVisible;
    }

    public String getTypingIndicatorMessage() {
        return mTypingIndicatorMessage;
    }

    public void setTypingIndicatorMessage(String typingIndicatorMessage) {
        mTypingIndicatorMessage = typingIndicatorMessage;
    }

    public boolean isAnimationVisible() {
        return mAnimationVisible;
    }

    public void setAnimationVisible(boolean animationVisible) {
        mAnimationVisible = animationVisible;
    }

    public ImageCacheWrapper getImageCacheWrapper() {
        return mImageCacheWrapper;
    }
}
