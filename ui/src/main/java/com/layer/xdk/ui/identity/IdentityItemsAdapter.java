package com.layer.xdk.ui.identity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.adapters.ItemRecyclerViewAdapter;
import com.layer.xdk.ui.databinding.XdkUiFourPartItemBinding;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewHolder;
import com.layer.xdk.ui.style.FourPartItemStyle;

import javax.inject.Inject;

import dagger.internal.Factory;

public class IdentityItemsAdapter extends ItemRecyclerViewAdapter<IdentityItemModel, IdentityItemViewModel,
        XdkUiFourPartItemBinding, FourPartItemStyle, FourPartItemViewHolder<IdentityItemModel, IdentityItemViewModel>> {

    private final Factory<IdentityItemViewModel> mItemViewModelFactory;

    @Inject
    public IdentityItemsAdapter(Context context,
            LayerClient layerClient,
            Factory<IdentityItemViewModel> itemViewModelFactory) {
        super(context, layerClient, new DiffCallback());
        mItemViewModelFactory = itemViewModelFactory;
    }

    @NonNull
    @Override
    public FourPartItemViewHolder<IdentityItemModel, IdentityItemViewModel> onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        XdkUiFourPartItemBinding binding = XdkUiFourPartItemBinding.inflate(getLayoutInflater(), parent, false);
        IdentityItemViewModel viewModel = mItemViewModelFactory.get();

        viewModel.setItemClickListener(getItemClickListener());

        FourPartItemViewHolder<IdentityItemModel, IdentityItemViewModel>
                viewHolder = new FourPartItemViewHolder<>(binding, viewModel, getStyle());

        binding.addOnRebindCallback(getOnRebindCallback());

        return viewHolder;
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<IdentityItemModel> {
        @Override
        public boolean areItemsTheSame(@NonNull IdentityItemModel oldItem,
                @NonNull IdentityItemModel newItem) {
            return oldItem.getIdentity().getId().equals(newItem.getIdentity().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull IdentityItemModel oldItem,
                @NonNull IdentityItemModel newItem) {
            return oldItem.deepEquals(newItem);
        }
    }
}
