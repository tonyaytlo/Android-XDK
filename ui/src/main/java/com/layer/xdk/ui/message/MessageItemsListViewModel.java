package com.layer.xdk.ui.message;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.adapter2.MessagesAdapter2;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class MessageItemsListViewModel extends BaseObservable {
//    protected MessageItemsAdapter mMessageItemsAdapter;
    private MessagesAdapter2 mAdapter2;

    public MessageItemsListViewModel(LayerClient layerClient,
                                     ImageCacheWrapper imageCacheWrapper,
                                     DateFormatter dateFormatter,
                                     IdentityFormatter identityFormatter) {
//        mMessageItemsAdapter = new MessageItemsAdapter(context, layerClient,
//                imageCacheWrapper, dateFormatter, identityFormatter);

        mAdapter2 = new MessagesAdapter2(layerClient,
                imageCacheWrapper, dateFormatter, identityFormatter);




    }

//    @Bindable
//    public MessageItemsAdapter getAdapter() {
//        return mMessageItemsAdapter;
//    }

    @Bindable
    public MessagesAdapter2 getAdapter() {
        return mAdapter2;
    }

    public void setItemClickListener(OnItemClickListener<Message> itemClickListener) {
//        mMessageItemsAdapter.setItemClickListener(itemClickListener);
    }
}
