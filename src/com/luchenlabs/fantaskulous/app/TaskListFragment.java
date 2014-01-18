package com.luchenlabs.fantaskulous.app;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.TaskList;
import com.luchenlabs.fantaskulous.view.TaskListView;

public class TaskListFragment extends Fragment {

    public static final String ARG_TASKLIST = "tasklist"; //$NON-NLS-1$

    public static final String ARG_IS_NEW = "isnew"; //$NON-NLS-1$

    private int _position;

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
        updateTaskListView((TaskListView) v.findViewById(R.id.taskListView));

        return v;
    }

    public void refresh() {
        Log.v(getClass().getSimpleName(), "refresh");
        updateTaskListView((TaskListView) getView().findViewById(R.id.taskListView));
    }

    public void updateTaskListView(final TaskListView taskListView) {
        Log.v(getClass().getSimpleName(), "updateTaskListView");
        List<TaskList> taskLists = G.getState().getTaskLists();
        if (taskLists != null) {
            Log.v(getClass().getSimpleName(), "setting model for " + taskLists);
            taskListView.setModel(taskLists.get(_position));
        }
    }
}
