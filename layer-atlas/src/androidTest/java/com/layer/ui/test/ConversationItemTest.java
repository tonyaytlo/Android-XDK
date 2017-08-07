package com.layer.ui.test;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.layer.ui.*;
import com.layer.ui.R;
import com.layer.ui.testactivity.ConversationItemTestActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ConversationItemTest {

    @Rule
    public ActivityTestRule<ConversationItemTestActivity> mActivityTestRule
            = new ActivityTestRule<ConversationItemTestActivity>(ConversationItemTestActivity.class);

    @Before
    public void setup() {
        final ConversationItemTestActivity activity = mActivityTestRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    @Test
    public void testViewIsDisplayed() {
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.right_accessory_text)).check(matches(isDisplayed()));
        onView(withId(R.id.avatar)).check(matches(isDisplayed()));
        onView(withId(R.id.subtitle)).check(matches(isDisplayed()));
    }
}
