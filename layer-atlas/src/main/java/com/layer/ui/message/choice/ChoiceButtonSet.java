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

import com.layer.ui.R;
import com.layer.ui.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoiceButtonSet extends LinearLayout {
    private ColorStateList mChoiceButtonColorStateList;
    private boolean mAllowReselect;
    private boolean mAllowDeselect;
    private boolean mAllowMultiSelect;
    private boolean mIsEnabledForMe;

    private Map<String, ChoiceMetadata> mChoiceMetadata = new HashMap<>();
    private OnChoiceClickedListener mOnChoiceClickedListener;

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

    public void setOnChoiceClickedListener(OnChoiceClickedListener onChoiceClickedListener) {
        mOnChoiceClickedListener = onChoiceClickedListener;
    }

    public void setSelectionConditions(boolean allowDeselect, boolean allowReselect,
                                       boolean allowMultiSelect, boolean isEnabledForMe) {
        mAllowDeselect = allowDeselect;
        mAllowReselect = allowReselect;
        mAllowMultiSelect = allowMultiSelect;
        mIsEnabledForMe = isEnabledForMe;
    }

    public boolean hasChoiceItem(ChoiceMetadata item) {
        return ((View) getParent()).findViewWithTag(item.getId()) != null;
    }

    public void addOrUpdateChoice(final ChoiceMetadata choice) {
        AppCompatButton choiceButton = ((View) getParent()).findViewWithTag(choice.getId());
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

        mChoiceMetadata.put(choice.getId(), choice);

        choiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String choiceId = (String) view.getTag();
                ChoiceMetadata choice = mChoiceMetadata.get(choiceId);
                if (mOnChoiceClickedListener != null) {
                    mOnChoiceClickedListener.onChoiceClick(choice);
                } else if (Log.isLoggable(Log.VERBOSE)) {
                    Log.v("Clicked choice but no OnChoiceClickedListener is registered. Choice: " + choiceId);
                }
            }
        });
    }

    public void setSelection(@NonNull List<String> choiceIds) {
        boolean somethingIsSelected = false;
        for (String choiceId : choiceIds) {
            AppCompatButton choiceButton = ((View) getParent()).findViewWithTag(choiceId);
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

    public interface OnChoiceClickedListener {
        void onChoiceClick(ChoiceMetadata choice);
    }
}
