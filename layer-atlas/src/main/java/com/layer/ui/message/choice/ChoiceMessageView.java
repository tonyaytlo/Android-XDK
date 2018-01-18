package com.layer.ui.message.choice;

import android.content.Context;
import android.databinding.Observable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.layer.ui.BR;
import com.layer.ui.databinding.UiChoiceMessageViewBinding;
import com.layer.ui.message.container.TitledMessageContainer;
import com.layer.ui.message.view.MessageView;

import java.util.List;

public class ChoiceMessageView extends MessageView<ChoiceMessageModel> {
    private UiChoiceMessageViewBinding mBinding;
    private LinearLayout mChoiceSetsParentLayout;
    private TextView mTitle;

    public ChoiceMessageView(Context context) {
        this(context, null, 0);
    }

    public ChoiceMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoiceMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);

        mBinding = UiChoiceMessageViewBinding.inflate(inflater, this, true);
        mChoiceSetsParentLayout = mBinding.choiceButtonsParent;
        mTitle = mBinding.choiceMessageLabel;
    }

    @Override
    public void setMessageModel(final ChoiceMessageModel model) {
        mBinding.setViewModel(model);
        model.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (sender != model) return;

                if (propertyId == BR.choiceMessageMetadata) {
                    processModel((ChoiceMessageModel) sender);
                }

            }
        });

        updateLabel(model);
        processModel(model);
    }

    @Override
    public Class<TitledMessageContainer> getContainerClass() {
        return TitledMessageContainer.class;
    }

    private void processModel(ChoiceMessageModel model) {
        if (model.getChoiceMessageMetadata() != null) {
            List<ChoiceModel> choiceModels = model.getChoiceMessageMetadata().getChoices();
            List<String> selectedChoices = model.getSelectedChoices();
            boolean allowReselect = model.getChoiceMessageMetadata().getAllowReselect();
            boolean allowDeselect = model.getChoiceMessageMetadata().getAllowDeselect();
            boolean allowMultiSelect = model.getChoiceMessageMetadata().getAllowMultiselect();
            boolean isEnabledForMe = model.getIsEnabledForMe();

            for (ChoiceModel choiceModel : choiceModels) {
                addOrUpdateChoiceMessageSet(choiceModel);
            }

            for (ChoiceModel choiceModel : choiceModels) {
                updateChoices(choiceModel, selectedChoices, allowMultiSelect, allowDeselect,
                        allowReselect, isEnabledForMe);
            }
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

    private void addOrUpdateChoiceMessageSet(ChoiceModel choiceModel) {
        for (int i = 0; i < mChoiceSetsParentLayout.getChildCount(); i++) {
            ChoiceButtonSet choiceButtonSet = (ChoiceButtonSet) mChoiceSetsParentLayout.getChildAt(i);
            if (choiceButtonSet.hasChoiceItem(choiceModel)) {
                choiceButtonSet.addOrUpdateChoice(choiceModel);
                return;
            }
        }

        ChoiceButtonSet choiceButtonSet = new ChoiceButtonSet(getContext());
        mChoiceSetsParentLayout.addView(choiceButtonSet);
        choiceButtonSet.addOrUpdateChoice(choiceModel);
    }

    private void updateChoices(ChoiceModel choiceModel, @NonNull List<String> selectedChoices,
                               boolean allowMultiSelect, boolean allowDeselect,
                               boolean allowReselect, boolean isEnabledForMe) {
        for (int i = 0; i < mChoiceSetsParentLayout.getChildCount(); i++) {
            ChoiceButtonSet choiceButtonSet = (ChoiceButtonSet) mChoiceSetsParentLayout.getChildAt(i);
            if (choiceButtonSet.hasChoiceItem(choiceModel)) {
                choiceButtonSet.setSelectionConditions(allowDeselect, allowReselect, allowMultiSelect, isEnabledForMe);
                choiceButtonSet.setSelection(selectedChoices);
            }
        }
    }
}
