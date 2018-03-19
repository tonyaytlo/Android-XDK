package com.layer.xdk.ui.testactivity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.DaggerXdkUiTestComponent;
import com.layer.xdk.ui.FakeXdkUiModule;
import com.layer.xdk.ui.XdkUiTestComponent;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarViewModel;
import com.layer.xdk.ui.databinding.TestActivityFourPartItemBinding;
import com.layer.xdk.ui.identity.IdentityItemViewModel;
import com.layer.xdk.ui.mock.MockIdentity;
import com.layer.xdk.ui.style.FourPartItemStyle;

public class IdentityItemTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XdkUiTestComponent component = DaggerXdkUiTestComponent.builder()
                .fakeXdkUiModule(new FakeXdkUiModule(this)).build();

        Identity identity = new MockIdentity();

        TestActivityFourPartItemBinding binding = DataBindingUtil.setContentView(this, R.layout.test_activity_four_part_item);
        FourPartItemStyle style = new FourPartItemStyle(this, null, 0);

        IdentityItemViewModel viewModel = component.identityItemViewModel();
        viewModel.setItem(identity);

        AvatarViewModel avatarViewModel = component.avatarViewModel();

        binding.testFourPartItem.avatar.init(avatarViewModel);
        binding.testFourPartItem.avatar.setParticipants(identity);
        binding.testFourPartItem.setStyle(style);
        binding.testFourPartItem.setViewModel(viewModel);
    }
}
