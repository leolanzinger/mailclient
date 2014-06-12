package com.zapp.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mailclient.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import eu.erikw.PullToRefreshListView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/*
 *  Activity that displays the to do panel
 */
public class TodoFragment extends Fragment {

    public static ArrayList<Email> todo_list;
    static SmoothProgressBar mPocketBar;
    static PullToRefreshListView listView;
    static EmailAdapter adapter;
    Animator animator;
    int list_position, list_visible_position;
    View child_focused;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         *  Instantiate to do array list and its adapter
         */
        todo_list = new ArrayList<Email>();
        setUpTodoList();

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

        adapter = new EmailAdapter(this.getActivity(), R.id.action_bar, todo_list){
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
        if (!MainActivity.first_open) {
            listView.setEmptyView(view.findViewById(R.id.empty_todo));
        }

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
        MainActivity.current_fragment = 1;

        animator = new Animator();

        /*
         * Add touch listener to swipe and unpin
         * email from the listview
         */
        listView.setOnTouchListener(new SwipeDetector(0) {
            @Override
            public void getResults() {
                if (this.swipeDetected()){
                    if (this.getAction().equals(Action.LR_TRIGGER)) {
                        // do the onSwipe action
                        animator.swipeTodo(child_focused, list_position - 1);
                        Email email = Mailbox.emailList.get(Mailbox.emailList.indexOf(todo_list.get(list_position - 1)));
                        email.removeTodo();
                        if (!email.seen) {
                            UpdateSeenMailTask update_task = new UpdateSeenMailTask(getActivity());
                            update_task.execute(email.ID);
                            email.seen = true;
                        }
                    }
                    else if (this.getAction().equals(Action.CLICK)) {
                        // open email
                        Intent intent = new Intent(getActivity(), ReadMail.class);
                        int index = Mailbox.emailList.indexOf(todo_list.get(list_position - 1));
                        intent.putExtra("index", index);
                        intent.putExtra("todo", true);
                        startActivity(intent);
                    }
                    else if (this.getAction().equals(Action.LR_BACK)) {
                        // reset view
                        animator.resetView(listView.getChildAt(list_visible_position));
                    }
                    else if (this.getAction().equals(Action.RL_BACK)) {
                        // reset view
                        animator.resetView(listView.getChildAt(list_visible_position));
                    }
                    else if (this.getAction().equals(Action.SEPARATOR)) {
                        //clicked on a separator -> do nothing
                        Log.d("separator", "succesfully clicked on separator!");
                    }
                    else {

                    }
                }
            }
        });
        Log.i("fragment lifecycle", "onCreateView todo fragment");
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setActionBarTitle("To do");
            ((MainActivity) getActivity()).mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("adapter", "on resume todo");
        setUpTodoList();
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.onDestroy();
    }

    public static void setUnseenHeader(int new_mails_counter) {
        // move unseen emails separator to top
        if (new_mails_counter > 0)  {
            Email new_unseen_separator = todo_list.get(new_mails_counter);
            todo_list.remove(new_mails_counter);
            todo_list.add(0, new_unseen_separator);
        }
    }

    public static void setUpTodoList() {
        todo_list.clear();
        for (int i=0; i<Mailbox.emailList.size(); i++) {
            Mailbox.emailList.get(i).separator = false;
        }
        for (int i=0; i<Mailbox.emailList.size(); i++) {
            if ((Mailbox.emailList.get(i).todo || !Mailbox.emailList.get(i).seen) && (!Mailbox.emailList.get(i).deleted)) {
                todo_list.add(Mailbox.emailList.get(i));
                Collections.sort(todo_list,new Comparator<Email>() {
                    public int compare(Email a, Email b) {
                        if (a.expire_date == null) return 1;
                        else if (b.expire_date == null) return -1;
                        else return a.expire_date.compareTo(b.expire_date);
                    }
                });
            }
        }

        //put unseen email first
        for (int i = 0; i < todo_list.size(); i++) {
            if (!todo_list.get(i).seen) {
                Email moved = todo_list.get(i);
                todo_list.remove(i);
                todo_list.add(0, moved);
            }
        }

        // revisit todo mail list
        boolean continue_reading = true;
        for (int i = 0; i < todo_list.size(); i++) {

            // if first element check this out
            if (i == 0) {
                if (todo_list.get(i).todo) {
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int today = cal.get(Calendar.DAY_OF_MONTH);
                    int email_expire;
                    Email separator = new Email();
                    separator.separator = true;
                    if (todo_list.get(i).expire_date != null) {
                        email_expire  = todo_list.get(i).expire_date.get(Calendar.DAY_OF_MONTH);
                        if (today == email_expire) {
                            separator.subject = "TODAY";
                            todo_list.add(i, separator);
                            Log.d("separator", "today first");
                            i++;
                        } else if (today == email_expire - 1) {
                            separator.subject = "TOMORROW";
                            todo_list.add(i, separator);
                            Log.d("separator", "tomorrow first");
                            i++;
                        } else {
                            if (continue_reading) {
                                separator.subject = "UPCOMING";
                                todo_list.add(i, separator);
                                Log.d("separator", "upcoming first");
                                continue_reading = false;
                                i++;
                            }
                        }
                    }
                    else {
                        if (continue_reading) {
                            separator.subject = "UPCOMING";
                            todo_list.add(i, separator);
                            Log.d("separator", "upcoming first");
                            continue_reading = false;
                            i++;
                        }
                    }
                }
                else {
                    Email separator = new Email();
                    separator.separator = true;

                    separator.subject = "UNREAD EMAILS";
                    todo_list.add(i, separator);
                    Log.d("separator", "today first");
                    i++;
                }
            }
            // if different expire dates insert excerpt
            else {
                if (todo_list.get(i).todo) {
                    int i_date, i_1_date;
                    if (todo_list.get(i).expire_date != null ) {
                        i_date = todo_list.get(i).expire_date.get(Calendar.DAY_OF_MONTH);
                    }
                    else {
                        i_date = -1;
                    }
                    if (todo_list.get(i - 1).expire_date != null) {
                        i_1_date = todo_list.get(i - 1).expire_date.get(Calendar.DAY_OF_MONTH);
                    }
                    else {
                        i_1_date = -2;
                    }
                    if (i_date != i_1_date) {
                        Date date = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        int today = cal.get(Calendar.DAY_OF_MONTH);
                        int email_expire = i_date;

                        Email separator = new Email();
                        separator.separator = true;

                        if (today == email_expire) {
                            separator.subject = "TODAY";
                            todo_list.add(i, separator);
                            i++;
                        } else if (today == email_expire - 1) {
                            separator.subject = "TOMORROW";
                            todo_list.add(i, separator);
                            i++;
                        } else {
                            if (continue_reading) {
                                separator.subject = "UPCOMING";
                                todo_list.add(i, separator);
                                continue_reading = false;
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }
//
//        mDrawerLayout.closeDrawers();
//    }
}