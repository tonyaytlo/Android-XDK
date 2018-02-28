package com.layer.xdk.ui.testactivity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarViewModel;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.conversationitem.ConversationItemFormatter;
import com.layer.xdk.ui.conversationitem.ConversationItemViewModel;
import com.layer.xdk.ui.databinding.TestActivityFourPartItemBinding;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.mock.MockConversation;
import com.layer.xdk.ui.mock.MockLayerClient;
import com.layer.xdk.ui.style.FourPartItemStyle;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.PicassoImageCacheWrapper;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;

public class ConversationItemTestActivity extends Activity {

    private ConversationItemFormatter mConversationItemFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageCacheWrapper imageCacheWrapper = new PicassoImageCacheWrapper(Picasso.with(this));
        LayerClient layerClient = new MockLayerClient();

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        IdentityFormatter identityFormatter = new IdentityFormatterImpl(getApplicationContext());
        mConversationItemFormatter = new ConversationItemFormatter(this, layerClient, identityFormatter, dateFormat, timeFormat);

        Identity authenticatedUser = layerClient.getAuthenticatedUser();
        Conversation conversation = new MockConversation(authenticatedUser, 3);

        TestActivityFourPartItemBinding binding = DataBindingUtil.setContentView(this, R.layout.test_activity_four_part_item);
        FourPartItemStyle style = new FourPartItemStyle(this, null, 0);

        ConversationItemViewModel viewModel = new ConversationItemViewModel(this.getApplicationContext(), layerClient);
        viewModel.setConversationItemFormatter(mConversationItemFormatter);
        viewModel.setAuthenticatedUser(layerClient.getAuthenticatedUser());
        viewModel.setItem(conversation);

        AvatarViewModel avatarViewModel = new AvatarViewModelImpl(imageCacheWrapper);
        binding.testFourPartItem.avatar.init(avatarViewModel, identityFormatter);

        binding.testFourPartItem.avatar.setParticipants(conversation.getParticipants());
        binding.testFourPartItem.setStyle(style);
        binding.testFourPartItem.setViewModel(viewModel);
    }
}
