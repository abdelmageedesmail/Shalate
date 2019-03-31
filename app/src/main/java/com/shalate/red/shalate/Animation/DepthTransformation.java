package com.akarate.red.akarateapp.Animation;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Ahmed on 8/10/2018.
 */

public class DepthTransformation implements ViewPager.PageTransformer{
    // complete toturail link
    // http://www.tellmehow.co/create-viewpager-transformations-animation/
    @Override
    public void transformPage(View page, float position) {
        if (position < -1){    // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);
        }
        else if (position <= 0){    // [-1,0]
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        }
        else if (position <= 1){    // (0,1]
            page.setTranslationX(-position*page.getWidth());
            page.setAlpha(1-Math.abs(position));
            page.setScaleX(1-Math.abs(position));
            page.setScaleY(1-Math.abs(position));
        }
        else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }


}
