package com.zapp.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.mailclient.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.erikw.PullToRefreshListView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/*
 *  Activity that displays the to do panel
 */
public class InboxFragment extends Fragment {

    public static ArrayList<Email> inbox_email_list;
    static SmoothProgressBar mPocketBar;
    static PullToRefreshListView listView;
    static EmailAdapter adapter;
    Animator animator;
    PopupWindow popup, fadePopup;
    View child_focused;
    int list_position, list_visible_position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         *  Instantiate to do array list and its adapter
         */
        inbox_email_list = new ArrayList<Email>();
        for (int i=0; i<Mailbox.emailList.size(); i++) {
            if (!Mailbox.emailList.get(i).deleted) {
                inbox_email_list.add(Mailbox.emailList.get(i));
            }
        }

        View view = inflater.inflate(R.layout.fragment_layout, container, false);

        /*
         *  Instantiate pullable listView
         */
        listView = (PullToRefreshListView) view.findViewById(R.id.listView);
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).receiveMail(mPocketBar);
                }
            }
        });

        adapter = new EmailAdapter(this.getActivity(), R.id.list_subject, inbox_email_list){
            /*
             * Used to get visible position of the list item in the adapter
             * (different from actual position in the list) !
             */
            @Override
            public void processPosition(View view) {
                list_position = listView.getPositionForView(view);
                list_visible_position = list_position - listView.getFirstVisiblePosition();
                child_focused = view;
            }
        };
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty_email));

        /*
         *  Istantiate progress bar and hide it
         */
        mPocketBar = (SmoothProgressBar) view.findViewById(R.id.pocket);
        mPocketBar.setVisibility(View.GONE);
        mPocketBar.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        getResources().getIntArray(R.array.pocket_background_colors),
                        ((SmoothProgressDrawable) mPocketBar.getIndeterminateDrawable()).getStrokeWidth())
        );
        mPocketBar.progressiveStop();

        /*
         *  Notify main activity of fragment
         */
        MainActivity.current_fragment = 2;

        animator = new Animator();

        /*
         * Add touch listener to swipe and unpin
         * email from the listview
         */
        listView.setOnTouchListener(new SwipeDetector(1) {
            @Override
            public void getResults() {
                if (this.swipeDetected()){
                    if (this.getAction().equals(SwipeDetector.Action.RL_TRIGGER)) {
                        /*
                         * Istantiate popup window for pin options
                         */
                        initiatePopupWindow();
                        animator.resetView(listView.getChildAt(list_visible_position));
                    }
                    else if (this.getAction().equals(SwipeDetector.Action.LR_TRIGGER)) {
                        // delete
                        Email email = Mailbox.emailList.get(Mailbox.emailList.indexOf(inbox_email_list.get(list_position - 1)));
                        email.removeTodo();
                        animator.swipeDelete(child_focused, list_position - 1);
                        Mailbox.emailList.get(Mailbox.emailList.indexOf(inbox_email_list.get(list_position - 1))).deleted = true;
                        // update deletion
                        UpdateDeletedMailTask update_deleted_task = new UpdateDeletedMailTask(getActivity(), true);
                        update_deleted_task.execute(email.ID);
                    }
                    else if (this.getAction().equals(SwipeDetector.Action.CLICK)) {
                        // open email
                        Intent intent = new Intent(getActivity(), ReadMail.class);
                        int index = Mailbox.emailList.indexOf(inbox_email_list.get(list_position - 1));
                        intent.putExtra("index", index);
                        intent.putExtra("inbox", true);
                        startActivity(intent);
                    }
                    else if (this.getAction().equals(SwipeDetector.Action.LR_BACK)) {
                        //go back
                        animator.resetView(listView.getChildAt(list_visible_position));
                    }
                    else if (this.getAction().equals(SwipeDetector.Action.RL_BACK)) {
                        //go back
                        animator.resetView(listView.getChildAt(list_visible_position));
                    }
                    else {
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setActionBarTitle("Inbox");
            ((MainActivity) getActivity()).mDrawerLayout.closeDrawers();
        }
    }


    /*
     * Istantiate popup window and set click listeners
     * perform animation after pinning element
     */
    public void initiatePopupWindow() {

        /*
         *  Get window size
         */
        WindowManager wm = (WindowManager) Mailbox.baseContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        /*
         *  First display fade popup to dim the background
         */
        LayoutInflater inflater = (LayoutInflater) MainActivity.baseContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout_fade = inflater.inflate(R.layout.fadepopup,
                (ViewGroup) getActivity().findViewById(R.id.fadePopup));
        fadePopup = new PopupWindow(layout_fade, width, height, true);
        fadePopup.setBackgroundDrawable(new BitmapDrawable());
        fadePopup.showAtLocation(layout_fade, Gravity.NO_GRAVITY, 0, 0);
        fadePopup.setOutsideTouchable(true);
        fadePopup.setFocusable(true);

        /*
         *  Define popup layout and display it
         */
        View layout = inflater.inflate(R.layout.popup, (ViewGroup) getActivity().findViewById(R.id.popup));
        popup = new PopupWindow(layout, (width/4)*3, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);


        /*
         *  Set up popup_list and adapter
         */
        ListView popup_list = (ListView) layout.findViewById(R.id.popup_list);
        String[] popup_array = new String[]{"no reminder", "today", "tomorrow", "pick date"};
        ArrayAdapter<String> popup_Adapter = new ArrayAdapter<String>(getActivity(), R.layout.popup_element, popup_array);
        popup_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Email email = Mailbox.emailList.get(Mailbox.emailList.indexOf(inbox_email_list.get(list_position - 1)));
                switch (position){
                    /*
                     *  No date set for alarm (don't set an alarm)
                     */
                    case 0:
                        email.addTodo(null);
                        adapter.notifyDataSetChanged();
                        break;
                    /*
                     * Alarm set for today at end time
                     */
                    case 1:
                        GregorianCalendar today_date = new GregorianCalendar();
                        // Add email to todolist
                        email.addTodo(today_date);
                        adapter.notifyDataSetChanged();
                        break;
                    /*
                     * Alarm set for tomorrow at start time (reminder message)
                     * and tomorrow at end time (if not completed)
                     */
                    case 2:
                        GregorianCalendar tomorrow_date = new GregorianCalendar();
                        tomorrow_date.add(Calendar.DATE, 1);
                        email.addTodo(tomorrow_date);
                        adapter.notifyDataSetChanged();
                        break;
                    /*
                     *  Alarm set for a date's start time (reminder message)
                     *  and date's end time (if not completed)
                     */
                    case 3:
                        DialogFragment newFragment = new DatePickerFragment(){
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int day){
                                    GregorianCalendar expire_date = new GregorianCalendar(year, month, day);
                                    Mailbox.emailList.get(Mailbox.emailList.indexOf(inbox_email_list.get(list_position - 1))).addTodo(expire_date);
                                }
                        };
                        newFragment.show(getActivity().getFragmentManager(), "datePicker");
                        adapter.notifyDataSetChanged();
                    default:
                        break;
                }
                popup.dismiss();
            }
        });
        popup_list.setAdapter(popup_Adapter);

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                fadePopup.dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.onDestroy();
    }

    @Override
    public void onResume() {
        Log.i("adapter", "on resume");
        adapter.notifyDataSetChanged();
        super.onResume();
    }



}