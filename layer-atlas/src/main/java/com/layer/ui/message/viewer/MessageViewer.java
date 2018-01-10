package com.layer.ui.message.viewer;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.message.MessagePartUtils;
import com.layer.ui.message.container.MessageContainer;
import com.layer.ui.message.model.MessageModel;
import com.layer.ui.message.model.MessageModelManager;
import com.layer.ui.message.view.MessageView;
import com.layer.ui.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MessageViewer extends FrameLayout {
    private MessageModelManager mMessageModelManager;

    private MessageContainer mMessageContainer;
    private MessageView mMessageView;

    public MessageViewer(@NonNull Context context) {
        this(context, null);
    }

    public MessageViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageViewer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMessageModelManager(@NonNull MessageModelManager messageModelManager) {
        mMessageModelManager = messageModelManager;
    }

    public void setMessage(@NonNull Message message) {
        MessagePart rootMessagePart = MessagePartUtils.getMessagePartWithRoleRoot(message);
        if (rootMessagePart == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Message has no message part with role = root");
            }
            throw new IllegalArgumentException("Message does not contain a part with role = root");
        }

        setMessage(message, rootMessagePart);
    }

    public void setMessage(@NonNull Message message, @NonNull MessagePart rootMessagePart) {
        String mimeType = MessagePartUtils.getMimeType(rootMessagePart);
        if (mimeType == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Message has no message part with role = root");
            }
            throw new IllegalArgumentException("No mime type found in the root message part");
        }

        MessageModel model = mMessageModelManager.getModel(mimeType);
        if (model == null) {
            if (Log.isLoggable(Log.DEBUG)) Log.d("No model found for mime type = " + mimeType);

            return;
        }
        model.setMessageModelManager(mMessageModelManager);
        model.setMessage(message, rootMessagePart);

        bindModelToView(model);
    }

    public void setModel(@NonNull MessageModel rootModel) {
        bindModelToView(rootModel);
    }

    //==============================================================================================
    //  View Processing
    //==============================================================================================

    public void bindModelToView(@NonNull MessageModel model) {
        Class<? extends MessageView> modelType = model.getRendererType();

        try {
            /** TODO: AND-1242 Fix problem with recycling inner message viewers where the type of model
             *        being set does not match the existing model and views already in there
             *        This code, formerly would only instantiate and add message views
             *        and containers iff they were null (i.e. a freshly instantiated message viewer)
             */
            mMessageView = instantiateView(modelType);

            mMessageContainer = instantiateContainer(mMessageView.getContainerClass());
            addContainer(mMessageContainer);
            mMessageContainer.setMessageView(mMessageView);

            mMessageContainer.setMessageModel(model);

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot instantiate view with class: " + modelType.getName() + " : " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot instantiate view with class: " + modelType.getName() + " : " + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Cannot instantiate view with class: " + modelType.getName() + " : " + e.getMessage());
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot instantiate view with class: " + modelType.getName() + " : " + e.getMessage());
        }
    }

    protected MessageView instantiateView(@NonNull Class<? extends MessageView> viewTypeClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends MessageView> constructor = viewTypeClass.getConstructor(Context.class);
        MessageView messageView = constructor.newInstance(getContext());
        messageView.setMessageModelManager(mMessageModelManager);
        return messageView;
    }

    protected MessageContainer instantiateContainer(@NonNull Class<? extends MessageContainer> containerTypeClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends MessageContainer> constructor = containerTypeClass.getConstructor(Context.class);
        return constructor.newInstance(getContext());
    }

    protected void addContainer(@NonNull MessageContainer container) {
        removeAllViews();
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addView(container);
    }
}
