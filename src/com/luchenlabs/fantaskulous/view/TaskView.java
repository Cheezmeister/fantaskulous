package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskView extends RelativeLayout implements FView<Task>, Observer {

    /**
     * Simple map from {@link Priority} to drawable
     *
     * @param priority
     * @return the priority icon if one exists, else 0
     */
    private static int iconForPriority(Priority priority) {
        switch (priority) {
            case HIGHEST:
                return R.drawable.ic_priority_highest;
            case HIGH:
                return R.drawable.ic_priority_high;
            case MEDIUM:
                return R.drawable.ic_priority_medium;
            case LOW:
                return R.drawable.ic_priority_low;
            case LOWEST:
                return R.drawable.ic_priority_lowest;
            case NONE:
            default:
                return R.drawable.ic_priority_none;
        }
    }

    private Task _task;

    private TaskController _controller;

    public TaskView(Context context) {
        super(context);
        _task = null;
        _controller = null;
        init();

    }

    public TaskView(Context context, Task task) {
        super(context);
        init();
        setModel(task);
    }

    /**
     * Grab the global task controller and stash it in a field
     */
    private void grabController() {
        _controller = G.getState().getTaskController();
    }

    private void hookListeners() {

        findViewById(R.id.btnPriority).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _controller.cyclePriority(_task);
            }
        });

        ((CompoundButton) findViewById(R.id.checkComplete)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _controller.complete(_task, isChecked);
            }
        });
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_task, this, true);
        hookListeners();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onAttachedToWindow()
     */
    @Override
    protected void onAttachedToWindow() {
        if (_task != null) {
            _task.addObserver(this);
        }
        super.onAttachedToWindow();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        if (_task != null) {
            _task.deleteObserver(this);
        }
        super.onDetachedFromWindow();
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

    @Override
    public void refresh() {
        ImageView btnPriority = (ImageView) findViewById(R.id.btnPriority);
        TextView textView = (TextView) findViewById(R.id.lblDesc);
        CheckBox checkComplete = (CheckBox) findViewById(R.id.checkComplete);

        String description = _task.getDescription();
        btnPriority.setImageResource(iconForPriority(_task.getPriority()));
        btnPriority.setContentDescription(_task.getPriority().toString());
        textView.setText(description);
        checkComplete.setChecked(_task.isComplete());
    }

    @Override
    public void setModel(Task m) {
        if (_task == m) return;
        Task oldTask = _task;
        _task = m;
        if (oldTask != null) oldTask.deleteObserver(this);
        if (_task != null) _task.addObserver(this);
        grabController();
        refresh();
        if (oldTask == null) hookListeners();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (_task != observable) return;
        refresh();

    }

}
