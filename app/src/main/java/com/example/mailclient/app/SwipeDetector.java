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

import eu.erikw.PullToRefreshListView;


// Like the name of the class suggests this helps you to detect if you are swiping things.
// It return stuff on the console but one day it will do real things

public class SwipeDetector implements PullToRefreshListView.OnTouchListener {

    public static enum Action {
        LR_TRIGGER,
        LR_BACK,
        RL_TRIGGER,
        RL_BACK,
        CLICK,
        RESET,
        REFRESH,
        None// when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downY, upX, upY, listY;
    private Action mSwipeDetected = Action.None;
    private View cur_item, cur_item_background;
    private float downX = 0;
    private float distanceX = 0;
    private float distanceY = 0;
    private float curX = 0;
    private float curY = 0;
    private int  move_count = 0;
    private float baseX = 0;
    private int screen_width, BLOCK_THRESHOLD, position;
    private boolean scrolling = false;
    private ListView list;

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

                distanceX = 0;
                distanceY = 0;

                curX = 0;

                // get current pressed item
                Rect rect = new Rect();
                list = (ListView) v;
                listY = list.getY();
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
//                        cur_item = child;
//                        Log.i("rootView" , String.valueOf(cur_item.getParent().getClass().getName())); // This returns ListView
                        cur_item = child.findViewById(R.id.list_content); //this is our item content view
                        cur_item_background = child.findViewById(R.id.list_background); // this is our item background
                        break;
                    }
                }
                baseX = cur_item.getX();
                move_count = 0;
                scrolling = false;

                //find the display size
                WindowManager wm = (WindowManager) cur_item.getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                screen_width = size.x;
                BLOCK_THRESHOLD = screen_width/100*35;

                cur_item_background.findViewById(R.id.unpin_icon).setVisibility(View.GONE);
                cur_item_background.findViewById(R.id.pin_icon).setVisibility(View.GONE);
                cur_item_background.findViewById(R.id.delete_icon).setVisibility(View.GONE);

                return false; // allow other events like Click to be processed

            // triggered every finger movement
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                //Relative distance to move the slider
                distanceX = (curX - downX);
                distanceY = (curY - downY);

                //if first move and set scrolling or swiping
                if (move_count == 0) {
                    if (Math.abs(distanceX) < Math.abs(distanceY) ) {
                        scrolling = true;
                    }
                    else {
                        scrolling = false;
                        if (is_todo) {
                            //going leftwards
                            if (distanceX < 0) {

                            }
                            //going rightwards
                            else {
                                //set green background (unpin item background)
                                cur_item_background.setBackgroundResource(R.drawable.unpin_background);
                                cur_item_background.findViewById(R.id.unpin_icon).setVisibility(View.VISIBLE);
                            }
                        } else {
                            //going leftwards
                            if (distanceX < 0) {
                                //set yellow background (pin item background)
                                cur_item_background.setBackgroundResource(R.drawable.pin_background);
                                cur_item_background.findViewById(R.id.pin_icon).setVisibility(View.VISIBLE);
                            }
                            //going rightwards
                            else {
                                //set red background (delete item background)
                                cur_item_background.setBackgroundResource(R.drawable.delete_background);
                                cur_item_background.findViewById(R.id.delete_icon).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                move_count++;
                //scroll disables swipe
                if (scrolling) {
                    // do not handle dispatch -> pass touch event to scroll listener
                    return false;
                }
                else {
                    if (Math.abs(distanceX) < BLOCK_THRESHOLD) {
                        if (is_todo) {
                            //going leftwards
                            if (distanceX < 0) {

                            }
                            //going rightwards
                            else {
                                cur_item.setX(baseX + distanceX);

                            }
                        } else {
                            cur_item.setX(baseX + distanceX);
                        }
                    }
                    // handle touch event -> do not pass to scroll
                    return true;
                }

            case MotionEvent.ACTION_UP:

                upX = event.getX();
                upY = event.getY();

                float deltaX = upX - downX;
                float deltaY = downY - upY;

                if( Math.abs(deltaX) > 25 && !scrolling) {
                    //Swipe action
                    if (Math.abs(deltaX) > BLOCK_THRESHOLD) {
                        if ( deltaX < 0 ) {
                            mSwipeDetected = Action.RL_TRIGGER;
                            Log.i(logTag, "rl trigger");
                        }
                        else {
                            mSwipeDetected = Action.LR_TRIGGER;
                            Log.i(logTag, "lr trigger");
                        }
                    } else {
                        if ( deltaX < 0 ) {
                            mSwipeDetected = Action.RL_BACK;
                            Log.i(logTag, "rl back");
                        }
                        else {
                            mSwipeDetected = Action.LR_BACK;
                            Log.i(logTag, "lr back");
                        }
                    }
                }
                else {
                    if (Math.abs(deltaY) < 25) {
                        mSwipeDetected = Action.CLICK;
                    }
                    else {
                        Log.i(logTag, "none");
                        mSwipeDetected = Action.RESET;
                    }
                }
            getResults();
            return false;
        }
        return false;
    }

    public void getResults() {

    }
}