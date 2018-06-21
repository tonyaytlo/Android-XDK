package com.layer.xdk.ui.util;

import android.arch.lifecycle.LifecycleOwner;

/**
 * Container signaling there are {@link android.arch.lifecycle.LifecycleObserver}s that should be
 * manually registered with the encapsulating {@link LifecycleOwner}.
 */
public interface LifecycleObserverContainer {

    /**
     * Registers needed {@link android.arch.lifecycle.LifecycleObserver}s with the
     * {@link LifecycleOwner}.
     *
     * @param lifecycleOwner owner of the lifecycle to register the observers with
     */
    void addLifecycleObservers(LifecycleOwner lifecycleOwner);

}
