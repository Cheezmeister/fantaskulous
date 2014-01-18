package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.luchenlabs.fantaskulous.G;
import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskView extends RelativeLayout implements FView<Task>, Observer {

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

    @Override
    public void refresh() {
        ImageView btnPriority = (ImageView) findViewById(R.id.btnPriority);
        TextView textView = (TextView) findViewById(R.id.lblDesc);
        CheckBox checkComplete = (CheckBox) findViewById(R.id.checkComplete);
        EditText fieldDesc = (EditText) findViewById(R.id.fieldTempDesc);

        String description = _task.getDescription();
        btnPriority.setImageResource(iconForPriority(_task.getPriority()));
        btnPriority.setContentDescription(_task.getPriority().toString());
        textView.setText(description);
        fieldDesc.setText(description);
        checkComplete.setChecked(_task.isComplete());
    }

    @Override
    public void setModel(Task m) {
        if (_task == m)
            return;
        Task oldTask = _task;
        _task = m;
        grabController();
        refresh();
        if (oldTask == null)
            hookListeners();
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

    /**
     * Grab the global task controller and stash it in a field
     */
    private void grabController() {
        _controller = G.getState().getTaskController();
    }

    private void hookListeners() {
        final EditText fieldDesc = (EditText) findViewById(R.id.fieldTempDesc);
        final View lblDesc = findViewById(R.id.lblDesc);

        findViewById(R.id.btnPriority).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _controller.cyclePriority(_task);
            }
        });
        lblDesc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditModeState(true);
            }
        });

        fieldDesc.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    _controller.changeDescription(_task, fieldDesc.getText().toString());
                    setEditModeState(false);
                }
                return false;
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

    /**
     * Toggle between displaying a {@link TextView} and {@link EditText} to
     * allow user to edit the title in place.
     * 
     * This is kind of hackish.
     * 
     * @param editing
     */
    private void setEditModeState(boolean editing) {
        findViewById(R.id.lblDesc).setVisibility(editing ? View.GONE : View.VISIBLE);
        EditText editText = (EditText) findViewById(R.id.fieldTempDesc);
        editText.setVisibility(editing ? View.VISIBLE : View.GONE);
        if (editing)
            editText.requestFocus();
    }

    /**
     * Simple map from {@link Priority} to drawable
     * 
     * @param priority
     * @return the priority icon if one exists, else 0
     */
    private static int iconForPriority(Priority priority) {
        switch (priority) {
            case HIGH:
                // case HIGHEST:
                return R.drawable.ic_priority_high;
            case MEDIUM:
                return R.drawable.ic_priority_medium_solid;
            case LOW:
                // case LOWEST:
                return R.drawable.ic_priority_low;
        }
        return 0;
    }

}
