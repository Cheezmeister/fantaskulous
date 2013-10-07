package com.luchenlabs.fantaskulous.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskArrayAdapter;

public class TaskListFragment extends Fragment {

    public static final String ARG_TASKLIST = "tasklist"; //$NON-NLS-1$

    private int _position;

    // // TODO
    // private void editTask(Task task) {
    // android.support.v4.app.FragmentTransaction ft = getFragmentManager()
    // .beginTransaction();
    // EditTaskFragment editTaskFragment = new EditTaskFragment();
    // Bundle args = new Bundle();
    // editTaskFragment.setArguments(args);
    // ft.add(editTaskFragment, "newTask");
    // ft.commit();
    // }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this._position = getArguments().getInt(ARG_TASKLIST);
        View v = inflater.inflate(R.layout.fragment_tasklist, null);
        TaskList taskList = G.getState().getTaskLists().get(_position);
        ((ListView) v.findViewById(R.id.taskListView)).setAdapter(
                new TaskArrayAdapter(getActivity(), R.layout.view_task, 0, taskList));
        return v;
    }

}
