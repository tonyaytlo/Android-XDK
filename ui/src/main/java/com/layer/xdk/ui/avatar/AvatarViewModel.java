package com.layer.xdk.ui.avatar;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

/**
 * @see AvatarViewModel exposes methods in the AvatarViewModelImpl which are called by the {@link
 * AvatarView}
 * @see AvatarViewModel is implemented by {@link AvatarViewModelImpl}
 **/
public interface AvatarViewModel {

    /**
     * getter for ImageCacherWrapper so that the view can cancel Bitmap Load request
     */
    ImageCacheWrapper getImageCacheWrapper();

    /**
     * Returns the initial base on the
     *
     * @see IdentityFormatter passed into the ViewModel
     */
    String getInitialsForAvatarView(Identity identity);
}
