package com.example.mailclient.app;

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
        TranslateAnimation anim = new TranslateAnimation(0, -v.getX(), 0, 0);
        anim.setFillAfter(false);
        anim.setDuration(500);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setX(0);
            }
        });

        v.startAnimation(anim);
    }

    public void swipeToLeft(final View v, final int pos) {
        WindowManager wm = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;

        // reset della view a posizione iniziale
        TranslateAnimation anim = new TranslateAnimation(0, screen_width, 0, 0);
        anim.setFillAfter(false);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Todo.todo_list.remove(pos);
                Todo.adapter.notifyDataSetChanged();
                v.setX(0);
            }
        });
        v.startAnimation(anim);
    }
}
