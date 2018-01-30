package com.layer.ui.message.product;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.layer.ui.databinding.UiProductOptionViewItemBinding;
import com.layer.ui.message.choice.ChoiceMessageModel;
import com.layer.ui.message.choice.ChoiceMetadata;

import java.util.List;

public class ProductOptionsView extends LinearLayout {
    private List<ChoiceMessageModel> mOptions;

    public ProductOptionsView(Context context) {
        this(context, null, 0);
    }

    public ProductOptionsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductOptionsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    public List<ChoiceMessageModel> getOptions() {
        return mOptions;
    }

    @BindingAdapter("app:setOptions")
    public static void setOptions(ProductOptionsView view, @NonNull List<ChoiceMessageModel> options) {
        view.mOptions = options;
        view.renderOptions();
    }

    private void renderOptions() {
        removeAllViews();
        if (mOptions != null && !mOptions.isEmpty()) {
            for (ChoiceMessageModel option : mOptions) {
                String choiceId = option.getSelectedChoices() != null ? option.getSelectedChoices().iterator().next() : null;
                List<ChoiceMetadata> choices = option.getChoiceMessageMetadata() != null ? option.getChoiceMessageMetadata().getChoices() : null;

                if (choices != null && choices.size() > 0) {
                    for (ChoiceMetadata choice : choices) {
                        if (choice.getId().equals(choiceId)) {
                            // Instantiate and add view
                            UiProductOptionViewItemBinding binding = UiProductOptionViewItemBinding.inflate(LayoutInflater.from(getContext()), this, true);

                            // Set data on it
                            binding.optionTitle.setText(option.getChoiceMessageMetadata().getLabel());
                            binding.optionText.setText(choice.getText());

                            break;
                        }
                    }
                }
            }
        }

        if (getChildCount() == 0) {
            setVisibility(GONE);
        }
    }
}
