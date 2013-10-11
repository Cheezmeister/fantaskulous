package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskArrayAdapter extends ArrayAdapter<Task> implements ListAdapter, Observer {

    private final LayoutInflater _inflater;

    public TaskArrayAdapter(Context context, int resource,
            int textViewResourceId, TaskList taskList) {
        super(context, 0, taskList.getTasks());
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        taskList.addObserver(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = _inflater.inflate(R.layout.view_task, null);
        // View v = super.getView(position, convertView, parent);
        TextView textView = (TextView) v.findViewById(R.id.titleTextView);
        String description = getItem(position).getDescription();
        textView.setText(description);
        return v;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable observable, Object data) {
        notifyDataSetChanged();
    }

}
