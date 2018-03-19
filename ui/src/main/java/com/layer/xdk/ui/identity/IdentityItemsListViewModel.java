package com.layer.xdk.ui.identity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.messaging.Identity;
import com.layer.sdk.query.Query;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;

import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;

public class IdentityItemsListViewModel extends BaseObservable {

    protected IdentityItemsAdapter mItemsAdapter;

    @Inject
    public IdentityItemsListViewModel(IdentityItemsAdapter itemsAdapter) {
        mItemsAdapter = itemsAdapter;
    }

    public void setIdentities(Set<Identity> identities) {
        mItemsAdapter.setItems(identities);
        notifyChange();
    }

    public void setQuery(Query<Identity> query) {
        this.setQuery(query, null);
    }

    public void setQuery(Query<Identity> query, Collection<String> updateAttributes) {
        mItemsAdapter.setQuery(query, updateAttributes);
    }

    public void setItemClickListener(OnItemClickListener<Identity> itemClickListener) {
        mItemsAdapter.setItemClickListener(itemClickListener);
        notifyChange();
    }

    @Bindable
    public IdentityItemsAdapter getAdapter() {
        return mItemsAdapter;
    }
}
