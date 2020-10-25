package com.eticum.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.eticum.R;

public class ImageView extends androidx.appcompat.widget.AppCompatImageView {
    public ImageView(Context context) {
        super(context, null, R.attr.collapsedTarget);
    }
    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.collapsedTarget);
    }
}
