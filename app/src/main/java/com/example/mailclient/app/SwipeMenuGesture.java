package com.example.mailclient.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;


public class SwipeMenuGesture implements View.OnTouchListener {

    private float iniX = 0;
    private float distancePercentage = 0;
    private float distance = 0;
    private float curX = 0;
    private boolean state = false; //false = enable true = disable
    private ImageView lockIcon = null;
    private Context context;
    private float threshold;//soglia a destra
    private Point size = new Point();
    private int threshold_perc = 20;

    public SwipeMenuGesture(Context context) {
        super();
        this.context = context;
        this.lockIcon = lockIcon;

        // stackoverflow oriended: http://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        size.set(size.x, size.y);

        // todo get 74% of size.x and cast to pixels (from dpi)
        threshold = size.y / 100 * threshold_perc;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            distance = 0;
            distancePercentage = 0;
            iniX = (int)event.getX();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            curX = event.getX();
            //Relative distance to move the slider
            distance = (curX - iniX);
            //Move the slider
            if (this.viewPosizionContraints(v, distance)) {
                v.setX(v.getX() + distance);
            }
            //Percentage of slide from origin
            distancePercentage = this.slidePercentage(v);
        }else if(event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL){
            Log.d("Distance", "Snap" + distancePercentage);
            //On release snap the view to the border
            this.borderSnap(v, distance);
            //Check if the the slide is to enable, disable the cell
            this.enableDistableSwipeView(v);
            //Reset values
            iniX = 0;
            distance = 0;
            distancePercentage = 0;
        }

        return true;
    }

    private void enableDistableSwipeView(View v){
        //TODO: settare soglie in base alla grandezza dello schermo
        //TODO: sostituire i colori con quelli definitivi
        //TODO: controllare se l'ordine delle icone � giusto
        Log.d("Dist", ""+(v.getX()+v.getWidth()));
        //Disable swipe view
        if (v.getX()+v.getWidth() < threshold && state == false) {
            state = true;
            v.setBackgroundColor(Color.parseColor("#2F6CD1"));
            //Set the correct lock icon
            if (lockIcon != null) {
                Log.d("Swipe", lockIcon.toString());
                lockIcon.setImageResource(R.drawable.delete);
            }
            //Enable swipe view
        } else if (v.getX()+v.getWidth() < threshold && state == true){
            state = false;
            v.setBackgroundResource(R.drawable.bottom_navbar_background);
            if (lockIcon != null) {
                Log.d("Swipe", lockIcon.toString());
                lockIcon.setImageResource(R.drawable.pin);
            }
        }
        //Save the state in the view
//        COMMENTATO A CASO
//        if(v.getClass() == SettingsCellView.class){
//            ((SettingsCellView)v).setEnable(state);
//        }
    }

    private final float TENSION = 0.8f;

    private void borderSnap(View v, float delta){
        //TODO: stotituire con dimensioni reali e non hardcodate
        if (distancePercentage < 30) {
            v.animate().setInterpolator(new OvershootInterpolator(TENSION))
                    .translationX(0);
        }else if(distancePercentage > 30){
            v.animate().setInterpolator(new OvershootInterpolator(TENSION))
                    .translationX(threshold);
        }
    }


    //check if you are swiping from left to right or vice versa
    private boolean viewPosizionContraints(View v, float delta){
        boolean state = false;
        float position = v.getX();
        //Return the state of the view

//        COMMENTATO A CASO
//        boolean enable = ((SettingsCellView)v).isEnable();
        boolean enable = true;

        if ((position< -size.y/100*threshold_perc && delta < 0) ||
                (position> threshold && delta > 0)){
            state =  false;
            //Disable the left to right swipe gesture if the view is disabled
        }else if((position <= 1 && delta > 0 && enable == true)){
            state =  false;
        }else{
            state = true;
        }
        return state;
    }

    private float slidePercentage(View v){
        return ((v.getX())/size.x)*100;
    }
}

