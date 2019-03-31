package com.shalate.red.shalate.viewModel;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import java.util.Locale;

public class MoreSettingViewModel {

    @BindingAdapter({"app:setGravityView"})
    public static void setGravityView(ImageView imageBack, MoreSettingViewModel model) {
        if (Locale.getDefault().getDisplayLanguage().equals("العربيه")) {
            imageBack.setRotation(imageBack.getRotation() +180);
        }else {
            imageBack.setRotation(imageBack.getRotation() +180);
        }
    }
}
