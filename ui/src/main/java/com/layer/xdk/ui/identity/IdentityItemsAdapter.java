package com.layer.xdk.ui.identity;

import android.content.Context;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.adapters.ItemRecyclerViewAdapter;
import com.layer.xdk.ui.databinding.XdkUiFourPartItemBinding;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewHolder;
import com.layer.xdk.ui.style.FourPartItemStyle;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.DateFormatterImpl;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class IdentityItemsAdapter extends ItemRecyclerViewAdapter<Identity, IdentityItemViewModel,
        XdkUiFourPartItemBinding, FourPartItemStyle, FourPartItemViewHolder<Identity, IdentityItemViewModel>> {

    protected static final String TAG = "IdentityItemsAdapter";

    protected ImageCacheWrapper mImageCacheWrapper;
    private DateFormatter mDateFormatter;
    private IdentityFormatter mIdentityFormatter;

    public IdentityItemsAdapter(Context context, LayerClient layerClient,
                                ImageCacheWrapper imageCacheWrapper) {
        super(context, layerClient, TAG, false);
        mImageCacheWrapper = imageCacheWrapper;
        mDateFormatter = new DateFormatterImpl(context);
        mIdentityFormatter = new IdentityFormatterImpl(context);
    }

    @Override
    public FourPartItemViewHolder<Identity, IdentityItemViewModel> onCreateViewHolder(ViewGroup parent, int viewType) {
        XdkUiFourPartItemBinding binding = XdkUiFourPartItemBinding.inflate(getLayoutInflater(), parent, false);
        IdentityItemViewModel viewModel = new IdentityItemViewModel(getContext(), getLayerClient());
        viewModel.setDateFormatter(mDateFormatter);
        viewModel.setIdentityFormatter(mIdentityFormatter);

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

    public void setDateFormatter(DateFormatter dateFormatter) {
        mDateFormatter = dateFormatter;
    }

    public void setIdentityFormatter(IdentityFormatter identityFormatter) {
        mIdentityFormatter = identityFormatter;
    }
}
