package com.layer.xdk.ui.message.choice;

import android.content.Context;
import android.databinding.Observable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.databinding.UiChoiceMessageViewBinding;
import com.layer.xdk.ui.message.container.TitledMessageContainer;
import com.layer.xdk.ui.message.view.MessageView;

import java.util.List;
import java.util.Set;

public class ChoiceMessageView extends MessageView<ChoiceMessageModel> implements
        ChoiceButtonSet.OnChoiceClickedListener {
    private UiChoiceMessageViewBinding mBinding;
    private ChoiceButtonSet mChoiceButtonSet;
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
        mChoiceButtonSet = mBinding.choiceButtonSet;
        mChoiceButtonSet.setOnChoiceClickedListener(this);
        mTitle = mBinding.choiceMessageLabel;
    }

    @Override
    public void setMessageModel(final ChoiceMessageModel model) {
        mBinding.setViewModel(model);
        model.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (sender != model) return;

                if (propertyId == BR.choiceMessageMetadata || propertyId == BR.selectedChoices) {
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

            for (ChoiceMetadata choice : choices) {
                mChoiceButtonSet.addOrUpdateChoice(choice);
            }

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
        ChoiceMessageModel viewModel = mBinding.getViewModel();
        if (viewModel != null) {
            viewModel.sendResponse(choice, selected, selectedChoices);
        }
    }
}
