package com.luchenlabs.fantaskulous.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskArrayAdapter;

public class TaskListFragment extends ListFragment {

    public static final String ARG_TASKLIST = "tasklist"; //$NON-NLS-1$

    private int _position;

    // TODO
    private void editTask(Task task) {
        android.support.v4.app.FragmentTransaction ft = getFragmentManager()
                .beginTransaction();
        EditTaskFragment editTaskFragment = new EditTaskFragment();
        Bundle args = new Bundle();
        editTaskFragment.setArguments(args);
        ft.add(editTaskFragment, "newTask");
        ft.commit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.ListFragment#getListAdapter()
     */
    @Override
    public ListAdapter getListAdapter() {
        TaskList taskList = G.getState().getTaskLists().get(_position);
        return new TaskArrayAdapter(getActivity(), R.layout.view_task, 0, taskList);
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see android.support.v4.app.ListFragment#getListView()
    // */
    // @Override
    // public ListView getListView() {
    // return (ListView) getView().findViewById(R.id.taskListView);
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
        return super.onCreateView(inflater, container, savedInstanceState);
        // View v = inflater.inflate(R.layout.fragment_tasklist, container);
        // return v;
    }
}
