package com.luchenlabs.fkls.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luchenlabs.fkls.G;
import com.luchenlabs.fkls.R;
import com.luchenlabs.fkls.controller.TaskController;
import com.luchenlabs.fkls.model.Priority;
import com.luchenlabs.fkls.model.Task;
import com.luchenlabs.fkls.model.TaskList;

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

    private ImageView _btnPriority;

    private TextView _textView;

    private CheckBox _checkComplete;

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
        _btnPriority = (ImageView) findViewById(R.id.btnPriority);
        _textView = (TextView) findViewById(R.id.lblDesc);
        _checkComplete = (CheckBox) findViewById(R.id.checkComplete);

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

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        SubMenu sub = menu.addSubMenu("Move");
        for (final TaskList l : G.getState().getModel().taskLists) {
            MenuItem item = sub.add(l.getName());
            item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // FIXME this breaks if multiple contexts are associated.
                    G.getState().getMainController().moveTaskToList(_task, _task.getContexts().first(), l);
                    return true;
                }
            });
        }
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
        String description = _task.getDescription();
        _btnPriority.setImageResource(iconForPriority(_task.getPriority()));
        _btnPriority.setContentDescription(_task.getPriority().toString());
        _textView.setText(description);
        _checkComplete.setChecked(_task.isComplete());
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
