package com.luchenlabs.fantaskulous.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.luchenlabs.fantaskulous.controller.TaskListController;
import com.luchenlabs.fantaskulous.model.TaskList;

public class TaskListView extends ListView implements Observer {

    private TaskList _taskList;
    private TaskListController _controller;

    public TaskListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void hookListeners() {
        // View btnAdd = findViewById(R.id.btnAdd);
        // final EditText fieldDescription = (EditText)
        // findViewById(R.id.fieldDescription);
        //
        // btnAdd.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // _controller.addTask(fieldDescription.getText());
        // }
        // });

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ListView#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        // init();
        super.onFinishInflate();
    }

    public void setTaskList(TaskList taskList) {
        if (_taskList != null) {
            _taskList.deleteObserver(this);
        }
        _taskList = taskList;
        _controller = new TaskListController(_taskList);
        _taskList.addObserver(this);
        hookListeners();
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if (_taskList != arg0)
            return;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see android.widget.ListView#getAdapter()
    // */
    // @Override
    // public ListAdapter getAdapter() {
    // return new TaskArrayAdapter(getContext(), R.layout.fragment_tasklist, 0,
    // _taskList);
    // }

}
