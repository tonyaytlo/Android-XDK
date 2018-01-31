package com.layer.xdk.ui.fourpartitem;

import com.layer.sdk.query.Queryable;
import com.layer.xdk.ui.adapters.ItemViewHolder;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.databinding.UiFourPartItemBinding;
import com.layer.xdk.ui.style.FourPartItemStyle;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class FourPartItemViewHolder<ITEM extends Queryable, VIEW_MODEL extends FourPartItemViewModel<ITEM>> extends ItemViewHolder<ITEM, VIEW_MODEL, UiFourPartItemBinding, FourPartItemStyle> {

    public FourPartItemViewHolder(UiFourPartItemBinding binding, VIEW_MODEL viewModel,
                                  FourPartItemStyle itemStyle, ImageCacheWrapper imageCacheWrapper) {
        super(binding, viewModel);
        setStyle(itemStyle);

        binding.setViewModel(viewModel);
        binding.setStyle(itemStyle);
        binding.avatar.init(new AvatarViewModelImpl(imageCacheWrapper), viewModel.getIdentityFormatter());
        binding.getRoot().setOnClickListener(viewModel.getOnClickListener());
        binding.getRoot().setOnLongClickListener(viewModel.getOnLongClickListener());
    }
}
