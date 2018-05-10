package com.layer.xdk.ui;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;

import com.layer.xdk.test.common.stub.LayerClientStub;
import com.layer.xdk.ui.conversation.ConversationItemFormatter;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.image.cache.ImageCacheWrapper;
import com.layer.xdk.ui.message.model.MessageModelManager;
import com.layer.xdk.ui.util.DateFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class DefaultXdkUiComponentTest {

    private DefaultXdkUiComponent mComponent;
    private ServiceLocator mServiceLocator;

    @Before
    public void setUp() {
        mServiceLocator = new ServiceLocator();
        mServiceLocator.setAppContext(RuntimeEnvironment.application);
        mServiceLocator.setLayerClient(new LayerClientStub());
        mComponent = DaggerDefaultXdkUiComponent
                .builder()
                .defaultXdkUiModule(new DefaultXdkUiModule(mServiceLocator))
                .build();
    }

    @Test
    public void testContextPassThrough() {
        assertThat(mComponent.applicationContext()).isSameAs(RuntimeEnvironment.application);
    }

    @Test
    public void testMessageModelManagerSingleton() {
        MessageModelManager instanceA = mComponent.messageModelManager();
        MessageModelManager instanceB = mComponent.messageModelManager();
        assertThat(instanceA).isSameAs(instanceB);
    }

    @Test
    public void testImageCacheWrapperSingleton() {
        ImageCacheWrapper instanceA = mComponent.imageCacheWrapper();
        mServiceLocator.setImageCacheWrapper(mock(ImageCacheWrapper.class));
        ImageCacheWrapper instanceB = mComponent.imageCacheWrapper();
        assertThat(instanceA).isSameAs(instanceB);
    }

    @Test
    public void testIdentityFormatterSingleton() {
        IdentityFormatter instanceA = mComponent.identityFormatter();
        mServiceLocator.setIdentityFormatter(mock(IdentityFormatter.class));
        IdentityFormatter instanceB = mComponent.identityFormatter();
        assertThat(instanceA).isSameAs(instanceB);
    }

    @Test
    public void testDateFormatterSingleton() {
        DateFormatter instanceA = mComponent.dateFormatter();
        mServiceLocator.setDateFormatter(mock(DateFormatter.class));
        DateFormatter instanceB = mComponent.dateFormatter();
        assertThat(instanceA).isSameAs(instanceB);
    }

    @Test
    public void testConversationItemFormatterSingleton() {
        ConversationItemFormatter instanceA = mComponent.conversationItemFormatter();
        mServiceLocator.setConversationItemFormatter(mock(ConversationItemFormatter.class));
        ConversationItemFormatter instanceB = mComponent.conversationItemFormatter();
        assertThat(instanceA).isSameAs(instanceB);
    }

    @Test
    public void testCanCreateDefaultIdentityFormatter() {
        assertThat(mComponent.defaultIdentityFormatter()).isNotNull();
    }

    @Test
    public void testCanCreateDefaultDateFormatter() {
        assertThat(mComponent.defaultDateFormatter()).isNotNull();
    }

    @Test
    public void testCanCreateDefaultConversationItemFormatter() {
        assertThat(mComponent.defaultConversationItemFormatter()).isNotNull();
    }
}