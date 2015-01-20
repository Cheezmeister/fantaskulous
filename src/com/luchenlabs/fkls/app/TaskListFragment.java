package com.luchenlabs.fkls.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luchenlabs.fkls.G;
import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.model.FklsModel;
import com.luchenlabs.fkls.view.TaskListView;

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
        Log.i(getClass().getSimpleName(), "Created view for list " + this._position); //$NON-NLS-1$

        return v;
    }

    public void refresh() {
        View view = getView();
        if (view == null) {
            Log.e(getClass().getSimpleName(), "No view found!"); //$NON-NLS-1$
            return;
        }
        updateTaskListView((TaskListView) view.findViewById(R.id.taskListView));
    }

    public void updateTaskListView(final TaskListView taskListView) {
        taskListView.invalidate();
        FklsModel model = G.getState().getModel();
        if (model != null) {
            taskListView.setModel(model.taskLists.get(_position));
        }
    }
}
