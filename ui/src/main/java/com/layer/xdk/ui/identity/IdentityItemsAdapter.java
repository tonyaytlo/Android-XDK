package com.layer.xdk.ui.identity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.adapters.ItemRecyclerViewAdapter;
import com.layer.xdk.ui.databinding.XdkUiFourPartItemBinding;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewHolder;
import com.layer.xdk.ui.style.FourPartItemStyle;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import javax.inject.Inject;

import dagger.internal.Factory;

public class IdentityItemsAdapter extends ItemRecyclerViewAdapter<Identity, IdentityItemViewModel,
        XdkUiFourPartItemBinding, FourPartItemStyle, FourPartItemViewHolder<Identity, IdentityItemViewModel>> {

    protected static final String TAG = "IdentityItemsAdapter";

    protected ImageCacheWrapper mImageCacheWrapper;
    private final Factory<IdentityItemViewModel> mItemViewModelFactory;

    @Inject
    public IdentityItemsAdapter(Context context,
            LayerClient layerClient,
            ImageCacheWrapper imageCacheWrapper,
            Factory<IdentityItemViewModel> itemViewModelFactory) {
        super(context, layerClient, TAG, false);
        mImageCacheWrapper = imageCacheWrapper;
        mItemViewModelFactory = itemViewModelFactory;
    }

    @NonNull
    @Override
    public FourPartItemViewHolder<Identity, IdentityItemViewModel> onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        XdkUiFourPartItemBinding binding = XdkUiFourPartItemBinding.inflate(getLayoutInflater(), parent, false);
        IdentityItemViewModel viewModel = mItemViewModelFactory.get();

        viewModel.setItemClickListener(getItemClickListener());

        FourPartItemViewHolder<Identity, IdentityItemViewModel> viewHolder = new FourPartItemViewHolder<>(binding, viewModel, getStyle(), mImageCacheWrapper);

        binding.addOnRebindCallback(getOnRebindCallback());

        return viewHolder;
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    public void onDestroy() {
        // NO OP
    }
}
