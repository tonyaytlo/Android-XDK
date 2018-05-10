package com.layer.xdk.test.performance.benchmark;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.fail;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.common.truth.Truth;
import com.layer.sdk.LayerClient;
import com.layer.xdk.test.performance.benchmark.activity.ConversationListBenchmarkActivity;
import com.layer.xdk.test.performance.testrules.ClearLayerDataRule;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.util.Util;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class BenchmarkTests {

    @Rule
    public ActivityTestRule<ConversationListBenchmarkActivity> mActivityRule = new ActivityTestRule<>(
            ConversationListBenchmarkActivity.class);

    @Rule
    public ClearLayerDataRule mClearLayerDataRule = new ClearLayerDataRule();

    @BeforeClass
    public static void clearOldResultsFile() {
        BenchmarkTestUtils.deleteBenchmarkResultFile();
    }

    @Before
    public void before() {
        IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.MINUTES);
        IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.MINUTES);
    }

    @Test
    public void runBenchmarkScenarios() {
        long startTime = System.currentTimeMillis();
        measureFirstSyncIteration(startTime);
//        measureColdSync(startTime); TODO AND-1435 Uncomment when benchmark account is fixed
        measureConversationTap(System.currentTimeMillis());
        measureActivityRestartAdapterPopulation();
        measureDeauthentication(System.currentTimeMillis());
    }

    private void measureColdSync(long startTime) {
        CountingIdlingResource coldSyncIdlingResource =
                mActivityRule.getActivity().getColdSyncResource();

        IdlingRegistry.getInstance().register(coldSyncIdlingResource);

        onView(withId(R.id.xdk_ui_items_recycler)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }

                RecyclerView.Adapter adapter = ((RecyclerView) view).getAdapter();
                assertThat(adapter.getItemCount(), is(1000));
            }
        });

        long duration = System.currentTimeMillis() - startTime;

        IdlingRegistry.getInstance().unregister(coldSyncIdlingResource);
        BenchmarkTestUtils.appendBenchmarkResult("Login to cold sync complete (scenario 7):", duration);
    }

    private void measureFirstSyncIteration(long startTime) {
        IdlingRegistry.getInstance().register(mActivityRule.getActivity().getInitialAdapterPopulationResource());

        LayerClient layerClient = mActivityRule.getActivity().getLayerClient();
        layerClient.authenticate();

        onView(withId(R.id.xdk_ui_items_recycler)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }

                RecyclerView.Adapter adapter = ((RecyclerView) view).getAdapter();
                assertThat(adapter.getItemCount(), greaterThanOrEqualTo(15));
            }
        });
        long duration = System.currentTimeMillis() - startTime;

        IdlingRegistry.getInstance().unregister(mActivityRule.getActivity().getInitialAdapterPopulationResource());
        BenchmarkTestUtils.appendBenchmarkResult("Login to first sync iteration (scenario 1)", duration);
    }

    private void measureDeauthentication(long startTime) {
        final CountDownLatch deAuthLatch = new CountDownLatch(1);
        Util.deauthenticate(mActivityRule.getActivity().getLayerClient(), new Util.DeauthenticationCallback() {
            @Override
            public void onDeauthenticationSuccess(LayerClient client) {
                deAuthLatch.countDown();
            }

            @Override
            public void onDeauthenticationFailed(LayerClient client, String reason) {
            }
        });

        try {
            Truth.assertThat(deAuthLatch.await(1, TimeUnit.MINUTES)).isTrue();
        } catch (InterruptedException e) {
            fail("Interrupted while waiting for de-authentication latch");
        }

        long duration = System.currentTimeMillis() - startTime;
        BenchmarkTestUtils.appendBenchmarkResult("Deauthentication duration (scenario 10)", duration);
    }

    private void measureConversationTap(long startTime) {
        onView(withId(R.id.xdk_ui_items_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        click()));

        onView(withId(R.id.xdk_ui_message_recycler)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }

                assertThat(((RecyclerView) view).getChildAt(0), isDisplayed());
            }
        });

        long duration = System.currentTimeMillis() - startTime;
        BenchmarkTestUtils.appendBenchmarkResult("Tap on conversation and message is visible (scenario 4)", duration);

        onView(isRoot()).perform(pressBack());
    }

    private void measureActivityRestartAdapterPopulation() {
        // Disconnect to stop any pending sync
        LayerClient layerClient = BenchmarkComponentManager.INSTANCE.getComponent().layerClient();

        // Close layer client so it will be created on the next launch
        layerClient.close();
        try {
            // Add a sleep to ensure client is closed
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            fail("Interrupted while waiting after LayerClient.close()");
        }

        long startTime = System.currentTimeMillis();

        mActivityRule.finishActivity();

        mActivityRule.launchActivity(null);
        IdlingRegistry.getInstance().register(mActivityRule.getActivity().getInitialAdapterPopulationResource());

        onView(withId(R.id.xdk_ui_items_recycler)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }
                assertThat(((RecyclerView) view).getChildAt(0), isDisplayed());
            }
        });
        IdlingRegistry.getInstance().unregister(mActivityRule.getActivity().getInitialAdapterPopulationResource());

        long duration = System.currentTimeMillis() - startTime;
        BenchmarkTestUtils.appendBenchmarkResult("App launch to conversations displayed when already authenticated (scenario 2)", duration);
    }
}
