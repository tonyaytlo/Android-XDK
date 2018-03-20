package com.layer.xdk.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.conversationitem.ConversationItemModel;
import com.layer.xdk.ui.conversationitem.ConversationItemViewModel;
import com.layer.xdk.ui.databinding.XdkUiFourPartItemBinding;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewHolder;
import com.layer.xdk.ui.style.FourPartItemStyle;

import javax.inject.Inject;

import dagger.internal.Factory;

public class ConversationItemsAdapter extends ItemRecyclerViewAdapter<ConversationItemModel,
        ConversationItemViewModel, XdkUiFourPartItemBinding, FourPartItemStyle,
        FourPartItemViewHolder<ConversationItemModel, ConversationItemViewModel>> {

    private Factory<ConversationItemViewModel> mItemViewModelFactory;

    @Inject
    public ConversationItemsAdapter(Context context, LayerClient layerClient,
                                    Factory<ConversationItemViewModel> itemViewModelFactory) {
        super(context, layerClient, new DiffCallback());
        mItemViewModelFactory = itemViewModelFactory;
    }

    @NonNull
    @Override
    public FourPartItemViewHolder<ConversationItemModel, ConversationItemViewModel> onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        XdkUiFourPartItemBinding binding = XdkUiFourPartItemBinding.inflate(getLayoutInflater(), parent, false);
        ConversationItemViewModel viewModel = mItemViewModelFactory.get();

        viewModel.setItemClickListener(getItemClickListener());
        viewModel.setItemLongClickListener(getItemLongClickListener());

        FourPartItemViewHolder<ConversationItemModel, ConversationItemViewModel> itemViewHolder =
                new FourPartItemViewHolder<>(binding, viewModel, getStyle());

        binding.addOnRebindCallback(getOnRebindCallback());

        return itemViewHolder;
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<ConversationItemModel> {
        @Override
        public boolean areItemsTheSame(@NonNull ConversationItemModel oldItem, @NonNull ConversationItemModel newItem) {
            return oldItem.getConversation().getId().equals(newItem.getConversation().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ConversationItemModel oldItem, @NonNull ConversationItemModel newItem) {
            return oldItem.deepEquals(newItem);
        }
    }
}
