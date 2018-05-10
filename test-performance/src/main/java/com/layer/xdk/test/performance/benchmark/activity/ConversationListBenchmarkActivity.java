package com.layer.xdk.test.performance.benchmark.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.widget.RecyclerView;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerSyncListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.xdk.test.performance.benchmark.BenchmarkComponentManager;
import com.layer.xdk.ui.DefaultXdkUiComponent;
import com.layer.xdk.ui.conversation.ConversationItemsListView;
import com.layer.xdk.ui.conversation.ConversationItemsListViewModel;
import com.layer.xdk.ui.conversation.adapter.ConversationItemModel;
import com.layer.xdk.ui.conversation.adapter.ConversationItemsAdapter;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;

import java.util.List;

public class ConversationListBenchmarkActivity extends Activity {

    private CountingIdlingResource mInitialAdapterPopulationResource;
    private CountingIdlingResource mColdSyncResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up idling resources
        mInitialAdapterPopulationResource = new CountingIdlingResource("FirstSyncIteration");
        mColdSyncResource = new CountingIdlingResource("ColdSync");
        mInitialAdapterPopulationResource.increment();
        mColdSyncResource.increment();

        // Set up LayerClient
        BenchmarkComponentManager.INSTANCE.init();
        DefaultXdkUiComponent component = BenchmarkComponentManager.INSTANCE.getComponent();

        component.layerClient().registerSyncListener(new LayerSyncListener() {
            @Override
            public void onBeforeSync(LayerClient layerClient, SyncType syncType) {
            }

            @Override
            public void onSyncProgress(LayerClient layerClient, SyncType syncType,
                    int progress) {
            }

            @Override
            public void onAfterSync(LayerClient layerClient, SyncType syncType) {
                mColdSyncResource.decrement();
            }

            @Override
            public void onSyncError(LayerClient layerClient, List<LayerException> list) {
            }
        });

        // Set up view
        ConversationItemsListView listView = new ConversationItemsListView(this);
        setContentView(listView);

        ConversationItemsListViewModel viewModel = component.conversationItemsListViewModel();
        viewModel.setItemClickListener(new OnItemClickListener<ConversationItemModel>() {
            @Override
            public void onItemClick(ConversationItemModel item) {
                Conversation conversation = item.getConversation();
                Intent intent = new Intent(ConversationListBenchmarkActivity.this, ConversationBenchmarkActivity.class);
                intent.putExtra(ConversationBenchmarkActivity.CONVERSATION_KEY, conversation.getId());
                startActivity(intent);
            }
        });
        viewModel.useDefaultQuery();
        ConversationItemsAdapter conversationItemsAdapter = viewModel.getConversationItemsAdapter();
        listView.setAdapter(conversationItemsAdapter);

        conversationItemsAdapter.registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        if (itemCount > 0 && !mInitialAdapterPopulationResource.isIdleNow()) {
                            mInitialAdapterPopulationResource.decrement();
                        }
                    }
                });
    }

    public LayerClient getLayerClient() {
        return BenchmarkComponentManager.INSTANCE.getComponent().layerClient();
    }

    public CountingIdlingResource getInitialAdapterPopulationResource() {
        return mInitialAdapterPopulationResource;
    }

    public CountingIdlingResource getColdSyncResource() {
        return mColdSyncResource;
    }


}
