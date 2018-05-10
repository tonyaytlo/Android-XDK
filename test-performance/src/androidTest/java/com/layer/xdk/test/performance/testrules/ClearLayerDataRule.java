package com.layer.xdk.test.performance.testrules;

import com.layer.xdk.test.performance.PerfTestingUtils;

import org.junit.rules.ExternalResource;

public class ClearLayerDataRule extends ExternalResource {

    @Override
    protected void before() {
        PerfTestingUtils.clearLayerData();
    }
}
