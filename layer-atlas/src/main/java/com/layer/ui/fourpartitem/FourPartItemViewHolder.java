package com.layer.ui.fourpartitem;

import com.layer.sdk.query.Queryable;
import com.layer.ui.adapters.ItemViewHolder;
import com.layer.ui.avatar.AvatarViewModelImpl;
import com.layer.ui.databinding.UiFourPartItemBinding;
import com.layer.ui.identity.IdentityFormatter;
import com.layer.ui.style.FourPartItemStyle;
import com.layer.ui.util.imagecache.ImageCacheWrapper;

public class FourPartItemViewHolder<ITEM extends Queryable, VIEW_MODEL extends FourPartItemViewModel<ITEM>> extends ItemViewHolder<ITEM, VIEW_MODEL, UiFourPartItemBinding, FourPartItemStyle> {

    public FourPartItemViewHolder(UiFourPartItemBinding binding, VIEW_MODEL viewModel,
                                  FourPartItemStyle itemStyle, ImageCacheWrapper imageCacheWrapper,
                                  IdentityFormatter identityFormatter) {
        super(binding, viewModel);
        setStyle(itemStyle);

        binding.setViewModel(viewModel);
        binding.setStyle(itemStyle);
        binding.avatar.init(new AvatarViewModelImpl(imageCacheWrapper), identityFormatter);
    }
}
