package com.layer.xdk.test.performance.benchmark;

import android.support.test.InstrumentationRegistry;

import com.layer.xdk.ui.DefaultXdkUiComponent;
import com.layer.xdk.ui.ServiceLocator;

public enum BenchmarkComponentManager {
    INSTANCE;

    private ServiceLocator mServiceLocator;

    public void init() {
        mServiceLocator = new ServiceLocator();
        mServiceLocator.setAppContext(InstrumentationRegistry.getTargetContext());
        mServiceLocator.setLayerClient(BenchmarkUtils.createLayerClientForBenchmark());
    }

    public DefaultXdkUiComponent getComponent() {
        return mServiceLocator.getXdkUiComponent();
    }
}
