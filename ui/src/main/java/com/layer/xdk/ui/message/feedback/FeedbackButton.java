package com.layer.xdk.ui.message.feedback;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.view.IconProvider;
import com.layer.xdk.ui.message.view.MessageViewHelper;

/**
 * Feedback button that shows 5 stars to represent a feedback message.
 */
public class FeedbackButton extends LinearLayout implements IconProvider, View.OnClickListener {

    private static final int MAX_RATING = 5;

    private MessageViewHelper mMessageViewHelper;
    private final AppCompatImageButton[] mButtons;
    private int mRating;
    private boolean mEnabled = true;
    private boolean mUpdateStateOnClick;
    private boolean mPerformActionOnClick = true;
    private boolean mAllowClicksWhileDisabled = true;

    private final ColorStateList mEnabledColor;
    private final ColorStateList mDisabledColor;

    private FeedbackMessageModel mMessageModel;

    public FeedbackButton(Context context) {
        this(context, null);
    }

    public FeedbackButton(Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedbackButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMessageViewHelper = new MessageViewHelper(context);

        mEnabledColor = ContextCompat.getColorStateList(getContext(),
                R.color.xdk_ui_feedback_button_enabled);
        mDisabledColor = ContextCompat.getColorStateList(getContext(),
                R.color.xdk_ui_feedback_button_disabled);

        mButtons = new AppCompatImageButton[MAX_RATING];
        int padding = getResources().getDimensionPixelSize(R.dimen.xdk_ui_margin_large);
        TypedValue backgroundValue = new TypedValue();
        context.getTheme()
                .resolveAttribute(R.attr.selectableItemBackgroundBorderless, backgroundValue, true);
        for (int i = 0; i < mButtons.length; i++) {
            AppCompatImageButton button = new AppCompatImageButton(context, attrs, defStyleAttr);
            mButtons[i] = button;
            button.setPadding(padding, padding, padding, padding);
            button.setBackgroundResource(backgroundValue.resourceId);
            button.setOnClickListener(this);
            addView(button);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPerformActionOnClick) {
                    mMessageViewHelper.performAction();
                }
            }
        });

        updateButtonsForState();
    }

    @Override
    public Drawable getIconDrawable() {
        return AppCompatResources.getDrawable(getContext(), R.drawable.xdk_ui_ic_feedback);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mButtons.length; i++) {
            if (v.equals(mButtons[i])) {
                if (mEnabled) {
                    mRating = i + 1;
                }
                onRatingClicked(i);

                break;
            }
        }
    }

    /**
     * Set the message model and update the state based on its data.
     *
     * @param model model this button should represent
     */
    public void setMessageModel(@Nullable FeedbackMessageModel model) {
        mMessageModel = model;
        mMessageViewHelper.setMessageModel(model);
        if (model != null) {
            mEnabled = model.isEditable();
            if (model.getRequestedRating() != null) {
                mRating = model.getRequestedRating();
            } else {
                mRating = model.getRating();
            }

            updateButtonsForState();
        }
    }

    /**
     * Determines if the image states should be updated when clicking the button. Default is false.
     *
     * @param updateStateOnClick true to enable state updating on click, false otherwise
     */
    public void setUpdateStateOnClick(boolean updateStateOnClick) {
        mUpdateStateOnClick = updateStateOnClick;
    }

    /**
     * Determines if the message's action should be invoked when clicking the button. Default is
     * true.
     *
     * @param performActionOnClick true to perform the message's action on click, false otherwise
     */
    public void setPerformActionOnClick(boolean performActionOnClick) {
        mPerformActionOnClick = performActionOnClick;
    }

    /**
     * Determines if clicks should be allowed while the button is disabled. Default is true.
     *
     * @param allowClicksWhileDisabled true to allow clicks when the button is disabled, false
     *                                 otherwise
     */
    public void setAllowClicksWhileDisabled(boolean allowClicksWhileDisabled) {
        mAllowClicksWhileDisabled = allowClicksWhileDisabled;
    }

    /**
     * @return current rating this button is showing
     */
    public int getRating() {
        return mRating;
    }

    private void onRatingClicked(int i) {
        if (mUpdateStateOnClick) {
            updateButtonsForState();
        }
        if (mMessageModel != null && mMessageModel.isEditable()) {
            mMessageModel.setRequestedRating(i + 1);
        }

        if (mPerformActionOnClick) {
            mMessageViewHelper.performAction();
        }
    }

    /**
     * Update the image, tint and enabled state of the buttons
     */
    private void updateButtonsForState() {
        for (int i = 0; i < mButtons.length; i++) {
            ImageButton button = mButtons[i];
            if (i < mRating) {
                button.setImageResource(R.drawable.xdk_ui_ic_feedback_filled);
            } else {
                button.setImageResource(R.drawable.xdk_ui_ic_feedback_hollow);
            }
            ImageViewCompat.setImageTintList(button, mEnabled ? mEnabledColor : mDisabledColor);
            if (!mAllowClicksWhileDisabled) {
                button.setEnabled(mEnabled);
            }
        }
    }
}
