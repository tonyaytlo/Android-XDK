package com.layer.xdk.ui.identity;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.databinding.Observable;

import com.layer.xdk.ui.identity.adapter.IdentityDataSourceFactory;
import com.layer.xdk.ui.identity.adapter.IdentityItemModel;
import com.layer.xdk.ui.identity.adapter.IdentityItemsAdapter;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;

import org.junit.Test;

public class IdentityItemsListViewModelTest {

    @Test
    public void testAddClickListenerNotify() {
        // Setup
        IdentityItemsListViewModel viewModel = new IdentityItemsListViewModel(mock(
                IdentityItemsAdapter.class), mock(IdentityDataSourceFactory.class));
        Observable.OnPropertyChangedCallback mockCallback = mock(
                Observable.OnPropertyChangedCallback.class);
        viewModel.addOnPropertyChangedCallback(mockCallback);

        // Test
        viewModel.setItemClickListener(new OnItemClickListener<IdentityItemModel>() {
            @Override
            public void onItemClick(IdentityItemModel item) {
            }
        });

        // Verify
        verify(mockCallback).onPropertyChanged(eq(viewModel), anyInt());
    }
}