package com.shalate.red.shalate.ConstantsFonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Ahmed on 8/8/2018.
 */

@SuppressLint("AppCompatCustomView")
public class RegularEditText extends EditText {
    public RegularEditText(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
     //   Typeface customFont = FontCache.getTypeface("cairo_regular.ttf", context);
        Typeface customFont = FontCache.getTypeface("arb_the_sans_plain.otf", context);
        setTypeface(customFont);
    }
}
