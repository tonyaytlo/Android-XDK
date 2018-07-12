package com.layer.xdk.ui.testactivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.layer.xdk.ui.composebar.ComposeBar;

public class ComposeBarTestActivity extends Activity {

    private ComposeBar mComposeBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        mComposeBar = new ComposeBar(this);
        setContentView(mComposeBar, layoutParams);
    }

    public ComposeBar getComposeBar() {
        return mComposeBar;
    }
}
