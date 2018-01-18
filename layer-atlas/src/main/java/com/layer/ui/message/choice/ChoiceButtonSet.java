package com.layer.ui.message.choice;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.layer.ui.R;

import java.util.List;

public class ChoiceButtonSet extends LinearLayout {
    private ColorStateList mChoiceButtonColorStateList;
    private boolean mAllowReselect;
    private boolean mAllowDeselect;
    private boolean mAllowMultiSelect;
    private boolean mIsEnabledForMe;

    public ChoiceButtonSet(Context context) {
        this(context, null, 0);
    }

    public ChoiceButtonSet(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoiceButtonSet(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        setOrientation(HORIZONTAL);

        mChoiceButtonColorStateList = ContextCompat.getColorStateList(context, R.color.ui_choice_button_selector);
    }

    public void setSelectionConditions(boolean allowDeselect, boolean allowReselect,
                                       boolean allowMultiSelect, boolean isEnabledForMe) {
        mAllowDeselect = allowDeselect;
        mAllowReselect = allowReselect;
        mAllowMultiSelect = allowMultiSelect;
        mIsEnabledForMe = isEnabledForMe;
    }

    public boolean hasChoiceItem(ChoiceModel item) {
        return getRootView().findViewWithTag(item.getId()) != null;
    }

    public void addOrUpdateChoice(final ChoiceModel choice) {
        AppCompatButton choiceButton = getRootView().findViewWithTag(choice.getId());
        if (choiceButton == null) {
            // Instantiate
            choiceButton = new AppCompatButton((getContext()));

            // Style it
            choiceButton.setBackgroundResource(R.drawable.ui_choice_set_button_background_selector);
            choiceButton.setTransformationMethod(null);
            choiceButton.setLines(1);
            choiceButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
                    .getDimension(R.dimen.ui_choice_button_message_button_text_size));
            choiceButton.setTextColor(mChoiceButtonColorStateList);
            choiceButton.setSingleLine(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                choiceButton.setStateListAnimator(null);
            }

            // Add it
            addView(choiceButton, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            choiceButton.setTag(choice.getId());
        }

        choiceButton.setText(choice.getText());

        choiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Clicked on choice: " + view.getTag() + " text" + choice.getText(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setSelection(@NonNull List<String> choiceIds) {
        boolean somethingIsSelected = false;
        for (String choiceId : choiceIds) {
            AppCompatButton choiceButton = getRootView().findViewWithTag(choiceId);
            if (choiceButton != null) {
                choiceButton.setSelected(true);
                choiceButton.setClickable(mAllowDeselect);
                somethingIsSelected = true;
                // Move on if further selections are not allowed
                if (!mAllowMultiSelect) break;
            }
        }

        if (!mAllowReselect) {
            for (int i = 0; i < getChildCount(); i++) {
                AppCompatButton button = (AppCompatButton) getChildAt(i);
                String tag = (String) button.getTag();
                if (!choiceIds.contains(tag)) {
                    boolean enabled = false;
                    if (mIsEnabledForMe) {
                        if (somethingIsSelected) {
                            enabled = mAllowMultiSelect || mAllowReselect || mAllowDeselect;
                        } else {
                            enabled = true;
                        }
                    }

                    button.setEnabled(enabled);
                }
            }
        }
    }
}
