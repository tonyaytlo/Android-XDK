package com.layer.ui.style;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import com.layer.ui.R;
import com.layer.ui.util.AvatarStyle;

public class ConversationItemStyle extends ItemStyle {
    private int mTitleTextColor;
    private int mTitleTextStyle;
    private Typeface mTitleTextTypeface;
    private int mTitleUnreadTextColor;
    private int mTitleUnreadTextStyle;
    private Typeface mTitleUnreadTextTypeface;
    private int mSubtitleTextColor;
    private int mSubtitleTextStyle;
    private Typeface mSubtitleTextTypeface;
    private int mSubtitleUnreadTextColor;
    private int mSubtitleUnreadTextStyle;
    private Typeface mSubtitleUnreadTextTypeface;
    private int mCellBackgroundColor;
    private int mCellUnreadBackgroundColor;
    private Typeface mRightAccessoryTextTypeface;
    private int mRightAccessoryTextColor;
    private Typeface mRightAccessoryUnreadTextTypeface;
    private int mRightAccessoryUnreadTextColor;
    private AvatarStyle mAvatarStyle;

    public ConversationItemStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ConversationItemsListView, R.attr.ConversationItemsListView, defStyle);
        mTitleTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellTitleTextColor, context.getResources().getColor(R.color.layer_ui_text_gray));
        mTitleTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellTitleTextStyle, Typeface.NORMAL);
        String titleTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellTitleTextTypeface);
        mTitleTextTypeface = titleTextTypefaceName != null ? Typeface.create(titleTextTypefaceName, mTitleTextStyle) : null;

        mTitleUnreadTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellTitleUnreadTextColor, context.getResources().getColor(R.color.layer_ui_text_black));
        mTitleUnreadTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellTitleUnreadTextStyle, Typeface.BOLD);
        String titleUnreadTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellTitleUnreadTextTypeface);
        mTitleUnreadTextTypeface = titleUnreadTextTypefaceName != null ? Typeface.create(titleUnreadTextTypefaceName, mTitleUnreadTextStyle) : null;

        mSubtitleTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellSubtitleTextColor, context.getResources().getColor(R.color.layer_ui_text_gray));
        mSubtitleTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellSubtitleTextStyle, Typeface.NORMAL);
        String subtitleTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellSubtitleTextTypeface);
        mSubtitleTextTypeface = subtitleTextTypefaceName != null ? Typeface.create(subtitleTextTypefaceName, mSubtitleTextStyle) : null;

        mSubtitleUnreadTextColor = ta.getColor(R.styleable.ConversationItemsListView_cellSubtitleUnreadTextColor, context.getResources().getColor(R.color.layer_ui_text_black));
        mSubtitleUnreadTextStyle = ta.getInt(R.styleable.ConversationItemsListView_cellSubtitleUnreadTextStyle, Typeface.NORMAL);
        String subtitleUnreadTextTypefaceName = ta.getString(R.styleable.ConversationItemsListView_cellSubtitleUnreadTextTypeface);
        mSubtitleUnreadTextTypeface = subtitleUnreadTextTypefaceName != null ? Typeface.create(subtitleUnreadTextTypefaceName, mSubtitleUnreadTextStyle) : null;

        setCellBackgroundColor(ta.getColor(R.styleable.ConversationItemsListView_cellBackgroundColor, Color.TRANSPARENT));
        setCellUnreadBackgroundColor(ta.getColor(R.styleable.ConversationItemsListView_cellUnreadBackgroundColor, Color.TRANSPARENT));

        setRightAccessoryTextColor(ta.getColor(R.styleable.ConversationItemsListView_rightAccessoryTextColor, context.getResources().getColor(R.color.layer_ui_color_primary_blue)));
        setRightAccessoryUnreadTextColor(ta.getColor(R.styleable.ConversationItemsListView_rightAccessoryUnreadTextColor, context.getResources().getColor(R.color.layer_ui_color_primary_blue)));

        AvatarStyle.Builder avatarStyleBuilder = new AvatarStyle.Builder();
        avatarStyleBuilder.avatarTextColor(ta.getColor(R.styleable.ConversationItemsListView_avatarTextColor, context.getResources().getColor(R.color.layer_ui_avatar_text)));
        avatarStyleBuilder.avatarBackgroundColor(ta.getColor(R.styleable.ConversationItemsListView_avatarBackgroundColor, context.getResources().getColor(R.color.layer_ui_avatar_background)));
        avatarStyleBuilder.avatarBorderColor(ta.getColor(R.styleable.ConversationItemsListView_avatarBorderColor, context.getResources().getColor(R.color.layer_ui_avatar_border)));
        setAvatarStyle(avatarStyleBuilder.build());
        ta.recycle();
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

    public AvatarStyle getAvatarStyle() {
        return mAvatarStyle;
    }

    public void setAvatarStyle(AvatarStyle avatarStyle) {
        mAvatarStyle = avatarStyle;
    }
}
