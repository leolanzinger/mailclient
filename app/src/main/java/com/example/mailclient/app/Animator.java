package com.example.mailclient.app;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Giulio on 15/04/14.
 */
public class Animator {
    public Animator() {

    }

    public void resetView(final View v) {
        // reset della view a posizione iniziale
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "translationX", v.getX(), 0);

        anim.setDuration(500);
        anim.start();
    }

    /*
     * Used only for to do items (for now - to be fixed )
     */
    public void swipeToLeft(final View v, final int pos) {
        WindowManager wm = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;

        // elimina la view
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "translationX", v.getX(), screen_width);
        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(android.animation.Animator animator) {

            }

            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                Todo.todo_list.remove(pos);
                Todo.adapter.notifyDataSetChanged();
                v.setX(0);
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animator) {

            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animator) {

            }
        });
        anim.setDuration(500);
        anim.start();
    }
}
