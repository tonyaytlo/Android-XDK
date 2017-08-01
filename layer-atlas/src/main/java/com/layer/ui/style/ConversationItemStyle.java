package com.layer.ui.style;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import com.layer.ui.R;
import com.layer.ui.util.AvatarStyle;

public class ConversationItemStyle extends ItemStyle {

    // Item
    private float mItemHeight;

    // Avatar
    private AvatarStyle mAvatarStyle;
    private int mAvatarVisibility;

    // Title
    private int mTitleTextColor;
    private int mTitleTextStyle;
    private Typeface mTitleTextTypeface;
    private int mTitleUnreadTextColor;
    private int mTitleUnreadTextStyle;
    private Typeface mTitleUnreadTextTypeface;
    private float mTitleTextSize;

    // Subtitle
    private int mSubtitleTextColor;
    private int mSubtitleTextStyle;
    private Typeface mSubtitleTextTypeface;
    private int mSubtitleUnreadTextColor;
    private int mSubtitleUnreadTextStyle;
    private Typeface mSubtitleUnreadTextTypeface;
    private float mSubtitleTextSize;
    private int mSubtitleVisibility;

    // Right Accessory Text
    private Typeface mRightAccessoryTextTypeface;
    private int mRightAccessoryTextColor;
    private Typeface mRightAccessoryUnreadTextTypeface;
    private int mRightAccessoryUnreadTextColor;
    private float mRightAccessoryTextSize;
    private int mRightAccessoryTextVisibility;

    // Cell background
    private int mCellBackgroundColor;
    private int mCellUnreadBackgroundColor;

    // Margins
    private float mMarginHorizontal;
    private float mMarginVertical;

