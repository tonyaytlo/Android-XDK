package com.layer.xdk.ui.identity;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewModel;
import com.layer.xdk.ui.util.DateFormatter;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class IdentityItemViewModel extends FourPartItemViewModel<Identity> {

    @SuppressWarnings("WeakerAccess")
    protected DateFormatter mDateFormatter;

    @Inject
    public IdentityItemViewModel(IdentityFormatter identityFormatter, DateFormatter dateFormatter) {
        super(identityFormatter);
        mDateFormatter = dateFormatter;
    }

    @Override
    public void setItem(Identity identity) {
        super.setItem(identity);
        notifyChange();
    }

    @Override
    public String getTitle() {
        return getIdentityFormatter().getDisplayName(getItem());
    }

    @Override
    public String getSubtitle() {
        return getIdentityFormatter().getMetaData(getItem());
    }

    @Override
    public String getAccessoryText() {
        return mDateFormatter.formatTimeDay(getItem().getLastSeenAt());
    }

    @Override
    public boolean isSecondaryState() {
        return false;
    }

    @Override
    public Set<Identity> getIdentities() {
        Set<Identity> identities = new HashSet<>(1);
        identities.add(getItem());

        return identities;
    }
}
