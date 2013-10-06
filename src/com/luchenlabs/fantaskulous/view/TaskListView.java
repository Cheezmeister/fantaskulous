package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListView extends ListView implements Observer {

    private TaskList _taskList;

    public TaskListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTaskList(TaskList taskList) {
        _taskList = taskList;
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if (_taskList != arg0)
            return;

    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see android.widget.ListView#getAdapter()
    // */
    // @Override
    // public ListAdapter getAdapter() {
    // return new TaskArrayAdapter(getContext(), R.layout.fragment_tasklist, 0,
    // _taskList);
    // }

}
