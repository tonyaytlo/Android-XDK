package com.layer.xdk.ui.message.choice;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.databinding.XdkUiChoiceMessageViewBinding;

import java.util.List;
import java.util.Set;

public class ChoiceMessageLayout extends LinearLayout implements
        ChoiceButtonSet.OnChoiceClickedListener {
    private XdkUiChoiceMessageViewBinding mBinding;
    private ChoiceButtonSet mChoiceButtonSet;
    private TextView mTitle;

    public ChoiceMessageLayout(Context context) {
        this(context, null, 0);
    }

    public ChoiceMessageLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoiceMessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMessageModel(final ChoiceMessageModel model) {
        if (mBinding == null) {
            initializeBinding();
        }

        mBinding.setMessageModel(model);
        if (model != null) {
            model.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if (sender != model) return;

                    if (propertyId == BR.choiceMessageMetadata
                            || propertyId == BR.selectedChoices) {
                        processModel((ChoiceMessageModel) sender);
                    }
                }
            });

            updateLabel(model);
            processModel(model);
        }
    }

    private void initializeBinding() {
        mBinding = DataBindingUtil.getBinding(this);
        mChoiceButtonSet = mBinding.choiceButtonSet;
        mChoiceButtonSet.setOnChoiceClickedListener(this);
        mTitle = mBinding.choiceMessageLabel;
    }

    private void processModel(ChoiceMessageModel model) {
        if (model.getChoiceMessageMetadata() != null) {
            List<ChoiceMetadata> choices = model.getChoiceMessageMetadata().getChoices();
            Set<String> selectedChoices = model.getSelectedChoices();
            boolean allowReselect = model.getChoiceMessageMetadata().getAllowReselect();
            boolean allowDeselect = model.getChoiceMessageMetadata().getAllowDeselect();
            boolean allowMultiSelect = model.getChoiceMessageMetadata().getAllowMultiselect();
            boolean isEnabledForMe = model.getIsEnabledForMe();

            mChoiceButtonSet.setAllowDeselect(allowDeselect);
            mChoiceButtonSet.setAllowReselect(allowReselect);
            mChoiceButtonSet.setAllowMultiSelect(allowMultiSelect);
            mChoiceButtonSet.setEnabledForMe(isEnabledForMe);

            mChoiceButtonSet.setupViewsForChoices(choices);
            mChoiceButtonSet.setSelection(selectedChoices);
        }
    }

    private void updateLabel(ChoiceMessageModel model) {
        if (!model.getHasContent()) return;

        String label = model.getChoiceMessageMetadata().getLabel();
        if (!TextUtils.isEmpty(label)) {
            mTitle.setText(label);
            mTitle.setVisibility(VISIBLE);
        } else {
            mTitle.setVisibility(GONE);
        }
    }

    @Override
    public void onChoiceClick(ChoiceMetadata choice, boolean selected,
            Set<String> selectedChoices) {
        ChoiceMessageModel messageModel = mBinding.getMessageModel();
        if (messageModel != null) {
            messageModel.sendResponse(choice, selected, selectedChoices);
        }
    }
}
