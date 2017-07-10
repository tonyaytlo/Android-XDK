package com.layer.ui.util;

import android.graphics.Typeface;
import android.support.annotation.ColorInt;

public final class ConversationStyle {

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

    private ConversationStyle(Builder builder) {
        mTitleTextColor = builder.titleTextColor;
        mTitleTextStyle = builder.titleTextStyle;
        setTitleTextTypeface(builder.titleTextTypeface);
        mTitleUnreadTextColor = builder.titleUnreadTextColor;
        mTitleUnreadTextStyle = builder.titleUnreadTextStyle;
        setTitleUnreadTextTypeface(builder.titleUnreadTextTypeface);
        mSubtitleTextColor = builder.subtitleTextColor;
        mSubtitleTextStyle = builder.subtitleTextStyle;
        setSubtitleTextTypeface(builder.subtitleTextTypeface);
        mSubtitleUnreadTextColor = builder.subtitleUnreadTextColor;
        mSubtitleUnreadTextStyle = builder.subtitleUnreadTextStyle;
        setSubtitleUnreadTextTypeface(builder.subtitleUnreadTextTypeface);
        mCellBackgroundColor = builder.cellBackgroundColor;
        mCellUnreadBackgroundColor = builder.cellUnreadBackgroundColor;
        setRightAccessoryTextTypeface(builder.rightAccessoryTextTypeface);
        mRightAccessoryTextColor = builder.rightAccessoryTextColor;
        setRightAccessoryUnreadTextTypeface(builder.rightAccessoryUnreadTextTypeface);
        mRightAccessoryUnreadTextColor = builder.rightAccessoryUnreadTextColor;
        mAvatarStyle = builder.avatarStyle;
    }

    public void setTitleTextTypeface(Typeface titleTextTypeface) {
        this.mTitleTextTypeface = titleTextTypeface;
    }

    public void setTitleUnreadTextTypeface(Typeface titleUnreadTextTypeface) {
        this.mTitleUnreadTextTypeface = titleUnreadTextTypeface;
    }

    public void setSubtitleTextTypeface(Typeface subtitleTextTypeface) {
        this.mSubtitleTextTypeface = subtitleTextTypeface;
    }

    public void setSubtitleUnreadTextTypeface(Typeface subtitleUnreadTextTypeface) {
        this.mSubtitleUnreadTextTypeface = subtitleUnreadTextTypeface;
    }

    public void setRightAccessoryTextTypeface(Typeface rightAccessoryTextTypeface) {
        this.mRightAccessoryTextTypeface = rightAccessoryTextTypeface;
    }

    public void setRightAccessoryUnreadTextTypeface(Typeface rightAccessoryUnreadTextTypeface) {
        mRightAccessoryUnreadTextTypeface = rightAccessoryUnreadTextTypeface;
    }

    @ColorInt
    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public int getTitleTextStyle() {
        return mTitleTextStyle;
    }

    public Typeface getTitleTextTypeface() {
        return mTitleTextTypeface;
    }

    @ColorInt
    public int getTitleUnreadTextColor() {
        return mTitleUnreadTextColor;
    }

    public int getTitleUnreadTextStyle() {
        return mTitleUnreadTextStyle;
    }

    public Typeface getTitleUnreadTextTypeface() {
        return mTitleUnreadTextTypeface;
    }

    @ColorInt
    public int getSubtitleTextColor() {
        return mSubtitleTextColor;
    }

    public int getSubtitleTextStyle() {
        return mSubtitleTextStyle;
    }

    public Typeface getSubtitleTextTypeface() {
        return mSubtitleTextTypeface;
    }

    @ColorInt
    public int getSubtitleUnreadTextColor() {
        return mSubtitleUnreadTextColor;
    }

    public int getSubtitleUnreadTextStyle() {
        return mSubtitleUnreadTextStyle;
    }

    public Typeface getSubtitleUnreadTextTypeface() {
        return mSubtitleUnreadTextTypeface;
    }

    @ColorInt
    public int getCellBackgroundColor() {
        return mCellBackgroundColor;
    }

