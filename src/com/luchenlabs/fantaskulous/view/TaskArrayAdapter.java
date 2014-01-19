package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskArrayAdapter extends ArrayAdapter<Task> implements ListAdapter, Observer {

    public TaskArrayAdapter(Context context, TaskList taskList, TaskListController controller) {
        super(context, 0, taskList.getTasks());
        taskList.addObserver(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Task item = getItem(position);
        TaskView taskView = (TaskView) convertView;
        if (taskView == null) {
            taskView = new TaskView(getContext(), item);
        } else {
            taskView.setModel(item);
        }
        taskView.setLongClickable(true);
        return taskView;
    }

    public void refresh() {
        // TODO sort(null);
        notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable observable, Object data) {
        refresh();
    }

}
