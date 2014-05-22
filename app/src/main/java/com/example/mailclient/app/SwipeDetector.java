package com.example.mailclient.app;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import eu.erikw.PullToRefreshListView;


/*
 *  Class that supports the swipe gesture
 *  during finger movement
 */

public class SwipeDetector implements PullToRefreshListView.OnTouchListener {

    /*
     * Bunch of variables used to calc
     * the position and the movement of the
     * swiping tile.
     */

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
    private int screen_width, BLOCK_THRESHOLD;
    private boolean scrolling = false;
    private ListView list;
    private boolean is_pin, is_delete;

    /*
     * Dumb boolean values to distinguish
     * between caller activities
     */
    public boolean is_todo = false;
    public boolean is_inbox = false;
    public boolean is_trash = false;

    /*
     *  Caller activity is recognized by the listener
     *  needed to implement different background animation
     *  activity == 0 -> todolist
     *  activity == 1 -> inboxlist
     *  activity == 2 -> trashlist
     */
    public SwipeDetector(int activity) {
        switch (activity) {
            case 0:
                is_todo = true;
                break;
            case 1:
                is_inbox = true;
                break;
            case 2:
                is_trash = true;
                break;
            default:
                break;
        }
    }

    /*
     * Swipe detection methods
     */
    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }
    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            /*
             * Triggered when finger touches
             * the screen.
             */
            case MotionEvent.ACTION_DOWN:

                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                distanceX = 0;
                distanceY = 0;
                curX = 0;
                is_pin = false;
                is_delete = false;

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
                        cur_item = child.findViewById(R.id.list_content); //this is our item content view
                        cur_item_background = child.findViewById(R.id.list_background); // this is our item background

                        //prevent null pointer - not our case, but who knows what the future will bring to us?
                        if (cur_item != null) {
                            baseX = cur_item.getX();

                            //find the display size
                            WindowManager wm = (WindowManager) MainActivity.baseContext.getSystemService(Context.WINDOW_SERVICE);
                            Display display = wm.getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            screen_width = size.x;
                            BLOCK_THRESHOLD = screen_width/100*35;

                            cur_item_background.findViewById(R.id.unpin_icon).setVisibility(View.GONE);
                            cur_item_background.findViewById(R.id.pin_icon).setVisibility(View.GONE);
                            cur_item_background.findViewById(R.id.delete_icon).setVisibility(View.GONE);
                        }
                        break;
                    }
                }
                move_count = 0;
                scrolling = false;

                return false; // return false to allow other events like Click to be processed!

            /*
             * Triggered when finger performs
             * a movement while touching the screen.
             */
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();

                //Relative distance to move the tile
                distanceX = (curX - downX);
                distanceY = (curY - downY);

                //if first move and set scrolling or swiping environment (one excludes the other)
                if (move_count == 0) {
                    if (Math.abs(distanceX) < Math.abs(distanceY) ) {
                        scrolling = true;
                    }
                    else {
                        scrolling = false;
                        /*
                         *  Now check which is the caller activity
                         *  and then change the background of the
                         *  swiping tile accordingly.
                         */
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
                        }
                        else if (is_inbox) {
                            //going leftwards
                            if (distanceX < 0) {
                                //set yellow background (pin item background)
                                cur_item_background.setBackgroundResource(R.drawable.pin_background);
                                cur_item_background.findViewById(R.id.pin_icon).setVisibility(View.VISIBLE);
                                is_pin = true;

                            }
                            //going rightwards
                            else {
                                //set red background (delete item background)
                                cur_item_background.setBackgroundResource(R.drawable.delete_background);
                                cur_item_background.findViewById(R.id.delete_icon).setVisibility(View.VISIBLE);
                                is_delete = true;
                            }
                        }
                        else if (is_trash) {
                            //going leftwards
                            if (distanceX < 0) {
                            }
                            //going rightwards
                            else {
                                //set green background (unpin item background)
                                cur_item_background.setBackgroundResource(R.drawable.pin_background);
                                cur_item_background.findViewById(R.id.restore_icon).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                move_count++;

                //scroll disables swipe
                if (scrolling) {
                    // if scrolling do not swipe the tiles (pass the touch event to the default ontouch listener
                    return false;
                }
                else {
                    // if not scrolling perform the swiping movement
                    if (Math.abs(distanceX) < BLOCK_THRESHOLD) {
                        if (is_todo) {
                            //going leftwards
                            if (distanceX < 0) {
                            }
                            //going rightwards
                            else {
                                cur_item.setX(baseX + distanceX);
                            }
                        }
                        if (is_trash) {
                            //going leftwards
                            if (distanceX < 0) {
                            }
                            //going rightwards
                            else {
                                cur_item.setX(baseX + distanceX);
                            }
                        }
                        else if (is_inbox) {
                            if (is_pin) {
                                if (distanceX > 0) {

                                }
                                else {
                                    cur_item.setX(baseX + distanceX);
                                }
                            }
                            else if (is_delete) {
                                if (distanceX < 0) {

                                }
                                else {
                                    cur_item.setX(baseX + distanceX);
                                }
                            }
                        }
                    }
                    // handle touch event -> do not pass to scroll (return true)
                    return true;
                }

            /*
             * Triggered when finger leaves
             * the screen.
             */
            case MotionEvent.ACTION_UP:

                upX = event.getX();
                upY = event.getY();
                float deltaX = upX - downX;
                float deltaY = downY - upY;

                if (!scrolling) {
                    if (Math.abs(deltaX) > 0) {
                        /*
                         * if it is the case set the action
                         * variables to the corrispondent
                         * performed movement.
                         */
                        if (Math.abs(deltaX) > BLOCK_THRESHOLD) {
                            if (deltaX < 0) {
                                mSwipeDetected = Action.RL_TRIGGER;
                            } else {
                                mSwipeDetected = Action.LR_TRIGGER;
                            }
                        } else {
                            if (deltaX < 0) {
                                mSwipeDetected = Action.RL_BACK;
                            } else {
                                mSwipeDetected = Action.LR_BACK;
                            }
                        }
                    } else {
                        mSwipeDetected = Action.CLICK;
                    }
                }
                else {
                    mSwipeDetected = Action.None;
                }
            getResults();
            return false;
        }
        return false;
    }

    /*
     *  To be overrided by the activity
     *  that implements the listener
     */
    public void getResults() {

    }
}