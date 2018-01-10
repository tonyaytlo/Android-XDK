package com.layer.ui.message.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.layer.ui.R;
import com.layer.ui.databinding.UiButtonMessageViewBinding;
import com.layer.ui.message.container.StandardMessageContainer;
import com.layer.ui.message.view.MessageView;

import java.util.List;

public class ButtonMessageView extends MessageView<ButtonMessageModel> {
    private UiButtonMessageViewBinding mBinding;
    private ColorStateList mActionButtonColorStateList;
    private ColorStateList mChoiceButtonColorStateList;

    public ButtonMessageView(Context context) {
        this(context, null, 0);
    }

    public ButtonMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = UiButtonMessageViewBinding.inflate(inflater, this, true);

        mActionButtonColorStateList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}},
                new int[]{
                        getResources().getColor(R.color.ui_button_message_action_button_text_enabled),
                        getResources().getColor(R.color.ui_button_message_action_button_text_disabled),
                        getResources().getColor(R.color.ui_button_message_action_button_text_pressed)
                });

        mChoiceButtonColorStateList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}},
                new int[]{
                        getResources().getColor(R.color.ui_button_message_choice_button_text_enabled),
                        getResources().getColor(R.color.ui_button_message_choice_button_text_disabled),
                        getResources().getColor(R.color.ui_button_message_choice_button_text_pressed)
                });
    }

    @Override
    public void setMessageModel(ButtonMessageModel model) {
        mBinding.setViewModel(model);
        if (model.getContentModel() != null) {
            mBinding.uiButtonMessageViewContent.setVisibility(VISIBLE);
            mBinding.uiButtonMessageViewContent.setModel(model.getContentModel());
        } else {
            mBinding.uiButtonMessageViewContent.setVisibility(GONE);
        }

        List<ButtonModel> buttonModels = model.getButtonModels();
        if (buttonModels != null) {
            for (ButtonModel buttonModel : buttonModels) {
                addButton(buttonModel);
            }
        }
    }

    @Override
    public Class<StandardMessageContainer> getContainerClass() {
        return StandardMessageContainer.class;
    }

    private void addButton(ButtonModel buttonModel) {
        if (buttonModel.getType().equals(ButtonModel.TYPE_ACTION)) {
            addActionButton(buttonModel);
        } else if (buttonModel.getType().equals(ButtonModel.TYPE_CHOICE)) {
            addChoiceButtons(buttonModel);
        }
    }

    private void addActionButton(@NonNull final ButtonModel buttonModel) {
        //Instantiate
        AppCompatButton actionButton = new AppCompatButton(getContext());

        // Style it
        actionButton.setBackgroundResource(R.drawable.ui_button_message_button_background_selector);
        actionButton.setTransformationMethod(null);
        actionButton.setLines(1);
        actionButton.setEllipsize(TextUtils.TruncateAt.END);
        actionButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
                .getDimension(R.dimen.ui_button_message_action_button_text_size));
        actionButton.setTextColor(mActionButtonColorStateList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionButton.setStateListAnimator(null);
        }

        // Add it
        mBinding.uiButtonMessageViewButtonsContainer.addView(actionButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Bind data to it
        actionButton.setText(buttonModel.getText());
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject data = mBinding.getViewModel().getActionData();
                String event = mBinding.getViewModel().getActionEvent();
                performAction(event, data);
            }
        });
    }

    private void addChoiceButtons(@NonNull ButtonModel buttonModel) {
        if (buttonModel.getChoices() == null || buttonModel.getChoices().isEmpty()) return;

        // Add parent for buttons
        LinearLayout choiceButtonContainerLayout = new LinearLayout(getContext());
        choiceButtonContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(choiceButtonContainerLayout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        for (ButtonModel.Choice choice : buttonModel.getChoices()) {
            addChoiceButton(choiceButtonContainerLayout, choice);
        }
    }

    private void addChoiceButton(@NonNull LinearLayout parent, @NonNull ButtonModel.Choice choice) {
        //Instantiate
        AppCompatButton choiceButton = new AppCompatButton((getContext()));

        // Style it
        choiceButton.setBackgroundResource(R.drawable.ui_button_message_button_background_selector);
        choiceButton.setTransformationMethod(null);
        choiceButton.setLines(1);
        choiceButton.setEllipsize(TextUtils.TruncateAt.END);
        choiceButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
                .getDimension(R.dimen.ui_button_message_choice_button_text_size));
        choiceButton.setTextColor(mChoiceButtonColorStateList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            choiceButton.setStateListAnimator(null);
        }

        // Add it
        parent.addView(choiceButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Bind data to it
        choiceButton.setText(choice.getText());

    }
}
