package com.layer.ui.binding;

import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.widget.TextView;

public class Bindings {

    @BindingAdapter({"bind:typeface"})
    public static void setTypeface(TextView textView, Typeface typeface) {
        textView.setTypeface(typeface);
    }
}
