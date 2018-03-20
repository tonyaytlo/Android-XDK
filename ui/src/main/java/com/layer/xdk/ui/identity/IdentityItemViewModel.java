package com.layer.xdk.ui.identity;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewModel;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

public class IdentityItemViewModel extends FourPartItemViewModel<IdentityItemModel> {

    @SuppressWarnings("WeakerAccess")
    protected DateFormatter mDateFormatter;

    @Inject
    public IdentityItemViewModel(IdentityFormatter identityFormatter,
            ImageCacheWrapper imageCacheWrapper,
            DateFormatter dateFormatter) {
        super(identityFormatter, imageCacheWrapper);
        mDateFormatter = dateFormatter;
    }

    @Override
    public String getTitle() {
        return getIdentityFormatter().getDisplayName(getItem().getIdentity());
    }

    @Override
    public String getSubtitle() {
        return getIdentityFormatter().getMetaData(getItem().getIdentity());
    }

    @Override
    public String getAccessoryText() {
        return mDateFormatter.formatTimeDay(getItem().getIdentity().getLastSeenAt());
    }

    @Override
    public boolean isSecondaryState() {
        return false;
    }

    @Override
    public Set<Identity> getIdentities() {
        return Collections.singleton(getItem().getIdentity());
    }
}
