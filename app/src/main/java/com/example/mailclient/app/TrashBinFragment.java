package com.example.mailclient.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import eu.erikw.PullToRefreshListView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/*
 *  Activity that displays the to do panel
 */
public class TrashBinFragment extends Fragment {

    public static ArrayList<Email> trash_email_list;
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
        trash_email_list = new ArrayList<Email>();
        for (int i=0; i<Mailbox.emailList.size(); i++) {
            if (Mailbox.emailList.get(i).deleted) {
                trash_email_list.add(Mailbox.emailList.get(i));
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

        adapter = new EmailAdapter(this.getActivity(), R.id.list_subject, trash_email_list){
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
        MainActivity.current_fragment = 3;

        animator = new Animator();

        /*
         * Add touch listener to swipe and restore
         * deleted mails from trash folder
         */
        listView.setOnTouchListener(new SwipeDetector(2) {
            @Override
            public void getResults() {
                if (this.swipeDetected()) {
                    if (this.getAction().equals(SwipeDetector.Action.LR_TRIGGER)) {
                        Email email = Mailbox.emailList.get(Mailbox.emailList.indexOf(trash_email_list.get(list_position - 1)));
                        email.removeTodo();
                        animator.swipeRestore(child_focused, list_position - 1);
                        Mailbox.emailList.get(Mailbox.emailList.indexOf(trash_email_list.get(list_position - 1))).deleted = false;
                        // update restore
                        UpdateDeletedMailTask update_deleted_task = new UpdateDeletedMailTask(getActivity(), false);
                        update_deleted_task.execute(email.ID);
                    }
                    else if (this.getAction().equals(SwipeDetector.Action.LR_BACK)) {
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
            ((MainActivity) getActivity()).mDrawerLayout.closeDrawers();
        }
    }

}