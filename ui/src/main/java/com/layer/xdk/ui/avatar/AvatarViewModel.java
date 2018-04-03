package com.layer.xdk.ui.avatar;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.image.cache.ImageCacheWrapper;

/**
 * @see AvatarViewModel exposes methods in the AvatarViewModelImpl which are called by the {@link
 * AvatarView}
 * @see AvatarViewModel is implemented by {@link AvatarViewModelImpl}
 **/
public interface AvatarViewModel {

    /**
     * @return the ImageCacheWrapper to be used to load images
     */
    ImageCacheWrapper getImageCacheWrapper();

    /**
     * @return an {@link IdentityFormatter} for formatting the {@link Identity} objects
     */
    String getInitialsForAvatarView(Identity identity);
}
