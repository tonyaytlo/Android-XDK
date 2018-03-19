package com.layer.xdk.ui.avatar;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import javax.inject.Inject;

public class AvatarViewModelImpl implements AvatarViewModel  {

    private IdentityFormatter mIdentityFormatter;
    private ImageCacheWrapper mImageCacheWrapper;

    @Inject
    public AvatarViewModelImpl(ImageCacheWrapper imageCacheWrapper,
            IdentityFormatter identityFormatter) {
        mImageCacheWrapper = imageCacheWrapper;
        mIdentityFormatter = identityFormatter;
    }

    public String getInitialsForAvatarView(Identity identity) {
        return mIdentityFormatter.getInitials(identity);
    }

    @Override
    public ImageCacheWrapper getImageCacheWrapper() {
        return mImageCacheWrapper;
    }

}
