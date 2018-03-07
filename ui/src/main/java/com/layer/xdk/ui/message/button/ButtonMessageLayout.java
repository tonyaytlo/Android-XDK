package com.layer.xdk.ui.message.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiButtonMessageViewBinding;
import com.layer.xdk.ui.message.view.MessageViewHelper;
import com.layer.xdk.ui.message.choice.ChoiceButtonSet;
import com.layer.xdk.ui.message.choice.ChoiceMetadata;
import com.layer.xdk.ui.message.container.MessageContainer;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.view.ParentMessageView;
import com.layer.xdk.ui.util.Log;

import java.util.List;
import java.util.Set;

public class ButtonMessageLayout extends ConstraintLayout implements ParentMessageView {
    private static final String BUTTON_SET_TAG_PREFIX = "ChoiceButtonSet-";

    private XdkUiButtonMessageViewBinding mBinding;
    private MessageViewHelper mMessageViewHelper;
    private ColorStateList mActionButtonColorStateList;

    public ButtonMessageLayout(Context context) {
        this(context, null, 0);
    }

    public ButtonMessageLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonMessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMessageViewHelper = new MessageViewHelper(context);

        // TODO Use the choice selector until AND-1271 is fixed
        mActionButtonColorStateList = ContextCompat.getColorStateList(context, R.color.xdk_ui_choice_button_selector);
    }

    @Override
    public <T extends MessageModel> void inflateChildLayouts(@NonNull T model,
            @NonNull OnLongClickListener longClickListener) {
        if (!(model instanceof ButtonMessageModel)) {
            // Nothing to do with a non button model
            return;
        }
        ButtonMessageModel buttonModel = (ButtonMessageModel) model;
        MessageModel contentModel = buttonModel.getContentModel();
        if (contentModel == null) {
            // Nothing to do
            return;
        }
        mBinding = DataBindingUtil.getBinding(this);
        ViewStub viewStub = mBinding.xdkUiButtonMessageViewContent.getViewStub();
        viewStub.setLayoutResource(contentModel.getContainerViewLayoutId());
        MessageContainer container = (MessageContainer) viewStub.inflate();
        container.setDrawBorder(false);
        View contentView = container.inflateMessageView(contentModel.getViewLayoutId());
        contentView.setOnLongClickListener(longClickListener);
        if (contentView instanceof ParentMessageView) {
            ((ParentMessageView) contentView).inflateChildLayouts(contentModel, longClickListener);
        }
    }

    public void setMessageModel(ButtonMessageModel model) {
        mBinding = DataBindingUtil.getBinding(this);
        mMessageViewHelper.setMessageModel(model);
        if (model == null) {
            return;
        }
        if (model.getContentModel() != null) {
            MessageContainer messageContainer =
                    (MessageContainer) mBinding.xdkUiButtonMessageViewContent.getRoot();
            if (messageContainer != null) {
                messageContainer.setMessageModel(model.getContentModel());
            }
        }

        // This is the easy but expensive way to do this. Improve with AND-1374
        mBinding.xdkUiButtonMessageViewButtonsContainer.removeAllViews();
        addOrUpdateButtonsFromModel();

        model.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                addOrUpdateButtonsFromModel();
            }
        });
        mBinding.executePendingBindings();
    }

    private void addOrUpdateButtonsFromModel() {
        ButtonMessageModel model = mBinding.getMessageModel();
        if (model == null) {
            return;
        }
        List<ButtonModel> buttonModels = model.getButtonModels();
        if (buttonModels != null) {
            for (ButtonModel buttonModel : buttonModels) {
                addOrUpdateButton(buttonModel);
            }
        }
    }

    private void addOrUpdateButton(ButtonModel buttonModel) {
        if (buttonModel.getType().equals(ButtonModel.TYPE_ACTION)) {
            addOrUpdateActionButton(buttonModel);
        } else if (buttonModel.getType().equals(ButtonModel.TYPE_CHOICE)) {
            addOrUpdateChoiceButtons(buttonModel);
        }
    }

    private void addOrUpdateActionButton(@NonNull final ButtonModel buttonModel) {
        //Instantiate
        AppCompatButton actionButton = findViewWithTag(buttonModel.getText());
        if (actionButton == null) {
            actionButton = new AppCompatButton(getContext());

            // Style it
            actionButton.setBackgroundResource(R.drawable.xdk_ui_choice_set_button_background_selector);
            actionButton.setTransformationMethod(null);
            actionButton.setLines(1);
            actionButton.setEllipsize(TextUtils.TruncateAt.END);
            actionButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
                    .getDimension(R.dimen.xdk_ui_button_message_action_button_text_size));
            actionButton.setTextColor(mActionButtonColorStateList);
            actionButton.setTag(buttonModel.getText());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                actionButton.setStateListAnimator(null);
            }

            // Add it
            mBinding.xdkUiButtonMessageViewButtonsContainer.addView(actionButton,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        // Bind data to it
        actionButton.setText(buttonModel.getText());
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessageViewHelper.performAction();
            }
        });
    }

    private void addOrUpdateChoiceButtons(@NonNull ButtonModel buttonModel) {
        if (buttonModel.getChoices() == null || buttonModel.getChoices().isEmpty()) return;
        final ButtonModel.ChoiceData choiceData = buttonModel.getChoiceData();
        if (choiceData == null || choiceData.getResponseName() == null) {
            if (Log.isLoggable(Log.WARN)) {
                Log.w("No response name for this choice set, not adding choice buttons");
            }
            return;
        }

        // Prefix this tag in case buttons have the same response name
        String buttonSetTag = BUTTON_SET_TAG_PREFIX + choiceData.getResponseName();
        ChoiceButtonSet choiceButtonSet = findViewWithTag(buttonSetTag);
        if (choiceButtonSet == null) {
            choiceButtonSet = new ChoiceButtonSet(getContext());
            choiceButtonSet.setOrientation(LinearLayout.HORIZONTAL);
            choiceButtonSet.setTag(buttonSetTag);
            mBinding.xdkUiButtonMessageViewButtonsContainer.addView(choiceButtonSet,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        choiceButtonSet.setupViewsForChoices(buttonModel.getChoices());
        choiceButtonSet.setEnabledForMe(choiceData.isEnabledForMe());
        choiceButtonSet.setAllowDeselect(choiceData.isAllowDeselect());
        choiceButtonSet.setAllowReselect(choiceData.isAllowReselect());
        choiceButtonSet.setAllowMultiSelect(choiceData.isAllowMultiselect());

        choiceButtonSet.setOnChoiceClickedListener(new ChoiceButtonSet.OnChoiceClickedListener() {
            @Override
            public void onChoiceClick(ChoiceMetadata choice, boolean selected,
                    Set<String> selectedChoices) {
                ButtonMessageModel messageModel = mBinding.getMessageModel();
                if (messageModel != null) {
                    messageModel.onChoiceClicked(choiceData, choice, selected, selectedChoices);
                }
            }
        });

        Set<String> selectedChoices = mBinding.getMessageModel().getSelectedChoices(
                choiceData.getResponseName());

        choiceButtonSet.setSelection(selectedChoices);
    }
}
