package com.layer.xdk.ui.test;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.hamcrest.Matchers.not;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.layer.sdk.messaging.Presence;
import com.layer.xdk.test.common.stub.IdentityStub;
import com.layer.xdk.ui.presence.PresenceView;
import com.layer.xdk.ui.testactivity.PresenceViewStubActivity;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class PresenceViewTest {

    private PresenceView mPresenceView;

    @Rule
    public ActivityTestRule<PresenceViewStubActivity> mAvatarActivityTestRule =
            new ActivityTestRule<>(PresenceViewStubActivity.class);

    @Before
    public void setUp() {
        final PresenceViewStubActivity activity = mAvatarActivityTestRule.getActivity();
        mPresenceView = activity.getPresenceView();
    }

    @Test
    public void testVisibility() throws Throwable {
        final IdentityStub alice = new IdentityStub();
        alice.mPresenceStatus = Presence.PresenceStatus.AVAILABLE;
        mAvatarActivityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPresenceView.setParticipants(alice);
            }
        });

        onView(Matchers.<View>theInstance(mPresenceView)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoneVisibility() throws Throwable {
        final IdentityStub alice = new IdentityStub();
        alice.mPresenceStatus = Presence.PresenceStatus.AVAILABLE;
        mAvatarActivityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPresenceView.setVisibility(View.GONE);
                mPresenceView.setParticipants(alice);
            }
        });

        onView(Matchers.<View>theInstance(mPresenceView)).check(matches(not(isDisplayed())));
    }
}