package com.example.mailclient.app;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Giulio on 15/04/14.
 */

/*
 *  Class that supports swiping animation
 *  after the finger has performed its movement
 */
public class Animator {
    public Animator() {

    }

    /*
     * Resets the specified view at the original horizontal position
     */
    public void resetView(final View v) {
        View content = v.findViewById(R.id.list_content);
        ObjectAnimator anim = ObjectAnimator.ofFloat(content, "translationX", content.getX(), 0);

        anim.setDuration(500);
        anim.start();
    }

    /*
     * Horizontally swipe away the view from the list
     * THIS IS THE CASE WHERE THE ITEM IS A TODO_ITEM
     */
    public void swipeTodo(final View v, final int pos) {
        final View content = v.findViewById(R.id.list_content);
        WindowManager wm = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;

        //performs the swiping animation
        ObjectAnimator anim = ObjectAnimator.ofFloat(content, "translationX", content.getX(), screen_width);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) {
            }
            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                TodoFragment.todo_list.remove(pos);
                TodoFragment.adapter.notifyDataSetChanged();
                content.setX(0);
                TodoFragment.setUpTodoList();
                TodoFragment.adapter.notifyDataSetChanged();
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

    /*
     * Horizontally swipe away the view from the list
     * THIS IS THE CASE WHERE THE ITEM IS A INBOX_ITEM
     */
    public void swipeDelete(final View v, final int pos) {
        final View content = v.findViewById(R.id.list_content);
        WindowManager wm = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;

        //performs the swiping animation
        ObjectAnimator anim = ObjectAnimator.ofFloat(content, "translationX", content.getX(), screen_width);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) {
            }
            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                InboxFragment.inbox_email_list.remove(pos);
                InboxFragment.adapter.notifyDataSetChanged();
                content.setX(0);
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

    /*
     * Horizontally swipe away the view from the list
     * THIS IS THE CASE WHERE THE ITEM IS A TRASH_ITEM
     */
    public void swipeRestore(final View v, final int pos) {
        final View content = v.findViewById(R.id.list_content);
        WindowManager wm = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;

        //performs the swiping animation
        ObjectAnimator anim = ObjectAnimator.ofFloat(content, "translationX", content.getX(), screen_width);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) {
            }
            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                TrashBinFragment.trash_email_list.remove(pos);
                TrashBinFragment.adapter.notifyDataSetChanged();
                content.setX(0);
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
