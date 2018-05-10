package com.layer.xdk.test.common.stub;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Metadata;
import com.layer.sdk.messaging.Presence;

import java.util.Date;
import java.util.UUID;

public class IdentityStub implements Identity {
    public Uri mId;
    public String mDisplayName;
    public String mFirstName;
    public String mLastName;
    public String mPhoneNumber;
    public String mEmailAddress;
    public String mAvatarImageUrl;
    public String mPublicKey;
    public Metadata mMetadata;
    public boolean mFollowed;
    public Presence.PresenceStatus mPresenceStatus;
    public Date mLastSeenAt;

    public IdentityStub() {
        mId = Uri.parse("layer:///identities/" + UUID.randomUUID());
        mDisplayName = UUID.randomUUID().toString();
        mFirstName = UUID.randomUUID().toString();
        mLastName = UUID.randomUUID().toString();
        mFollowed = true;
    }

    @NonNull
    @Override
    public Uri getId() {
        return mId;
    }

    @NonNull
    @Override
    public String getUserId() {
        return mId.getLastPathSegment();
    }

    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public String getFirstName() {
        return mFirstName;
    }

    @Override
    public String getLastName() {
        return mLastName;
    }

    @Override
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Override
    public String getEmailAddress() {
        return mEmailAddress;
    }

    @Override
    public String getAvatarImageUrl() {
        return mAvatarImageUrl;
    }

    @NonNull
    @Override
    public Metadata getMetadata() {
        return mMetadata;
    }

    @Override
    public String getPublicKey() {
        return mPublicKey;
    }

    @Override
    public boolean isFollowed() {
        return mFollowed;
    }

    @Override
    public void follow() {

    }

    @Override
    public void unFollow() {

    }

    @Override
    public Presence.PresenceStatus getPresenceStatus() {
        return mPresenceStatus;
    }

    @Override
    public Date getLastSeenAt() {
        return mLastSeenAt;
    }
}
