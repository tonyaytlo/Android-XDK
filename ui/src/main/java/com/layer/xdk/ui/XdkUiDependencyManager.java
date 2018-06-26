package com.layer.xdk.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Manages the dependencies used to for injection into XDK components. This allows for use of a
 * {@link ServiceLocator} as well as a custom Dagger component to manage dependencies.
 *
 * <h3>Service locator usage:</h3>
 * Obtain an instance of the {@link ServiceLocator} by calling {@link #getServiceLocator()} and
 * setting the desired dependencies on that object. This instance is managed as a singleton inside
 * this {@link XdkUiDependencyManager} so it can be used app wide.
 *
 * <h3>Dagger usage:</h3>
 * To use Dagger to supply the dependencies, make your component extend from {@link XdkUiComponent}
 * and set it on this manager by calling {@link #setComponent(XdkUiComponent)}. This allows the XDK
 * to use your component when creating objects or injecting Activities/Fragments. It is important
 * that your component includes the {@link XdkUiInternalModule} and an extension of the
 * {@link XdkUiModule}.
 */
public enum XdkUiDependencyManager {
    INSTANCE;

    private ServiceLocator mServiceLocator;
    private XdkUiComponent mXdkUiComponent;

    /**
     * Obtain the instance of the {@link ServiceLocator} that this object manages.
     *
     * @return the existing {@link ServiceLocator} if it has been created. If it has not, then a new
     * instance will be created and returned.
     */
    @NonNull
    public synchronized ServiceLocator getServiceLocator() {
        if (mServiceLocator == null) {
            mServiceLocator = new ServiceLocator();
        }
        return mServiceLocator;
    }

    /**
     * Set the Dagger component to use for dependency management. This component <b>must</b> include
     * the {@link XdkUiInternalModule} and an extension of the {@link XdkUiModule}.
     *
     * @param component Dagger component to use for dependencies
     */
    public synchronized void setComponent(@Nullable XdkUiComponent component) {
        mXdkUiComponent = component;
    }

    /**
     * If using a custom Dagger component set via {@link #setComponent(XdkUiComponent)}, this will
     * return that component.
     *
     * If no custom Dagger component is set then it is assumed that the {@link ServiceLocator} is
     * being used. In this case the component will be fetched from the {@link ServiceLocator}.
     *
     * @return a component to create XDK objects
     */
    @NonNull
    public synchronized XdkUiComponent getXdkUiComponent() {
        if (mXdkUiComponent == null) {
            // Use the component from the service locator as the default
            return getServiceLocator().getXdkUiComponent();
        }
        return mXdkUiComponent;
    }
}
