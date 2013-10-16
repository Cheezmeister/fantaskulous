package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.luchenlabs.fantaskulous.R;
import com.luchenlabs.fantaskulous.model.Task;

public class TaskView extends RelativeLayout implements Observer {

    private Task _task;

    public TaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        findViewById(R.id.btnPriority).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // _task.setPriority(priority);
            }
        });
        super.onFinishInflate();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (_task != observable)
            return;

    }

}