    @ColorInt
    public int getCellUnreadBackgroundColor() {
        return mCellUnreadBackgroundColor;
    }

    public Typeface getRightAccessoryTextTypeface() {
        return mRightAccessoryTextTypeface;
    }

    @ColorInt
    public int getRightAccessoryTextColor() {
        return mRightAccessoryTextColor;
    }

    public Typeface getRightAccessoryUnreadTextTypeface() {
        return mRightAccessoryUnreadTextTypeface;
    }

    @ColorInt
    public int getRightAccessoryUnreadTextColor() {
        return mRightAccessoryUnreadTextColor;
    }

    public AvatarStyle getAvatarStyle() {
        return mAvatarStyle;
    }

    public static final class Builder {
        private int titleTextColor;
        private int titleTextStyle;
        private Typeface titleTextTypeface;
        private int titleUnreadTextColor;
        private int titleUnreadTextStyle;
        private Typeface titleUnreadTextTypeface;
        private int subtitleTextColor;
        private int subtitleTextStyle;
        private Typeface subtitleTextTypeface;
        private int subtitleUnreadTextColor;
        private int subtitleUnreadTextStyle;
        private Typeface subtitleUnreadTextTypeface;
        private int cellBackgroundColor;
        private int cellUnreadBackgroundColor;
        private Typeface rightAccessoryTextTypeface;
        private int rightAccessoryTextColor;
        private Typeface rightAccessoryUnreadTextTypeface;
        private int rightAccessoryUnreadTextColor;
        private AvatarStyle avatarStyle;

        public Builder() {
        }

        public Builder titleTextColor(@ColorInt int val) {
            titleTextColor = val;
            return this;
        }

        public Builder titleTextStyle(int val) {
            titleTextStyle = val;
            return this;
        }

        public Builder titleTextTypeface(Typeface val) {
            titleTextTypeface = val;
            return this;
        }

        public Builder titleUnreadTextColor(@ColorInt int val) {
            titleUnreadTextColor = val;
            return this;
        }

        public Builder titleUnreadTextStyle(int val) {
            titleUnreadTextStyle = val;
            return this;
        }

        public Builder titleUnreadTextTypeface(Typeface val) {
            titleUnreadTextTypeface = val;
            return this;
        }

        public Builder subtitleTextColor(@ColorInt int val) {
            subtitleTextColor = val;
            return this;
        }

        public Builder subtitleTextStyle(int val) {
            subtitleTextStyle = val;
            return this;
        }

        public Builder subtitleTextTypeface(Typeface val) {
            subtitleTextTypeface = val;
            return this;
        }

        public Builder subtitleUnreadTextColor(@ColorInt int val) {
            subtitleUnreadTextColor = val;
            return this;
        }

        public Builder subtitleUnreadTextStyle(int val) {
            subtitleUnreadTextStyle = val;
            return this;
        }

        public Builder subtitleUnreadTextTypeface(Typeface val) {
            subtitleUnreadTextTypeface = val;
            return this;
        }

        public Builder cellBackgroundColor(@ColorInt int val) {
            cellBackgroundColor = val;
            return this;
        }

        public Builder cellUnreadBackgroundColor(@ColorInt int val) {
            cellUnreadBackgroundColor = val;
            return this;
        }

        public Builder rightAccessoryTextTypeface(Typeface val) {
            rightAccessoryTextTypeface = val;
            return this;
        }

        public Builder rightAccessoryTextColor(@ColorInt int val) {
            rightAccessoryTextColor = val;
            return this;
        }

        public Builder rightAccessoryUnreadTextTypeface(Typeface val) {
            rightAccessoryUnreadTextTypeface = val;
            return this;
        }

        public Builder rightAccessoryUnreadTextColor(@ColorInt int val) {
            rightAccessoryUnreadTextColor = val;
            return this;
        }

        public Builder avatarStyle(AvatarStyle val) {
            avatarStyle = val;
            return this;
        }

        public ConversationStyle build() {
            return new ConversationStyle(this);
        }
    }
}
