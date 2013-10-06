package com.luchenlabs.fantaskulous.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskArrayAdapter extends ArrayAdapter<Task> implements ListAdapter {

    public TaskArrayAdapter(Context context, int resource,
            int textViewResourceId, TaskList taskList) {
        super(context, resource, textViewResourceId, taskList.getTasks());
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

}
