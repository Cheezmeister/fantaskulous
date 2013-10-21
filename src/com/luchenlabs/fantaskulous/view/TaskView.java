package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskView extends RelativeLayout implements Observer {

    private final Task _task;

    private final TaskController _controller;

    public TaskView(Context context, Task task) {
        super(context);
        _task = task;
        _task.addObserver(this);
        _controller = new TaskController(_task);
        init();

    }

    @Override
    public void update(Observable observable, Object data) {
        if (_task != observable)
            return;
        refresh();

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_task, this, true);
        findViewById(R.id.btnPriority).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _controller.cyclePriority();
            }
        });
        refresh();
    }

    private void refresh() {
        ImageView btnPriority = (ImageView) findViewById(R.id.btnPriority);
        TextView textView = (TextView) findViewById(R.id.titleTextView);

        String description = _task.getDescription();
        textView.setText(description);
        btnPriority.setImageResource(iconForPriority(_task.getPriority()));
    }

    private static int iconForPriority(Priority priority) {
        switch (priority) {
            case HIGH:
                // case HIGHEST:
                return R.drawable.ic_priority_high;
            case MEDIUM:
                return R.drawable.ic_priority_medium;
            case LOW:
                // case LOWEST:
                return R.drawable.ic_priority_low;
        }
        return 0;
    }

}
