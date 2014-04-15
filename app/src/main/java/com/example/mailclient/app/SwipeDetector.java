package com.example.mailclient.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;


// Like the name of the class suggests this helps you to detect if you are swiping things.
// It return stuff on the console but one day it will do real things

public class SwipeDetector implements ListView.OnTouchListener {

    public static enum Action {
        LR_TRIGGER,
        LR_BACK,
        RL_TRIGGER,
        RL_BACK,
        CLICK,
        None // when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downY, upX, upY;
    private Action mSwipeDetected = Action.None;
    private View cur_item;
    private float downX = 0;
    private float distancePercentage = 0;
    private float distance = 0;
    private float curX = 0;
    private float curY = 0;
    private float baseX = 0;
    private int screen_width, SWIPE_THRESHOLD, BLOCK_THRESHOLD, position;

    public boolean is_todo = false;

    public SwipeDetector(boolean istodo) {
        is_todo = istodo;
    }

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;

                // get current pressed item
                Rect rect = new Rect();
                ListView list = (ListView) v;
                int childCount = list.getChildCount();
                int[] listViewCoords = new int[2];
                v.getLocationOnScreen(listViewCoords);
                int x = (int) event.getRawX() - listViewCoords[0];
                int y = (int) event.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = list.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        position = i;
                        cur_item = child; // This is your down view
                        break;
                    }
                }
                baseX = cur_item.getX();

                //find the display size
                WindowManager wm = (WindowManager) cur_item.getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                screen_width = size.x;
                SWIPE_THRESHOLD = screen_width/100*65;
                BLOCK_THRESHOLD = screen_width/100*35;

                return false; // allow other events like Click to be processed

            // triggered every finger movement
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                //Relative distance to move the slider
                distance = (curX - downX);
                //Add it to starting x position of item
                if (!is_todo) {
                    if (Math.abs(curY - downY) < 25) {
                        if (Math.abs(distance) > BLOCK_THRESHOLD && distance < 0) {
                            //COMMENTO PORTANTE, NON RIMUOVERE
                            //                    Log.i(logTag, "distance: " + distance + " screen width threshold: " + screen_width/100*30);
                        } else {
                            if (Math.abs(distance) < 25) {

                            } else {
                                cur_item.setX(baseX + distance);
                            }
                        }
                    } else {

                    }
                }
                else {
                    if ((baseX + distance) > baseX) {
                        if (Math.abs(curY - downY) < 25) {
                            if (Math.abs(distance) > BLOCK_THRESHOLD && distance < 0) {
                                //COMMENTO PORTANTE, NON RIMUOVERE
                                //                    Log.i(logTag, "distance: " + distance + " screen width threshold: " + screen_width/100*30);
                            } else {
                                if (Math.abs(distance) < 25) {

                                } else {
                                    cur_item.setX(baseX + distance);
                                }
                            }
                        } else {

                        }
                    }
                }

                return false;

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();

                distance = 0;
                curX = 0;

                float deltaX = upX - downX;
                float deltaY = downY - upY;

                if( Math.abs(deltaX) > 25 && Math.abs(deltaY) < 25) {
                    //You can define something to do after a certain threshold
                    if (Math.abs(deltaX) > BLOCK_THRESHOLD) {
                        if ( deltaX < 0 ) {
                            mSwipeDetected = Action.RL_TRIGGER;
                            Log.i(logTag, "rl trigger");
                        }
                        else {
                            mSwipeDetected = Action.LR_TRIGGER;
                            Log.i(logTag, "lr trigger");
                        }
    //                    Log.i(logTag, "il valore e' : " + cur_item.getX());
    //                    //width - deltaX finds how far you have to animate the swipe
    //                    TranslateAnimation anim = new TranslateAnimation(0, screen_width - deltaX, 0, 0);
    //                    anim.setFillAfter(true);
    //                    anim.setDuration(500);
    //                    cur_item.startAnimation(anim);
                    } else {
                        if ( deltaX < 0 ) {
                            mSwipeDetected = Action.RL_BACK;
                            Log.i(logTag, "rl back");
                        }
                        else {
                            mSwipeDetected = Action.LR_BACK;
                            Log.i(logTag, "lr back");
                        }
    //                    Log.i(logTag, "il valore e' : " + cur_item.getX());
    //                    //else you return where you were before
    //                    TranslateAnimation anim = new TranslateAnimation(0, -cur_item.getX(), 0, 0);
    //                    anim.setFillAfter(true);
    //                    anim.setDuration(500);
    //                    cur_item.startAnimation(anim);

                    }
                }
                else {
                    if (Math.abs(deltaY) > 25) {
                        mSwipeDetected = Action.None;
                    }
                    else {
                        Log.i(logTag, "click");
                        mSwipeDetected = Action.CLICK;
                    }
                }
            getResults(position);
            return false;
        }
        return false;
    }

    public void getResults(int position) {

    }
}