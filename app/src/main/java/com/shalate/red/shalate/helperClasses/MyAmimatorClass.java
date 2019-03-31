package com.shalate.red.shalate.helperClasses;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.shalate.red.shalate.R;


public class MyAmimatorClass {

    public static void ShakeAnimator(Context context, View view)
    {
        final Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.startAnimation(animShake);
    }
}
