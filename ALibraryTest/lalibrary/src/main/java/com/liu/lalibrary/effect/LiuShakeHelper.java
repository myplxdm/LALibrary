package com.liu.lalibrary.effect;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by liu on 16/4/5.
 */
public class LiuShakeHelper
{

    public static ObjectAnimator attachTo(View view, long duration, boolean isUpDown)
    {
        ObjectAnimator animator;
        if (isUpDown)
        {
            animator = ObjectAnimator.ofFloat(view, "translationX", -30, 30, -20, 20, -10, 10, 0);
        }else
        {
            animator = ObjectAnimator.ofFloat(view, "translationY", -30, 30, -20, 20, -10, 10, 0);
        }
        animator.setDuration(duration);
        return animator;
    }
}
