package com.layer.xdk.ui.message.choice;

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

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChoiceButtonSet extends LinearLayout {
    private ColorStateList mChoiceButtonColorStateList;
    private boolean mAllowReselect;
    private boolean mAllowDeselect;
    private boolean mAllowMultiSelect;
    private boolean mEnabledForMe;

    private Map<String, ChoiceMetadata> mChoiceMetadata = new HashMap<>();
    private OnChoiceClickedListener mOnChoiceClickedListener;
    private Set<String> mSelectedChoiceIds = new HashSet<>();

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

        // Default orientation
        setOrientation(VERTICAL);

        mChoiceButtonColorStateList = ContextCompat.getColorStateList(context, R.color.xdk_ui_choice_button_selector);
    }

    public void setOnChoiceClickedListener(OnChoiceClickedListener onChoiceClickedListener) {
        mOnChoiceClickedListener = onChoiceClickedListener;
    }

    public void setAllowReselect(boolean allowReselect) {
        mAllowReselect = allowReselect;
    }

    public void setAllowDeselect(boolean allowDeselect) {
        mAllowDeselect = allowDeselect;
    }

    public void setAllowMultiSelect(boolean allowMultiSelect) {
        mAllowMultiSelect = allowMultiSelect;
    }

    public void setEnabledForMe(boolean enabledForMe) {
        mEnabledForMe = enabledForMe;
    }

    public void setupViewsForChoices(List<ChoiceMetadata> metadata) {
        mChoiceMetadata.clear();
        // Ensure we have the right number of views
        // Add needed views
        for (int i = getChildCount(); i < metadata.size(); i++) {
            addButton();
        }

        // Remove extra views
        int childCount = getChildCount();
        for (int i = metadata.size(); i < childCount; i++) {
            removeViewAt(i);
        }

        // Set up buttons
        for (int i = 0; i < getChildCount(); i++) {
            AppCompatButton child = (AppCompatButton) getChildAt(i);
            // We have to request a layout here since the recycled view may be wider. This needs
            // optimization (AND-1372)
            child.requestLayout();
            updateChoice(child, metadata.get(i));
        }
    }

    private void addButton() {
        // Instantiate
        AppCompatButton choiceButton = new AppCompatButton((getContext()));

        // Style it
        choiceButton.setBackgroundResource(R.drawable.xdk_ui_choice_set_button_background_selector);
        choiceButton.setTransformationMethod(null);
        choiceButton.setLines(1);
        choiceButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
                .getDimension(R.dimen.xdk_ui_choice_button_message_button_text_size));
        choiceButton.setTextColor(mChoiceButtonColorStateList);
        choiceButton.setSingleLine(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            choiceButton.setStateListAnimator(null);
        }

        // Add it
        LayoutParams layoutParams;
        if (getOrientation() == VERTICAL) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        }
        addView(choiceButton, layoutParams);
    }

    public void updateChoice(AppCompatButton choiceButton, final ChoiceMetadata choice) {
        choiceButton.setTag(choice.getId());
        choiceButton.setText(choice.getText());

        mChoiceMetadata.put(choice.getId(), choice);

        choiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String choiceId = (String) view.getTag();
                boolean willBeSelected = !view.isSelected();
                toggleChoice(choiceId);

                ChoiceMetadata choice = mChoiceMetadata.get(choiceId);
                if (mOnChoiceClickedListener != null) {
                    mOnChoiceClickedListener.onChoiceClick(choice, willBeSelected, mSelectedChoiceIds);
                } else if (Log.isLoggable(Log.VERBOSE)) {
                    Log.v("Clicked choice but no OnChoiceClickedListener is registered. Choice: " + choiceId);
                }
            }
        });
    }

    private void toggleChoice(String choiceId) {
        if (mSelectedChoiceIds.contains(choiceId)) {
            // Currently selected, deselect if allowed
            if (mAllowDeselect) {
                mSelectedChoiceIds.remove(choiceId);
            }
        } else {
            // Un-select others if multi-select is not allowed
            if (!mAllowMultiSelect) {
                mSelectedChoiceIds.clear();
            }
            mSelectedChoiceIds.add(choiceId);

        }
        setSelection(mSelectedChoiceIds);
    }

    public void setSelection(@NonNull Set<String> choiceIds) {
        mSelectedChoiceIds = choiceIds;
        boolean somethingIsSelected = false;

        for (int i = 0; i < getChildCount(); i++) {
            AppCompatButton choiceButton = (AppCompatButton) getChildAt(i);
            String tag = (String) choiceButton.getTag();
            if (choiceIds.contains(tag)) {
                choiceButton.setSelected(true);
                somethingIsSelected = true;
            } else {
                choiceButton.setSelected(false);
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            AppCompatButton button = (AppCompatButton) getChildAt(i);
            if (!mEnabledForMe || (somethingIsSelected && !mAllowReselect) || (button.isSelected() && !mAllowDeselect)) {
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
            }

        }
    }

    public interface OnChoiceClickedListener {
        /**
         * Called when a choice button is clicked.
         *
         * @param choice Metadata of the choice button that was clicked
         * @param selected true if button transitions from unselected -> selected state. False otherwise.
         * @param selectedChoices choice IDs that are currently in a selected state in this set.
         */
        void onChoiceClick(ChoiceMetadata choice, boolean selected, Set<String> selectedChoices);
    }
}
