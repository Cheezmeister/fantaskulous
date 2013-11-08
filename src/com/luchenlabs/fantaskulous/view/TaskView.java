package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import org.junit.Assert;

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

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.controller.TaskController;
import com.luchenlabs.fantaskulous.model.Priority;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskView extends RelativeLayout implements Observer {

    private final Task _task;

    private final TaskController _controller;

    public TaskView(Context context) {
        super(context);
        _task = null;
        _controller = null;
        Assert.fail("Is this call valid?"); //$NON-NLS-1$
        init();

    }

    public TaskView(Context context, Task task, TaskController controller) {
        super(context);
        _task = task;
        _controller = controller;
        init();

    }

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

    private void hookListeners() {
        final EditText fieldDesc = (EditText) findViewById(R.id.fieldTempDesc);
        final View lblDesc = findViewById(R.id.lblDesc);

        findViewById(R.id.btnPriority).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _controller.cyclePriority();
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
                    _controller.changeDescription(fieldDesc.getText().toString());
                    setEditModeState(false);
                }
                return false;
            }
        });
        fieldDesc.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setEditModeState(false);
                }
            }
        });

        ((CompoundButton) findViewById(R.id.checkComplete)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _controller.complete(isChecked);
            }
        });
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_task, this, true);
        hookListeners();
        refresh();
    }

    private void setEditModeState(boolean editing) {
        findViewById(R.id.lblDesc).setVisibility(editing ? View.GONE : View.VISIBLE);
        ((EditText) findViewById(R.id.fieldTempDesc)).setVisibility(editing ? View.VISIBLE : View.GONE);
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
