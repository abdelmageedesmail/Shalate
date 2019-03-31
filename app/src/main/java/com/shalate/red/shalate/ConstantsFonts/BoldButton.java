package com.shalate.red.shalate.ConstantsFonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Ahmed on 8/8/2018.
 */

@SuppressLint("AppCompatCustomView")
public class BoldButton extends Button {
    public BoldButton(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public BoldButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public BoldButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
      //  Typeface customFont = FontCache.getTypeface("cairo_bold.ttf", context);
        Typeface customFont = FontCache.getTypeface("arb_the_sans_plain.otf", context);
        setTypeface(customFont);
    }
}
