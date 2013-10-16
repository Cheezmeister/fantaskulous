package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskArrayAdapter extends ArrayAdapter<Task> implements ListAdapter, Observer {

    private final LayoutInflater _inflater;
    private final TaskListController _controller;

    public TaskArrayAdapter(Context context, int resource,
            int textViewResourceId, TaskList taskList) {
        super(context, 0, taskList.getTasks());
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _controller = new TaskListController(taskList);
        taskList.addObserver(this);
    }

    public TaskListController getController() {
        return _controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = _inflater.inflate(R.layout.view_task, null);
        // View v = super.getView(position, convertView, parent);
        TextView textView = (TextView) v.findViewById(R.id.titleTextView);
        final Task item = getItem(position);
        String description = item.getDescription();
        textView.setText(description);
        ((ImageView) v.findViewById(R.id.btnPriority)).setImageResource(iconForPriority(item.getPriority()));
        v.findViewById(R.id.btnPriority).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new TaskController(_controller, item).cyclePriority();
            }
        });
        return v;
    }

    private int iconForPriority(Priority priority) {
        switch (priority) {
            case HIGH:
                return R.drawable.ic_priority_high;
            case MEDIUM:
                return R.drawable.ic_priority_medium;
            case LOW:
                return R.drawable.ic_priority_low;
        }
        return 0;
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
