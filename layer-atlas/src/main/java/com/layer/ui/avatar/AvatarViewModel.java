package com.layer.ui.avatar;

import com.layer.sdk.messaging.Identity;
import com.layer.ui.identity.IdentityFormatter;
import com.layer.ui.util.imagecache.ImageCacheWrapper;

/**
 * @see AvatarViewModel exposes methods in the AvatarViewModelImpl which are called by the {@link
 * AvatarView}
 * @see AvatarViewModel is implemented by {@link AvatarViewModelImpl}
 **/
public interface AvatarViewModel {

    /**
     * Set Name Formatter for the Identity
     */
    void setIdentityFormatter(IdentityFormatter identityFormatter);

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
