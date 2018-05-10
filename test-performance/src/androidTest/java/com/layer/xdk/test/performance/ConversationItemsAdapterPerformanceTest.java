package com.layer.xdk.test.performance;

import android.Manifest;
import android.arch.core.executor.ArchTaskExecutor;
import android.arch.paging.PagedList;
import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.test.common.stub.ConversationStub;
import com.layer.xdk.test.common.stub.IdentityStub;
import com.layer.xdk.test.common.stub.LayerClientStub;
import com.layer.xdk.test.common.stub.MessagePartStub;
import com.layer.xdk.test.common.stub.MessageStub;
import com.layer.xdk.test.performance.testrules.EnableDeviceGetPropsInfo;
import com.layer.xdk.test.performance.testrules.EnableLogcatDump;
import com.layer.xdk.test.performance.testrules.EnablePostTestDumpsys;
import com.layer.xdk.ui.DefaultXdkUiModule;
import com.layer.xdk.ui.ServiceLocator;
import com.layer.xdk.ui.conversation.adapter.ConversationItemModel;
import com.layer.xdk.ui.conversation.adapter.ConversationItemsAdapter;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.text.TextMessageModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConversationItemsAdapterPerformanceTest {

    private LayerClientStub mLayerClientStub;

    private PerformanceTestComponent mTestComponent;


    @Rule
    public ActivityTestRule<ConversationItemsAdapterPerformanceActivity> mActivityRule =
            new ActivityTestRule<>(ConversationItemsAdapterPerformanceActivity.class);

    @Rule
    public EnablePostTestDumpsys mPostTestDumpsys = new EnablePostTestDumpsys();

    @Rule
    public EnableDeviceGetPropsInfo mDeviceGetPropsInfo = new EnableDeviceGetPropsInfo();

    @Rule
    public EnableLogcatDump mLogcatDump = new EnableLogcatDump();

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Before
    public void setUp() {
        mLayerClientStub = new LayerClientStub();
        IdentityStub authenticatedUser = new IdentityStub();
        authenticatedUser.mDisplayName = "Authenticated User";
        mLayerClientStub.mAuthenticatedUser = authenticatedUser;

        ServiceLocator serviceLocator = new ServiceLocator();
        serviceLocator.setAppContext(mActivityRule.getActivity().getApplicationContext());
        serviceLocator.setLayerClient(mLayerClientStub);

        mTestComponent = DaggerPerformanceTestComponent.builder()
                .defaultXdkUiModule(new DefaultXdkUiModule(serviceLocator))
                .build();
    }

    @Test
    public void testJank() throws Throwable {
        final ConversationItemsAdapter adapter = mTestComponent.conversationItemsAdapter();

        int totalSize = 1000;

        PagedList<ConversationItemModel> list = new PagedList.Builder<>(new JankDataSource(totalSize), 90)
                .setMainThreadExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .setBackgroundThreadExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .build();
        adapter.submitList(list);
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivityRule.getActivity().setAdapter(adapter);
            }
        });

        RecyclerView recyclerView = mActivityRule.getActivity().findViewById(R.id.xdk_ui_items_recycler);
        int lastPosition = totalSize - 1;
        recyclerView.smoothScrollToPosition(lastPosition);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        while (layoutManager.findLastCompletelyVisibleItemPosition() != (lastPosition)) {
            Thread.sleep(300);
        }
    }

    private class JankDataSource extends PositionalDataSource<ConversationItemModel> {

        private final int mTotalCount;

        private JankDataSource(int totalCount) {
            mTotalCount = totalCount;
        }


        @Override
        public void loadInitial(@NonNull LoadInitialParams params,
                @NonNull LoadInitialCallback<ConversationItemModel> callback) {
            callback.onResult(createStubs(params.requestedLoadSize), params.requestedStartPosition, mTotalCount);
        }

        @Override
        public void loadRange(@NonNull LoadRangeParams params,
                @NonNull LoadRangeCallback<ConversationItemModel> callback) {
            callback.onResult(createStubs(params.loadSize));
        }

        @NonNull
        private List<ConversationItemModel> createStubs(int count) {
            List<ConversationItemModel> models = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                // Create conversation and participants
                ConversationStub conversation = new ConversationStub();
                conversation.mParticipants = new HashSet<>();
                conversation.mParticipants.add(mLayerClientStub.mAuthenticatedUser);
                IdentityStub otherUser = new IdentityStub();
                otherUser.mDisplayName = UUID.randomUUID().toString();
                conversation.mParticipants.add(otherUser);

                // Create last message and part
                MessageStub lastMessage = new MessageStub();
                lastMessage.mConversation = conversation;
                lastMessage.mSender = otherUser;
                lastMessage.mReceivedAt = new Date();
                MessagePartStub lastMessagePart = new MessagePartStub();
                lastMessagePart.mData = UUID.randomUUID().toString().getBytes();
                lastMessage.mMessageParts = Collections.singleton((MessagePart) lastMessagePart);
                conversation.mLastMessage = lastMessage;

                // Create model
                MessageModel lastMessageModel = new TextMessageModel(
                        InstrumentationRegistry.getContext(), mLayerClientStub, lastMessage);
                lastMessageModel.processPartsFromTreeRoot();
                models.add(new ConversationItemModel(conversation, lastMessageModel, mLayerClientStub.mAuthenticatedUser));
            }
            return models;
        }
    }
}
