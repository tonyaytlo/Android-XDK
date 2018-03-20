package com.layer.xdk.ui.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.recyclerview.OnItemLongClickListener;
import com.layer.xdk.ui.style.ItemStyle;
import com.layer.xdk.ui.viewmodel.ItemViewModel;

import java.util.List;

public abstract class ItemRecyclerViewAdapter<ITEM,
        VIEW_HOLDER_MODEL extends ItemViewModel<ITEM>,
        BINDING extends ViewDataBinding,
        STYLE extends ItemStyle,
        VIEW_HOLDER extends ItemViewHolder<ITEM, VIEW_HOLDER_MODEL, BINDING, STYLE>>
        extends PagedListAdapter<ITEM, VIEW_HOLDER> {

    private final String TAG;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRecyclerView;

    private LayerClient mLayerClient;

    private STYLE mStyle;
    private OnItemClickListener<ITEM> mItemClickListener;
    private OnItemLongClickListener<ITEM> mItemLongClickListener;

    private OnRebindCallback<BINDING> mOnRebindCallback;

    protected ItemRecyclerViewAdapter(Context context, LayerClient layerClient,
            @NonNull DiffUtil.ItemCallback<ITEM> diffCallback) {
        super(diffCallback);
        mContext = context;
        mLayerClient = layerClient;
        TAG = getClass().getSimpleName();
        mLayoutInflater = LayoutInflater.from(context);
        mOnRebindCallback = new OnRebindCallback<BINDING>() {
            @Override
            public boolean onPreBind(BINDING binding) {
                return mRecyclerView != null && mRecyclerView.isComputingLayout();
            }

            @Override
            public void onCanceled(BINDING binding) {
                int childAdapterPosition = mRecyclerView.getChildAdapterPosition(binding.getRoot());
                if (childAdapterPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(childAdapterPosition, TAG);
                }
            }
        };
    }

    protected boolean hasNonDataBindingInvalidate(List<Object> payloads, Object validation) {
        for (Object payload : payloads) {
            if (payload != validation) {
                return true;
            }
        }
        return false;
    }

    //==============================================================================================
    // Public API
    //==============================================================================================

    /**
     * Bind an empty/null item. Typically for when the query controller is running a query
     * asynchronously
     */
    @CallSuper
    public void onBindEmpty(VIEW_HOLDER holder) {
        holder.setEmpty();
    }

    public STYLE getStyle() {
        return mStyle;
    }

    @CallSuper
    public void setStyle(STYLE style) {
        mStyle = style;
    }


    public ITEM getItem(ItemViewHolder<ITEM, VIEW_HOLDER_MODEL, BINDING, STYLE> viewHolder) {
        return viewHolder.getItem();
    }

    public void setItemClickListener(OnItemClickListener<ITEM> listener) {
        mItemClickListener = listener;
    }

    public void setItemLongClickListener(OnItemLongClickListener<ITEM> listener) {
        mItemLongClickListener = listener;
    }

    public OnItemClickListener<ITEM> getItemClickListener() {
        return mItemClickListener;
    }

    public OnItemLongClickListener<ITEM> getItemLongClickListener() {
        return mItemLongClickListener;
    }

    //==============================================================================================
    // RecyclerView methods
    //==============================================================================================

    @Override
    public void onBindViewHolder(@NonNull VIEW_HOLDER holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads, TAG)) {

            ITEM item = getItem(position);
            if (item != null) {
                holder.setItem(item);
                holder.getBinding().executePendingBindings();
            } else {
                onBindEmpty(holder);
            }
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull VIEW_HOLDER holder, int position) {
        throw new IllegalArgumentException("Use onBindViewHolder(ItemViewHolder<ITEM, VIEW_MODEL, BINDING, STYLE, VIEW_HOLDER> holder, int position, List<Object> payloads) instead");
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
    }


    //==============================================================================================
    // Getters
    //==============================================================================================


    protected Context getContext() {
        return mContext;
    }

    protected LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    protected LayerClient getLayerClient() {
        return mLayerClient;
    }

    protected OnRebindCallback<BINDING> getOnRebindCallback() {
        return mOnRebindCallback;
    }
}