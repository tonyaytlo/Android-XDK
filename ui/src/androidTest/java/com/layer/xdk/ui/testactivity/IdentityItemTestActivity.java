package com.layer.xdk.ui.testactivity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarViewModel;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.databinding.TestActivityFourPartItemBinding;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.identity.IdentityItemViewModel;
import com.layer.xdk.ui.mock.MockIdentity;
import com.layer.xdk.ui.mock.MockLayerClient;
import com.layer.xdk.ui.style.FourPartItemStyle;
import com.layer.xdk.ui.util.DateFormatterImpl;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.PicassoImageCacheWrapper;
import com.squareup.picasso.Picasso;

public class IdentityItemTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageCacheWrapper imageCacheWrapper = new PicassoImageCacheWrapper(Picasso.with(this));
        IdentityFormatter identityFormatter = new IdentityFormatterImpl(getApplicationContext());
        Identity identity = new MockIdentity();

        TestActivityFourPartItemBinding binding = DataBindingUtil.setContentView(this, R.layout.test_activity_four_part_item);
        FourPartItemStyle style = new FourPartItemStyle(this, null, 0);

        IdentityItemViewModel viewModel = new IdentityItemViewModel(getApplicationContext(), new MockLayerClient());
        viewModel.setIdentityFormatter(identityFormatter);
        viewModel.setDateFormatter(new DateFormatterImpl(getApplicationContext()));
        viewModel.setItem(identity);

        AvatarViewModel avatarViewModel = new AvatarViewModelImpl(imageCacheWrapper);

        binding.testFourPartItem.avatar.init(avatarViewModel, identityFormatter);
        binding.testFourPartItem.avatar.setParticipants(identity);
        binding.testFourPartItem.setStyle(style);
        binding.testFourPartItem.setViewModel(viewModel);
    }
}
