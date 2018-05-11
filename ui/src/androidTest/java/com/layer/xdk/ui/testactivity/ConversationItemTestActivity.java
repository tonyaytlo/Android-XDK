package com.layer.xdk.ui.testactivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.DaggerXdkUiTestComponent;
import com.layer.xdk.ui.FakeXdkUiModule;
import com.layer.xdk.ui.XdkUiTestComponent;
import com.layer.xdk.ui.conversation.adapter.ConversationItemModel;
import com.layer.xdk.ui.conversation.adapter.viewholder.ConversationItemVHModel;
import com.layer.xdk.ui.databinding.XdkUiFourPartItemBinding;
import com.layer.xdk.ui.fourpartitem.FourPartItemStyle;
import com.layer.xdk.ui.mock.MockConversation;

public class ConversationItemTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XdkUiTestComponent component = DaggerXdkUiTestComponent.builder()
                .fakeXdkUiModule(new FakeXdkUiModule(this)).build();

        LayerClient layerClient = component.layerClient();

        Identity authenticatedUser = layerClient.getAuthenticatedUser();
        Conversation conversation = new MockConversation(authenticatedUser, 3);

        FrameLayout frameLayout = new FrameLayout(this);
        XdkUiFourPartItemBinding binding = XdkUiFourPartItemBinding.inflate(
                LayoutInflater.from(this), frameLayout, true);
        setContentView(frameLayout);

        FourPartItemStyle style = new FourPartItemStyle(this, null, 0);

        ConversationItemVHModel viewHolderModel = component.conversationItemViewModel();
        ConversationItemModel itemModel = new ConversationItemModel(conversation, null, authenticatedUser);
        viewHolderModel.setItem(itemModel);

        binding.avatar.setImageCacheWrapper(viewHolderModel.getImageCacheWrapper());
        binding.avatar.setIdentityFormatter(viewHolderModel.getIdentityFormatter());
        binding.avatar.setParticipants(conversation.getParticipants());
        binding.setStyle(style);
        binding.setViewHolderModel(viewHolderModel);
    }
}