    public ConversationItemStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources resources = context.getResources();

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ConversationItemsListView, R.attr.ConversationItemsListView, defStyle);
        mTitleTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellTitleTextColor, resources.getColor(R.color.layer_ui_text_gray));
        mTitleTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellTitleTextStyle, Typeface.NORMAL);
        String titleTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellTitleTextTypeface);
        mTitleTextTypeface = titleTextTypefaceName != null ? Typeface.create(titleTextTypefaceName, mTitleTextStyle) : null;

        mTitleUnreadTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellTitleUnreadTextColor, resources.getColor(R.color.layer_ui_text_black));
        mTitleUnreadTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellTitleUnreadTextStyle, Typeface.BOLD);
        String titleUnreadTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellTitleUnreadTextTypeface);
        mTitleUnreadTextTypeface = titleUnreadTextTypefaceName != null ? Typeface.create(titleUnreadTextTypefaceName, mTitleUnreadTextStyle) : null;

        mSubtitleTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellSubtitleTextColor, resources.getColor(R.color.layer_ui_text_gray));
        mSubtitleTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellSubtitleTextStyle, Typeface.NORMAL);
        String subtitleTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellSubtitleTextTypeface);
        mSubtitleTextTypeface = subtitleTextTypefaceName != null ? Typeface.create(subtitleTextTypefaceName, mSubtitleTextStyle) : null;
        mSubtitleVisibility = View.VISIBLE;

        mSubtitleUnreadTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellSubtitleUnreadTextColor, resources.getColor(R.color.layer_ui_text_black));
        mSubtitleUnreadTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellSubtitleUnreadTextStyle, Typeface.NORMAL);
        String subtitleUnreadTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellSubtitleUnreadTextTypeface);
        mSubtitleUnreadTextTypeface = subtitleUnreadTextTypefaceName != null ? Typeface.create(subtitleUnreadTextTypefaceName, mSubtitleUnreadTextStyle) : null;

        setCellBackgroundColor(ta.getColor(R.styleable.ConversationItemsListView_cellBackgroundColor, Color.TRANSPARENT));
        setCellUnreadBackgroundColor(ta.getColor(R.styleable.ConversationItemsListView_cellUnreadBackgroundColor, Color.TRANSPARENT));

        setRightAccessoryTextColor(ta.getColor(R.styleable.ConversationItemsListView_rightAccessoryTextColor, resources.getColor(R.color.layer_ui_color_primary_blue)));
        setRightAccessoryUnreadTextColor(ta.getColor(R.styleable.ConversationItemsListView_rightAccessoryUnreadTextColor, resources.getColor(R.color.layer_ui_color_primary_blue)));
        mRightAccessoryTextVisibility = View.VISIBLE;

        AvatarStyle.Builder avatarStyleBuilder = new AvatarStyle.Builder();
        avatarStyleBuilder.avatarTextColor(ta.getColor(R.styleable.ConversationItemsListView_avatarTextColor, resources.getColor(R.color.layer_ui_avatar_text)));
        avatarStyleBuilder.avatarBackgroundColor(ta.getColor(R.styleable.ConversationItemsListView_avatarBackgroundColor, resources.getColor(R.color.layer_ui_avatar_background)));
        avatarStyleBuilder.avatarBorderColor(ta.getColor(R.styleable.ConversationItemsListView_avatarBorderColor, resources.getColor(R.color.layer_ui_avatar_border)));
        mAvatarVisibility = View.VISIBLE;

        float heightMedium = resources.getDimension(R.dimen.layer_ui_item_height_medium);
        float heightSmall = resources.getDimension(R.dimen.layer_ui_item_height_small);
        float heightTiny = resources.getDimension(R.dimen.layer_ui_item_height_tiny);

        mItemHeight = ta.getDimension(R.styleable.ConversationItemsListView_itemHeight, resources.getDimension(R.dimen.layer_ui_item_height_large));

        if (mItemHeight > heightMedium) {
            mMarginVertical = ta.getDimension(R.styleable.ConversationItemsListView_marginVertical, resources.getDimension(R.dimen.layer_ui_margin_large));
            mTitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_title_large);
            mSubtitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_subtitle_large);
            mRightAccessoryTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_right_accessory_text_large);
            avatarStyleBuilder.width(ta.getDimension(R.styleable.ConversationItemsListView_avatarWidth, resources.getDimension(R.dimen.layer_ui_avatar_width_large)));
            avatarStyleBuilder.height(ta.getDimension(R.styleable.ConversationItemsListView_avatarHeight, resources.getDimension(R.dimen.layer_ui_avatar_height_large)));

        } else if (mItemHeight > heightSmall && mItemHeight <= heightMedium) {

            mMarginVertical = ta.getDimension(R.styleable.ConversationItemsListView_marginVertical, resources.getDimension(R.dimen.layer_ui_margin_medium));
            mTitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_title_medium);
            mSubtitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_subtitle_medium);
            mSubtitleVisibility = View.GONE;
            mRightAccessoryTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_right_accessory_text_medium);
            avatarStyleBuilder.width(ta.getDimension(R.styleable.ConversationItemsListView_avatarWidth, resources.getDimension(R.dimen.layer_ui_avatar_width_medium)));
            avatarStyleBuilder.height(ta.getDimension(R.styleable.ConversationItemsListView_avatarHeight, resources.getDimension(R.dimen.layer_ui_avatar_height_medium)));

        } else if (mItemHeight > heightTiny && mItemHeight <= heightSmall) {

            mMarginVertical = ta.getDimension(R.styleable.ConversationItemsListView_marginVertical, resources.getDimension(R.dimen.layer_ui_margin_small));
            mTitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_title_small);
            mSubtitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_subtitle_small);
            mSubtitleVisibility = View.GONE;
            mRightAccessoryTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_right_accessory_text_small);
            avatarStyleBuilder.width(ta.getDimension(R.styleable.ConversationItemsListView_avatarWidth, resources.getDimension(R.dimen.layer_ui_avatar_width_small)));
            avatarStyleBuilder.height(ta.getDimension(R.styleable.ConversationItemsListView_avatarHeight, resources.getDimension(R.dimen.layer_ui_avatar_height_small)));

        } else {

            mAvatarVisibility = View.GONE;
            mSubtitleVisibility = View.GONE;

            mMarginVertical = ta.getDimension(R.styleable.ConversationItemsListView_marginVertical, resources.getDimension(R.dimen.layer_ui_margin_tiny));
            mTitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_title_tiny);
            mSubtitleTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_subtitle_tiny);
            mSubtitleVisibility = View.GONE;
            mRightAccessoryTextSize = resources.getDimension(R.dimen.layer_ui_conversation_item_right_accessory_text_tiny);
            avatarStyleBuilder.width(ta.getDimension(R.styleable.ConversationItemsListView_avatarWidth, resources.getDimension(R.dimen.layer_ui_avatar_width_tiny)));
            avatarStyleBuilder.height(ta.getDimension(R.styleable.ConversationItemsListView_avatarHeight, resources.getDimension(R.dimen.layer_ui_avatar_height_tiny)));
        }

        mMarginHorizontal = ta.getDimension(R.styleable.ConversationItemsListView_marginHorizontal, resources.getDimension(R.dimen.layer_ui_margin_large));

        setAvatarStyle(avatarStyleBuilder.build());
        ta.recycle();
    }

    public float getItemHeight() {
        return mItemHeight;
    }

    @ColorInt
    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public void setTitleTextColor(@ColorInt int titleTextColor) {
        mTitleTextColor = titleTextColor;
    }

    public int getTitleTextStyle() {
        return mTitleTextStyle;
    }

    public void setTitleTextStyle(int titleTextStyle) {
        mTitleTextStyle = titleTextStyle;
    }

    public Typeface getTitleTextTypeface() {
        return mTitleTextTypeface;
    }

    public void setTitleTextTypeface(Typeface titleTextTypeface) {
        mTitleTextTypeface = titleTextTypeface;
    }

    @ColorInt
    public int getTitleUnreadTextColor() {
        return mTitleUnreadTextColor;
    }

    public void setTitleUnreadTextColor(@ColorInt int titleUnreadTextColor) {
        mTitleUnreadTextColor = titleUnreadTextColor;
    }

    public int getTitleUnreadTextStyle() {
        return mTitleUnreadTextStyle;
    }

    public void setTitleUnreadTextStyle(int titleUnreadTextStyle) {
        mTitleUnreadTextStyle = titleUnreadTextStyle;
    }

    public Typeface getTitleUnreadTextTypeface() {
        return mTitleUnreadTextTypeface;
    }

    public void setTitleUnreadTextTypeface(Typeface titleUnreadTextTypeface) {
        mTitleUnreadTextTypeface = titleUnreadTextTypeface;
    }

    @ColorInt
    public int getSubtitleTextColor() {
        return mSubtitleTextColor;
    }

    public void setSubtitleTextColor(@ColorInt int subtitleTextColor) {
        mSubtitleTextColor = subtitleTextColor;
    }

    public int getSubtitleTextStyle() {
        return mSubtitleTextStyle;
    }

    public void setSubtitleTextStyle(int subtitleTextStyle) {
        mSubtitleTextStyle = subtitleTextStyle;
    }

    public Typeface getSubtitleTextTypeface() {
        return mSubtitleTextTypeface;
    }

    public void setSubtitleTextTypeface(Typeface subtitleTextTypeface) {
        mSubtitleTextTypeface = subtitleTextTypeface;
    }

    @ColorInt
    public int getSubtitleUnreadTextColor() {
        return mSubtitleUnreadTextColor;
    }

    public void setSubtitleUnreadTextColor(@ColorInt int subtitleUnreadTextColor) {
        mSubtitleUnreadTextColor = subtitleUnreadTextColor;
    }

    public int getSubtitleUnreadTextStyle() {
        return mSubtitleUnreadTextStyle;
    }

    public void setSubtitleUnreadTextStyle(int subtitleUnreadTextStyle) {
        mSubtitleUnreadTextStyle = subtitleUnreadTextStyle;
    }

    public Typeface getSubtitleUnreadTextTypeface() {
        return mSubtitleUnreadTextTypeface;
    }

    public void setSubtitleUnreadTextTypeface(Typeface subtitleUnreadTextTypeface) {
        mSubtitleUnreadTextTypeface = subtitleUnreadTextTypeface;
    }

    @ColorInt
    public int getCellBackgroundColor() {
        return mCellBackgroundColor;
    }

    public void setCellBackgroundColor(@ColorInt int cellBackgroundColor) {
        mCellBackgroundColor = cellBackgroundColor;
    }

    @ColorInt
    public int getCellUnreadBackgroundColor() {
        return mCellUnreadBackgroundColor;
    }

    public void setCellUnreadBackgroundColor(@ColorInt int cellUnreadBackgroundColor) {
        mCellUnreadBackgroundColor = cellUnreadBackgroundColor;
    }

    public Typeface getRightAccessoryTextTypeface() {
        return mRightAccessoryTextTypeface;
    }

    public void setRightAccessoryTextTypeface(Typeface rightAccessoryTextTypeface) {
        mRightAccessoryTextTypeface = rightAccessoryTextTypeface;
    }

    @ColorInt
    public int getRightAccessoryTextColor() {
        return mRightAccessoryTextColor;
    }

    public void setRightAccessoryTextColor(@ColorInt int rightAccessoryTextColor) {
        mRightAccessoryTextColor = rightAccessoryTextColor;
    }

    public Typeface getRightAccessoryUnreadTextTypeface() {
        return mRightAccessoryUnreadTextTypeface;
    }

    public void setRightAccessoryUnreadTextTypeface(Typeface rightAccessoryUnreadTextTypeface) {
        mRightAccessoryUnreadTextTypeface = rightAccessoryUnreadTextTypeface;
    }

    @ColorInt
    public int getRightAccessoryUnreadTextColor() {
        return mRightAccessoryUnreadTextColor;
    }

    public void setRightAccessoryUnreadTextColor(@ColorInt int rightAccessoryUnreadTextColor) {
        mRightAccessoryUnreadTextColor = rightAccessoryUnreadTextColor;
    }

    public float getMarginHorizontal() {
        return mMarginHorizontal;
    }

    public void setMarginHorizontal(float marginHorizontal) {
        mMarginHorizontal = marginHorizontal;
    }

    public float getMarginVertical() {
        return mMarginVertical;
    }

    public void setMarginVertical(float marginVertical) {
        mMarginVertical = marginVertical;
    }

    public int getSubtitleVisibility() {
        return mSubtitleVisibility;
    }

    public float getTitleTextSize() {
        return mTitleTextSize;
    }

    public void setTitleTextSize(float titleTextSize) {
        mTitleTextSize = titleTextSize;
    }

    public float getSubtitleTextSize() {
        return mSubtitleTextSize;
    }

    public void setSubtitleTextSize(float subtitleTextSize) {
        mSubtitleTextSize = subtitleTextSize;
    }

    public float getRightAccessoryTextSize() {
        return mRightAccessoryTextSize;
    }

    public void setRightAccessoryTextSize(float rightAccessoryTextSize) {
        mRightAccessoryTextSize = rightAccessoryTextSize;
    }

    public int getAvatarVisibility() {
        return mAvatarVisibility;
    }

    public int getRightAccessoryTextVisibility() {
        return mRightAccessoryTextVisibility;
    }

    public AvatarStyle getAvatarStyle() {
        return mAvatarStyle;
    }

    public void setAvatarStyle(AvatarStyle avatarStyle) {
        mAvatarStyle = avatarStyle;
    }
}
