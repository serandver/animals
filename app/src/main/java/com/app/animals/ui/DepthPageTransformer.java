package com.app.animals.ui;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class DepthPageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.92f;
    private static final float MIN_ALPHA = 0.7f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1) {
            page.setAlpha(0f);
        } else if (position <= 1) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position) * 0.08f);
            float alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position) * 0.3f);

            page.setAlpha(alphaFactor);
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setTranslationX(-position * page.getWidth() * 0.08f);
        } else {
            page.setAlpha(0f);
        }
    }
}